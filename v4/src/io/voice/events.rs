/*
 * File:          events.rs
 * Path:          v4\src\io\voice\events.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 0e576ea9c1df0705be6d28eebf4eab2203633c0f2239f18713b8df9d90f82e1a
 *
 */


use mongodb::bson::doc;
use redis::AsyncCommands;
use skua::all::{ButtonStyle, CreateActionRow, CreateButton, CreateEmbed, CreateEmbedAuthor, CreateMessage, EditMessage};
use shared::images::IMAGES;
use crate::atenea::client::AteneaClient;
use crate::atenea::error::AteneaResult;
use crate::atenea::model::BoxFuture;
use crate::atenea::model::events::{PlayerUpdate, Ready, Stats, TrackEnd, TrackException, TrackStart, TrackStuck, WebSocketClosed};
use crate::atenea::model::track::TrackData;
use crate::atenea::player_context::{QueueMessage};
use crate::database::cache::node::save_node_stats;
//use crate::database::cache::queue::PlayerInfo;
use crate::database::schema::guild::Guild;
use crate::io::voice::RepeatMode;
use crate::util::string::{format_song_title, player_timestamp};

/*pub fn raw(client: AteneaClient, session_id: String, event: &serde_json::Value) -> BoxFuture<()> {
    info!("Raw event: {}", event);
    return Box::pin(async move {});
}*/

/// Dispatched by Atenea upon successful connection and authorization.
pub fn ready(_: AteneaClient, session_id: String, _: &Ready) -> BoxFuture<()> {
    info!("Node '{}' is ready!", session_id);
    return Box::pin(async move {});
}

/// Dispatched periodically with the current state of a player.
pub fn player_update(client: AteneaClient, _: String, event: &PlayerUpdate) -> BoxFuture<()> {
    return Box::pin(async move {

        if let Some(mut player) = client.get_player_context(event.guild_id).await {
            player.update_state(event.state.clone()).await.unwrap();
            player.update_redis().await.unwrap();
        };

        /*let player_data = PlayerInfo::get(event.guild_id, client.user_id, &client.cache).await;
        if let Some(mut player_data) = player_data {
            player_data.position = event.state.position;
            player_data.save(&client.cache).await;
        }*/
    });
}

/// A collection of statistics sent every minute.
pub fn stats(client: AteneaClient, session_id: String, event: &Stats) -> BoxFuture<()> {
    return Box::pin(async move {
        save_node_stats(client.user_id, &client.cache, session_id, event).await;
    });
}

