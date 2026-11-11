/*
 * File:          context.rs
 * Path:          v4\src\atenea\player_context\context.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: eb1cbdd704b907bb59d98dc81cb09c96456684d6438861d8ee39c3a6be237141
 *
 */


use crate::atenea::client::AteneaClient;
use crate::atenea::error::{AteneaError, AteneaResult};
use crate::atenea::model::*;

use std::collections::VecDeque;
use redis::AsyncCommands;
use serde::{Deserialize, Serialize};

use skua::all::{GuildChannel, GuildId, MessageId, UserId};
use crate::atenea::model::track::{TrackData, TrackError};
use crate::atenea::player_context::TrackInQueue;
use crate::io::voice::RepeatMode;

#[derive(Clone, Serialize, Deserialize)]
pub struct Filters {
    pub volume: u16,
    pub bass_boost: bool,
    pub spacial: bool, // 8D
    pub night_core: bool,
    pub vapor_wave: bool,
    pub treble: bool,
    pub tremolo: bool,
    pub karaoke: bool,
    pub speed: bool,
    pub pitch: bool
}

impl Default for Filters {
    fn default() -> Self {
        Self {
            volume: 100,
            bass_boost: false,
            spacial: false,
            night_core: false,
            vapor_wave: false,
            treble: false,
            tremolo: false,
            karaoke: false,
            speed: false,
            pitch: false
        }
    }
}

#[derive(Clone, Serialize)]
/// The player context.
pub struct PlayerContext {
    pub guild_id: GuildId,
    pub channel: GuildChannel,
    pub bot_id: UserId,
    pub last_message_id: Option<MessageId>,
    pub repeat_mode: RepeatMode,
    #[serde(skip_serializing)]
    pub client: AteneaClient,
    pub autoplay: bool,

    pub last_track: Option<TrackData>,

    //inner stuff
    pub queue: VecDeque<TrackInQueue>, // THIS
    pub player_data: player::Player, // THIS
    pub last_should_continue: bool,
    pub filters: Filters
}

#[derive(Clone, Serialize, Deserialize)]
/// The player context.
pub struct PlayerContextDummy {
    pub guild_id: GuildId,
    pub channel: GuildChannel,
    pub bot_id: UserId,
    pub last_message_id: Option<MessageId>,
    pub repeat_mode: RepeatMode,
    pub autoplay: bool,

    pub last_track: Option<TrackData>,

    //inner stuff
    pub queue: VecDeque<TrackInQueue>, // THIS
    pub player_data: player::Player, // THIS
    pub last_should_continue: bool,
    pub filters: Filters
}

impl PlayerContext {
    pub fn from_dummy(dummy: PlayerContextDummy, client: AteneaClient) -> Self {
        Self {
            guild_id: dummy.guild_id,
            channel: dummy.channel,
            bot_id: dummy.bot_id,
            last_message_id: dummy.last_message_id,
            repeat_mode: dummy.repeat_mode,
            client,
            autoplay: dummy.autoplay,
            last_track: dummy.last_track,
            queue: dummy.queue,
            player_data: dummy.player_data,
            last_should_continue: dummy.last_should_continue,
            filters: dummy.filters
        }
    }
}

impl PlayerContext {

    async fn queue_init(&mut self) {
        if self.last_should_continue && self.player_data.track.is_none() {
            if let Err(why) = self.skip().await {
                error!(
                    "Error sending skip message in player {}: {}",
                    self.guild_id.get(), why
                );
            }
        }
    }

    /// Close the current player.
    /*pub fn close(self) -> AteneaResult<()> {
        self.tx.send(super::PlayerMessage::Close)?;
        Ok(())
    }*/

    pub async fn pop_queue(&mut self) -> AteneaResult<TrackInQueue> {
        return if let Some(track) = self.queue.pop_front() {
            self.last_track = self.player_data.track.clone();
            self.player_data.track = Some(track.track.clone());
            self.player_data.requester = track.track.requester.clone();
            self.update_redis().await?;
            Ok(track)
        } else {
            Err(AteneaError::TrackError(TrackError {
                message: "No tracks in queue".to_string(),
                severity: "low".to_string(),
                cause: "No tracks in queue".to_string(),
            }))
        }
    }

    pub async fn shuffle_queue(&mut self) -> AteneaResult<()> {
        let mut queue = self.queue.clone();
        let mut new_queue = VecDeque::new();
        while let Some(track) = queue.pop_front() {
            let index = rand::random::<usize>() % (new_queue.len() + 1);
            new_queue.insert(index, track);
        }
        self.queue = new_queue;
        self.update_redis().await?;
        Ok(())
    }

