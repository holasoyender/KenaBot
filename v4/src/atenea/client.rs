/*
 * File:          client.rs
 * Path:          v4\src\atenea\client.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: 9964d82a40fb82022eb0a73fbaa261d85deeb48b4c7852197e89cd4d6c7b7463
 *
 */


use crate::atenea::error::AteneaResult;
use crate::atenea::model::*;
use crate::atenea::node;
use crate::atenea::player_context::*;

use std::collections::VecDeque;
use std::sync::atomic::{AtomicBool, Ordering};
use std::sync::Arc;

use arc_swap::ArcSwap;
use reqwest::{header::HeaderMap, Client as ReqwestClient};
use skua::all::{GuildChannel, GuildId, Http, UserId};
use redis::aio::ConnectionManager as RedisConnection;
use mongodb::Database;
use redis::AsyncCommands;
use crate::io::voice::RepeatMode;
use crate::locale::manager::LanguageManager;

#[derive(Clone)]
/// The main client, where everything gets done, from events to requests to management.
pub struct AteneaClient {
    pub nodes: Arc<Vec<node::Node>>,
    pub events: events::Events,
    pub user_data: Arc<parking_lot::RwLock<typemap_rev::TypeMap>>,
    pub cache: RedisConnection,
    pub db: Database,
    pub stats_db: Database,
    pub http: Arc<Http>,
    pub user_id: UserId,
    pub language_manager: LanguageManager
}

impl AteneaClient {
    /// Create a new Atenea Client.
    ///
    /// # Parameters
    ///
    /// - `events`: The atenea event handler.
    /// - `nodes`: List of nodes to connect to.
    pub fn new(events: events::Events, nodes: Vec<node::NodeBuilder>, redis: RedisConnection, db: Database, stats_db: Database, user_id: UserId, http: Arc<Http>, language_manager: LanguageManager) -> AteneaClient {
        let mut built_nodes = Vec::new();

        for (idx, i) in nodes.into_iter().enumerate() {
            let mut headers = HeaderMap::new();
            headers.insert("Authorization", i.password.parse().unwrap());
            headers.insert("User-Id", i.user_id.to_string().parse().unwrap());

            if let Some(session_id) = &i.session_id {
                headers.insert("Session-Id", session_id.parse().unwrap());
            }

            headers.insert(
                "Client-Name",
                format!("{}/{}", env!("CARGO_PKG_NAME"), env!("CARGO_PKG_VERSION"))
                    .to_string()
                    .parse()
                    .unwrap(),
            );

            let rest_client = ReqwestClient::builder()
                .default_headers(headers)
                .build()
                .unwrap();

            let node = if i.is_ssl {
                let http = crate::atenea::http::Http {
                    rest_address: format!("https://{}/v4", i.hostname),
                    rest_address_versionless: format!("https://{}", i.hostname),
                    rest_client,
                };

                node::Node {
                    id: idx,
                    websocket_address: format!("wss://{}/v4/websocket", i.hostname),
                    http,
                    events: i.events,
                    password: i.password,
                    user_id: i.user_id,
                    is_running: AtomicBool::new(false),
                    session_id: ArcSwap::new(if let Some(id) = i.session_id {
                        id.into()
                    } else {
                        idx.to_string().into()
                    }),
                }
            } else {
                let http = crate::atenea::http::Http {
                    rest_address: format!("http://{}/v4", i.hostname),
                    rest_address_versionless: format!("http://{}", i.hostname),
                    rest_client,
                };

                node::Node {
                    id: idx,
                    websocket_address: format!("ws://{}/v4/websocket", i.hostname),
                    http,
                    events: i.events,
                    password: i.password,
                    user_id: i.user_id,
                    is_running: AtomicBool::new(false),
                    session_id: ArcSwap::new(if let Some(id) = i.session_id {
                        id.into()
                    } else {
                        idx.to_string().into()
                    }),
                }
            };

            built_nodes.push(node);
        }

        AteneaClient {
            nodes: Arc::new(built_nodes),
            //players: Arc::new(DashMap::new()),
            //senders: Arc::new(DashMap::new()),
            events,
            user_data: Arc::new(parking_lot::RwLock::new(typemap_rev::TypeMap::new())),
            cache: redis,
            db,
            stats_db,
            user_id,
            http,
            language_manager
        }
    }