/// Dispatched when a track starts playing.
pub fn track_start(client: AteneaClient, _: String, event: &TrackStart) -> BoxFuture<()> {
    return Box::pin(async move {

        debug!("Starting track: {:?}", event.track);

        //let player_data = PlayerInfo::get(event.guild_id, client.user_id, &client.cache).await;
        let player = client.get_player_context(event.guild_id).await;
        let config = client.db.collection::<Guild>("Guilds").find_one(doc! {"guildID": event.guild_id.to_string()}, None).await.unwrap().unwrap();
        let lang = client.language_manager.get_language_or_default_str(&config.lang).clone();

        if let Some(mut player_data) = player {

            if player_data.repeat_mode == RepeatMode::Queue {
                if let Some(ref last_track) = player_data.last_track {
                    let mut new_track = last_track.clone();
                    new_track.info.position = 0;

                    debug!("Adding track to queue: {:?}", new_track);
                    player_data.set_queue(QueueMessage::Append(vec![new_track.clone().into()].into())).await.unwrap();
                }
            }

            let mut track = match player_data.player_data.track.clone() {
                None => {
                    let player_update = client.get_player_context(event.guild_id).await.unwrap().player_data;
                    match player_update.track {
                        None => {
                            return
                        }
                        Some(x) => x
                    }
                }
                Some(x) => x
            };

            // check for mismatched queues (race condition) TODO: This doesnt work (the entire loop thing)
            if track.info.uri != event.track.info.uri { // 1: The current track is not the playing track
                debug!("Mismatched queues, trying to find the correct track");

                ////
                // Check if the last track is the correct one
                match player_data.last_track.clone() {
                    None => { // 2: The last track is not the playing track
                        match player_data.pop_queue().await {
                            Ok(nt) => {
                                if nt.track.info.uri == event.track.info.uri { // The next track is the playing track
                                    player_data = client.get_player_context(event.guild_id).await.unwrap();
                                    track = nt.track;
                                } else {
                                    player_data.set_queue(QueueMessage::Append(vec![nt.track.into()].into())).await.unwrap();
                                }
                            }
                            Err(_) => {
                                error!("Failed to pop queue, no matching track found for the playing track");
                            }
                        }
                    }
                    Some(lt) => { // 2: The last track could be the playing track
                        if event.track.info.uri == lt.info.uri { // The last track is the playing track
                            track = lt.clone();
                            player_data.set_queue(QueueMessage::Append(vec![track.clone().into()].into())).await.unwrap();
                        }
                    }
                }
            }

            let queue = player_data.queue.clone();
            let explicit = track.info.title.starts_with("[EXPLICIT] ");

            let image = match track.info.source_name.as_str() {
                "spotify" => track.info.artwork_url.unwrap_or(IMAGES.no_image.to_string()),
                "soundcloud" => IMAGES.soundcloud.to_string(),
                "deezer" => IMAGES.deezer.to_string(),
                "applemusic" => IMAGES.apple_music.to_string(),
                "tiktok" => IMAGES.tik_tok.to_string(),
                "reddit" => IMAGES.reddit.to_string(),
                _ => IMAGES.no_image.to_string()
            };

            let requester = match track.requester {
                None => player_data.player_data.requester.clone().unwrap_or(IMAGES.no_image.to_string()),
                Some(x) => x
            };

            let embed = CreateEmbed::new()
                .color(config.color)
                .author(CreateEmbedAuthor::new(lang.events.track_start.embed_author)
                    .icon_url(requester)
                )
                .thumbnail(image)
                .description(format!("**[{}]({})** {} from `{}`",
                                     format_song_title(&track.info.title),
                                     track.info.uri.unwrap_or("".to_string()),
                                     if explicit { shared::emojis::EMOJIS.explicit.normal() } else { "".to_string() },
                                     format_song_title(&track.info.author)
                ))
                .field(lang.commands.play.duration, format!("`{}`", player_timestamp(&track.info.length)), true)
                .field(lang.commands.play.in_queue, if player_data.autoplay { "∞".to_string() } else { format!("`{} {}`", queue.len(), if queue.len() > 1 {lang.commands.play.songs} else {lang.commands.play.song}) }, true);

            match player_data.channel.send_message(&client.http, CreateMessage::new()
                .embed(embed)
                .components(
                    vec![CreateActionRow::Buttons(
                        vec![
                            CreateButton::new("queue::cmd::pause")
                                .style(ButtonStyle::Success)
                                .label(lang.commands.play.pause_resume),
                            CreateButton::new("queue::cmd::skip")
                                .style(ButtonStyle::Primary)
                                .label("Skip"),
                            CreateButton::new("queue::cmd::stop")
                                .style(ButtonStyle::Danger)
                                .label(lang.commands.play.stop),
                            CreateButton::new("all::cmd::queue")
                                .style(ButtonStyle::Secondary)
                                .label("Queue"),
                            /*CreateButton::new("cmd:play:like")
                                .style(ButtonStyle::Success)
                                .emoji(shared::emojis::EMOJIS.like.reaction()),*/
                        ]
                    )]
                )
            ).await {
                Ok(msg) => {
                    player_data.set_last_message_id(msg.id).await.unwrap();
                },
                Err(e) => {
                    error!("Failed to send track start message: {}", e);
                }//ignored
            }
        }
    });
}