    /// Skip the current track and play the next in the queue.
    pub async fn skip(&mut self) -> AteneaResult<()> {
        if let Some(track) = self.queue.pop_front() {
            self.last_track = self.player_data.track.clone();
            self.player_data.track = Some(track.track.clone());
            match track.track.requester.clone() {
                Some(x) => self.player_data.requester = Some(x),
                None => {}
            }
            self.update_redis().await?;
            let clone = track.clone();
            if let Err(why) = self
                .update_player(&track.into_update_player(), false)
                .await
            {
                error!(
                                    "Error sending update_player request in player {}: {}",
                                    self.guild_id.get(), why
                                );
            }
            if let Err(why) = self.update_track(Some(clone.track)).await {
                error!(
                                    "Error sending update_track request in player {}: {}",
                                    self.guild_id.get(), why
                                );
            }
        } else {
            if let Err(why) = self.stop_now().await {
                error!(
                                    "Error sending stop request in player {}: {}",
                                    self.guild_id.get(), why
                                );
            }
        }

        self.update_redis().await?;
        Ok(())
    }

    /// Finish the current track.
    ///
    /// # Parameters
    ///
    /// - `should_continue`: if the next track in the queue should play.
    pub async fn finish(&mut self, should_continue: bool) -> AteneaResult<()> {
        self.last_should_continue = should_continue;

        if should_continue {
            if let Err(why) = self.skip().await {
                error!(
                                    "Error sending skip message in player {}: {}",
                                    self.guild_id.get(), why
                                );
            }
        }
        Ok(())
    }

    /// Update player data in the context.
    pub async fn update_player_data(&mut self, player: player::Player) -> AteneaResult<()> {
        self.player_data = player;
        self.update_redis().await?;
        Ok(())
    }

    pub(crate) async fn update_track(&mut self, track: Option<TrackData>) -> AteneaResult<()> {
        self.player_data.track = track;
        self.update_redis().await?;
        Ok(())
    }

    pub(crate) async fn update_state(&mut self, state: player::State) -> AteneaResult<()> {
        //self.player_data.track.unwrap().info.position = state.position.clone();
        match self.player_data.track {
            Some(ref mut track) => {
                track.info.position = state.position.clone();
            }
            None => {}
        }

        self.player_data.state = state;
        self.update_redis().await?;

        Ok(())
    }

    /// Add a track to the end of the queue.
    pub async fn queue(&mut self, track: impl Into<TrackInQueue>) -> AteneaResult<()> {
        self.set_queue(super::QueueMessage::PushToBack(track.into())).await
    }

    /// Get the current queue.
    /*pub async fn get_queue(&self) -> AteneaResult<VecDeque<TrackInQueue>> {
        let (tx, rx) = oneshot::channel();

        self.tx.send(super::PlayerMessage::GetQueue(tx))?;

        Ok(rx.await?)
    }*/

    /// Modify the queue in specific ways.
    pub async fn set_queue(&mut self, queue_message: super::QueueMessage) -> AteneaResult<()> {
        self.queue_init().await;

        use super::QueueMessage::*;

        match queue_message {
            PushToBack(track) => {
                self.queue.push_back(track);
            }
            PushToFront(track) => {
                self.queue.push_front(track);
            }
            Insert(index, track) => {
                self.queue.insert(index, track);
            }
            Remove(index) => {
                self.queue.remove(index);
            }
            Clear => {
                self.queue.clear();
            }
            Replace(tracks) => {
                self.queue = tracks;
            }
            Append(mut tracks) => {
                self.queue.append(&mut tracks);
            }
        }

        self.update_redis().await?;
        Ok(())
    }

    /// Get the current player information.
    /*pub async fn get_player(&self) -> AteneaResult<player::Player> {
        let (tx, rx) = oneshot::channel();

        self.tx.send(super::PlayerMessage::GetPlayer(tx))?;

        Ok(rx.await?)
    }*/

    // Sets the repeat mode for the player
    pub async fn set_repeat_mode(&mut self, repeat_mode: RepeatMode) -> AteneaResult<()> {
        self.repeat_mode = repeat_mode;
        self.update_redis().await?;
        Ok(())
    }

    pub async fn set_last_message_id(&mut self, message_id: MessageId) -> AteneaResult<()> {
        self.last_message_id = Some(message_id);
        self.update_redis().await?;
        Ok(())
    }

    /// Request a raw player update.
    pub async fn update_player(
        &mut self,
        update_player: &http::UpdatePlayer,
        no_replace: bool,
    ) -> AteneaResult<player::Player> {
        let node = self.client.get_node_for_guild(self.guild_id);

        let result = node
            .http
            .update_player(
                self.guild_id,
                &node.session_id.load(),
                update_player,
                no_replace,
            )
            .await?;

        self.player_data = result.clone();
        self.update_redis().await?;

        Ok(result)
    }