    /// Establish the connection(s) and start listening for events.
    pub async fn start(&self) {
        for node in &*self.nodes {
            if let Err(why) = node.connect(self.clone()).await {
                error!("Failed to connect to the atenea websocket: {}", why);
            }
        }

        let atenea_client = self.clone();

        tokio::spawn(async move {
            loop {
                tokio::time::sleep(std::time::Duration::from_secs(5)).await;

                for node in &*atenea_client.nodes {
                    if !node.is_running.load(Ordering::Relaxed) {
                        if let Err(why) = node.connect(atenea_client.clone()).await {
                            error!("Failed to connect to the atenea websocket: {}", why);
                        }
                    }
                }
            }
        });
    }

    /// Get the node assigned to a guild.
    pub fn get_node_for_guild(&self, guild_id: impl Into<GuildId>) -> &node::Node {
        let guild_id = guild_id.into();

        self.nodes
            .get(guild_id.get() as usize % self.nodes.len())
            .unwrap()
    }

    /// Get the player context for a guild, if it exists.
    pub async fn get_player_context(&self, guild_id: impl Into<GuildId>) -> Option<PlayerContext> {
        let guild_id = guild_id.into();
        let mut redis = self.cache.clone();
        let key = format!("player:{}:{}", self.user_id, guild_id);

        let player_data: Option<String> = redis.get(&key).await.unwrap();

        return if let Some(raw_player_context) = player_data {
            let player_context: PlayerContextDummy = serde_json::from_str(&raw_player_context).unwrap();
            Some(PlayerContext::from_dummy(player_context, self.clone()))
        } else {
            None
        }

    }

    /// Creates a new player without a context.
    ///
    /// Calling this method is required to play tracks on a guild.
    pub async fn create_player(
        &self,
        guild_id: impl Into<GuildId>,
        connection_info: impl Into<player::ConnectionInfo>,
    ) -> AteneaResult<player::Player> {
        let guild_id = guild_id.into();
        let mut connection_info = connection_info.into();
        connection_info.fix();

        let node = self.get_node_for_guild(guild_id);

        let player = node
            .http
            .update_player(
                guild_id,
                &node.session_id.load(),
                &http::UpdatePlayer {
                    voice: Some(connection_info.clone()),
                    ..Default::default()
                },
                true,
            )
            .await?;

        Ok(player)
    }

    /// Creates a new player with context.
    ///
    /// Calling this method is required to create the initial player, and be able to use the built-in queue.
    pub async fn create_player_context(
        &self,
        guild_id: impl Into<GuildId>,
        connection_info: impl Into<player::ConnectionInfo>,
        channel: GuildChannel,
        autoplay: bool
    ) -> AteneaResult<PlayerContext> {
        let guild_id = guild_id.into();
        let mut connection_info = connection_info.into();
        connection_info.fix();

        let node = self.get_node_for_guild(guild_id);

        if let Some(x) = self.get_player_context(guild_id).await {
            return Ok(x.clone());
        }

        let player = node
            .http
            .update_player(
                guild_id,
                &node.session_id.load(),
                &http::UpdatePlayer {
                    voice: Some(connection_info.clone()),
                    ..Default::default()
                },
                true,
            )
            .await?;

        let player_context = PlayerContext {
            guild_id,
            client: self.clone(),
            channel,
            last_message_id: None,
            last_track: None,
            bot_id: node.user_id,
            autoplay,
            repeat_mode: RepeatMode::None,

            queue: VecDeque::new(),
            player_data: player,
            last_should_continue: true,
            filters: Default::default(),
        };

        //self.players.insert(guild_id, player_context.clone());
        let mut redis = self.cache.clone();
        let key = format!("player:{}:{}", self.user_id, guild_id);
        let raw_player_context = serde_json::to_string(&player_context).unwrap();
        let _: () = redis.set_ex(&key, raw_player_context, 3600).await.unwrap();
        
        Ok(player_context)
    }