/// Dispatched when a track ends.
/// track_exception and track_stuck will also trigger this event.
pub fn track_end(client: AteneaClient, _: String, event: &TrackEnd) -> BoxFuture<()> {
    return Box::pin(async move {
        let player = client.get_player_context(event.guild_id.clone()).await;
        if let Some(mut player_data) = player {
            if event.reason.clone().into() {
                match player_data.repeat_mode {
                    RepeatMode::Track => {
                        let mut new_track = event.track.clone();
                        new_track.info.position = 0;

                        debug!("Repeating track: {:?}", new_track);

                        player_data.play_now(&new_track).await.unwrap();
                    }
                    /*RepeatMode::Queue => {
                        let mut new_track = event.track.clone();
                        new_track.info.position = 0;

                        info!("Adding track to queue: {:?}", new_track);
                        player_data.set_queue(QueueMessage::Append(vec![new_track.clone().into()].into())).await.unwrap();

                        // check again because I fucking hate race condition
                        let n_player = client.get_player_context(event.guild_id.clone()).await;
                        if let Some(mut n_player_data) = n_player {
                            if let Some(last) = n_player_data.queue.iter().last() {
                                if last.track.encoded != new_track.encoded {
                                    info!("Race condition on queue repeat, syncing new track..."); // todo: this doesnt work!!!!!!!1111!!1!!!1!!!
                                    n_player_data.set_queue(QueueMessage::Append(vec![new_track.into()].into())).await.unwrap();
                                }
                            }
                        }
                    }*/
                    RepeatMode::None => {
                        debug!("No repeat mode set");
                    }
                    _ => {}
                }
            }

            if let Some(last_message_id) = player_data.last_message_id {
                match player_data.channel.message(&client.http, last_message_id).await {
                    Ok(mut msg) => {
                        let config = client.db.collection::<Guild>("Guilds").find_one(doc! {"guildID": event.guild_id.to_string()}, None).await.unwrap().unwrap();

                        if config.silent {
                            msg.delete(&client.http).await.unwrap_or_default();
                        } else {
                            msg.edit(&client.http, EditMessage::new().components(vec![])).await.unwrap_or_default();
                        }
                    }
                    Err(_) => {}
                };
            }
        }
    });
}

/// Dispatched when a track throws an exception.
pub fn track_exception(_: AteneaClient, session_id: String, event: &TrackException) -> BoxFuture<()> {
    info!("Track exception on session {}: {:?}", session_id, event);
    //todo: poner un embed?
    return Box::pin(async move {});

}

/// Dispatched when a track gets stuck while playing.
pub fn track_stuck(_: AteneaClient, session_id: String, event: &TrackStuck) -> BoxFuture<()> {
    info!("Track stuck on session {}: {:?}", session_id, event);
    return Box::pin(async move {});

}

/// Dispatched when an audio WebSocket to Discord is closed.
pub fn websocket_closed(client: AteneaClient, session_id: String, event: &WebSocketClosed) -> BoxFuture<()> {
    return Box::pin(async move {
        debug!("Websocket session {} closed: {:?}", session_id, event);

        if let Some(player) = client.get_player_context(event.guild_id).await {

            if let Some(last_message_id) = player.last_message_id {
                match player.channel.message(&client.http, last_message_id).await {
                    Ok(mut msg) => {
                        let config = client.db.collection::<Guild>("Guilds").find_one(doc! {"guildID": event.guild_id.to_string()}, None).await.unwrap().unwrap();

                        if config.silent {
                            msg.delete(&client.http).await.unwrap_or_default();
                        } else {
                            msg.edit(&client.http, EditMessage::new().components(vec![])).await.unwrap_or_default();
                        }
                    }
                    Err(_) => {}
                };
            }

            player.delete_redis().await.unwrap();
        };
    });
}