    /// Force play a track, replacing the current track.
    ///
    /// NOTE: Does not modify the queue.
    pub async fn play_now(&mut self, track: &TrackData) -> AteneaResult<player::Player> {
        self.update_player(
            &http::UpdatePlayer {
                encoded_track: Some(track.encoded.to_string()),
                position: Some(track.info.position),
                ..Default::default()
            },
            false,
        )
        .await
    }

    /// Stop playing the current track.
    ///
    /// This does not continue playback of the queue.
    pub async fn stop_now(&mut self) -> AteneaResult<player::Player> {
        let node = self.client.get_node_for_guild(self.guild_id);

        let path = format!(
            "/v4/sessions/{}/players/{}",
            &node.session_id.load(),
            self.guild_id.get()
        );

        let result = node
            .http
            .raw_request(
                reqwest::Method::PATCH,
                path,
                &serde_json::json!({"encodedTrack": null}),
            )
            .await?;

        let player = serde_json::from_value::<crate::atenea::error::RequestResult<player::Player>>(result)?
            .to_result()?;

        self.player_data = player.clone();

        self.update_redis().await?;
        Ok(player)
    }

    /// Set the pause state of the player.
    pub async fn set_pause(&mut self, pause: bool) -> AteneaResult<player::Player> {
        self.update_player(
            &http::UpdatePlayer {
                paused: Some(pause),
                ..Default::default()
            },
            true,
        )
        .await
    }

    /// Set the volume of the player.
    pub async fn set_volume(&mut self, mut volume: u16) -> AteneaResult<player::Player> {
        volume = volume.min(1000);

        self.update_player(
            &http::UpdatePlayer {
                volume: Some(volume),
                ..Default::default()
            },
            true,
        )
        .await
    }

    /// Set the filters of the player.
    pub async fn set_filters(&mut self, filters: Filters) -> AteneaResult<player::Player> {
        self.filters = filters.clone();
        self.update_redis().await?;

        let audio_filters: player::Filters = filters.into();
        self.update_player(
            &http::UpdatePlayer {
                filters: Some(audio_filters),
                ..Default::default()
            },
            true,
        )
        .await
    }
    
    pub async fn commit_filters(&mut self) -> AteneaResult<player::Player> {
        let audio_filters: player::Filters = self.filters.clone().into();
        self.update_player(
            &http::UpdatePlayer {
                filters: Some(audio_filters),
                ..Default::default()
            },
            true,
        )
            .await
    }

    pub async fn toggle_filter(&mut self, filter: &str) -> AteneaResult<player::Player> {
        match filter {
            "bass_boost" => {
                self.filters.bass_boost = !self.filters.bass_boost;
                if self.filters.bass_boost {
                    self.filters.treble = false;
                }
            },
            "8d" => self.filters.spacial = !self.filters.spacial,
            "night_core" => {
                self.filters.night_core = !self.filters.night_core;
                self.filters.vapor_wave = false;
                self.filters.speed = false;
                self.filters.pitch = false;
            },
            "vapor_wave" => {
                self.filters.vapor_wave = !self.filters.vapor_wave;
                self.filters.night_core = false;
                self.filters.speed = false;
                self.filters.pitch = false;
            },
            "treble" => {
                self.filters.treble = !self.filters.treble;
                self.filters.bass_boost = false;
            },
            "tremolo" => self.filters.tremolo = !self.filters.tremolo,
            "karaoke" => self.filters.karaoke = !self.filters.karaoke,
            "speed" => {
                self.filters.speed = !self.filters.speed;
                self.filters.night_core = false;
                self.filters.vapor_wave = false;
                self.filters.pitch = false;
            },
            "pitch" => {
                self.filters.pitch = !self.filters.pitch;
                self.filters.night_core = false;
                self.filters.vapor_wave = false;
                self.filters.speed = false;
            },
            _ => {}
        }
        
        self.filters.volume = self.player_data.volume;
        
        self.update_redis().await?;
        self.commit_filters().await
    }

    /// Jump to a specific position in the currently playing track.
    pub async fn set_position(
        &mut self,
        position: std::time::Duration,
    ) -> AteneaResult<player::Player> {
        self.update_player(
            &http::UpdatePlayer {
                position: Some(position.as_millis() as u64),
                ..Default::default()
            },
            true,
        )
        .await
    }

    pub async fn update_redis(
        &self,
    ) -> AteneaResult<()> {
        let mut redis = self.client.cache.clone();
        let key = format!("player:{}:{}", self.bot_id, self.guild_id);
        let player_info = serde_json::to_string(self).unwrap();
        let _: () = redis.set_ex(&key, player_info, 3600).await.unwrap();
        Ok(())
    }

    pub async fn delete_redis(
        &self,
    ) -> AteneaResult<()> {
        let mut redis = self.client.cache.clone();
        let key = format!("player:{}:{}", self.bot_id, self.guild_id);
        let _: () = redis.del(&key).await.unwrap();
        Ok(())
    }
}