    /// Deletes and closes a specific player context, if it exists.
    pub async fn delete_player(&self, guild_id: impl Into<GuildId>) -> AteneaResult<()> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        //if let Some((_, player)) = self.players.remove(&guild_id) {
        //    player.close()?;
        //}

        let mut redis = self.cache.clone();
        let key = format!("player:{}:{}", self.user_id, guild_id);
        let _: () = redis.del(&key).await.unwrap();

        node.http
            .delete_player(guild_id, &node.session_id.load())
            .await?;

        Ok(())
    }

    /// Deletes all stored player contexts.
    ///
    /// This is useful to put on the ready event, to close already open players in case the
    /// Atenea server restarts.
    pub async fn delete_all_player_contexts(&self) -> AteneaResult<()> {
        //for guild_id in self.players.iter().map(|i| i.guild_id).collect::<Vec<_>>() {
        //    self.delete_player(guild_id).await?;
        //}

        let mut redis = self.cache.clone();
        let keys: Vec<String> = redis.keys(format!("player:{}:*", self.user_id)).await.unwrap();
        for key in keys {
            let _: () = redis.del(&key).await.unwrap();
        }

        Ok(())
    }

    /// Request a raw player update.
    pub async fn update_player(
        &self,
        guild_id: impl Into<GuildId>,
        update_player: &http::UpdatePlayer,
        no_replace: bool,
    ) -> AteneaResult<player::Player> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node
            .http
            .update_player(guild_id, &node.session_id.load(), update_player, no_replace)
            .await?;

        if let Some(mut player) = self.get_player_context(guild_id).await {
            player.update_player_data(result.clone()).await?;
        }

        Ok(result)
    }

    /// Resolves audio tracks for use with the `update_player` endpoint.
    ///
    /// # Parameters
    ///
    /// - `identifier`: A track identifier.
    ///  - Can be a url: "https://youtu.be/watch?v=DrM2lo6B04I"
    ///  - A unique identifier: "DrM2lo6B04I"
    ///  - A search: "
    pub async fn load_tracks(
        &self,
        guild_id: impl Into<GuildId>,
        identifier: &str,
    ) -> AteneaResult<track::Track> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.load_tracks(identifier).await?;

        Ok(result)
    }

    /// Decode a single track into its info.
    ///
    /// # Parameters
    ///
    /// - `track`: base64 encoded track data.
    pub async fn decode_track(
        &self,
        guild_id: impl Into<GuildId>,
        track: &str,
    ) -> AteneaResult<track::TrackData> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.decode_track(track).await?;

        Ok(result)
    }

    /// Decode multiple tracks into their info.
    ///
    /// # Parameters
    ///
    /// - `tracks`: base64 encoded tracks.
    pub async fn decode_tracks(
        &self,
        guild_id: impl Into<GuildId>,
        tracks: &[String],
    ) -> AteneaResult<Vec<track::TrackData>> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.decode_tracks(tracks).await?;

        Ok(result)
    }

    /// Request Atenea server version.
    pub async fn request_version(&self, guild_id: impl Into<GuildId>) -> AteneaResult<String> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.version().await?;

        Ok(result)
    }

    /// Request Atenea statistics.
    ///
    /// NOTE: The frame stats will never be returned.
    pub async fn request_stats(
        &self,
        guild_id: impl Into<GuildId>,
    ) -> AteneaResult<events::Stats> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.stats().await?;

        Ok(result)
    }

    /// Request Atenea server information.
    pub async fn request_info(&self, guild_id: impl Into<GuildId>) -> AteneaResult<http::Info> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.info().await?;

        Ok(result)
    }

    /// Returns the player for the guild.
    pub async fn request_player(
        &self,
        guild_id: impl Into<GuildId>,
    ) -> AteneaResult<player::Player> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node
            .http
            .get_player(guild_id, &node.session_id.load())
            .await?;

        Ok(result)
    }

    /// Returns all players from the Node bound to the guild.
    pub async fn request_all_players(
        &self,
        guild_id: impl Into<GuildId>,
    ) -> AteneaResult<Vec<player::Player>> {
        let guild_id = guild_id.into();
        let node = self.get_node_for_guild(guild_id);

        let result = node.http.get_players(&node.session_id.load()).await?;

        Ok(result)
    }
}
