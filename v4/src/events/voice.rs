/*
 * File:          voice.rs
 * Path:          v4\src\events\voice.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: edb5709635f8468807d179e51279b6e058bd900df6848ed21285f48de3d34115
 *
 */


use mongodb::bson::doc;
use redis::aio::ConnectionManager as RedisConnection;
use skua::all::{ChannelType, Context, EditVoiceState, GuildId, UserId, VoiceState};
use crate::atenea::client::AteneaClient;
use crate::atenea::player_context::QueueMessage;
use crate::database::cache::queue::PlayerInfo;
use crate::database::cache::stats::CacheStats;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;

pub async fn voice_channel_join(handler: &Handler, ctx: Context, state: VoiceState) {

    let bot_id = handler.bot_id.read().unwrap();

    if state.user_id == bot_id {

        let stats = CacheStats::get(bot_id, &handler.cache).await;
        if let Some(mut stats) = stats {
            stats.voice_connections += 1;
            stats.save(bot_id, &handler.cache).await;
        }

        let channel = match state.channel_id.unwrap().to_channel(&ctx.http).await {
            Ok(channel) => channel.guild().unwrap(),
            Err(_) => return,
        };

        if channel.kind == ChannelType::Stage {
            let builder = EditVoiceState::new().request_to_speak(true);
            channel.edit_own_voice_state(&ctx.http, builder).await.unwrap_or_default();
            return;
        }
    } else {

    }
}

pub async fn voice_server_mute(handler: &Handler, ctx: Context, old: VoiceState, new: VoiceState) {
    let bot_id = handler.bot_id.read().unwrap();
    let guild_id = new.guild_id.unwrap();

    if old.user_id == bot_id {

        let atenea_client = {
            let data_read = ctx.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
            if new.mute {
                player.set_pause(true).await.unwrap();
            } else {
                player.set_pause(false).await.unwrap();
            }
        }
    }
}

pub async fn voice_channel_leave(handler: &Handler, ctx: Context, state: VoiceState) {
    let bot_id = handler.bot_id.read().unwrap();
    let guild_id = state.guild_id.unwrap_or_default();

    let atenea_client = {
        let data_read = ctx.data.read().await;
        data_read.get::<Atenea>().unwrap().clone()
    };

    if state.user_id == bot_id {

        let stats = CacheStats::get(bot_id, &handler.cache).await;
        if let Some(mut stats) = stats {
            stats.voice_connections -= 1;
            stats.save(bot_id, &handler.cache).await;
        }

        let manager = skua_voice::get(&ctx).await.unwrap().clone();

        if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
            player.set_queue(QueueMessage::Clear).await.unwrap();
            player.stop_now().await.unwrap();
            atenea_client.delete_player(guild_id).await.unwrap();
        }

        if let Some(player_info) = PlayerInfo::get(guild_id, bot_id, &handler.cache).await {
            player_info.delete(&handler.cache).await;
        }

        if manager.get(guild_id).is_some() {
            manager.remove(guild_id).await.unwrap();
        }
    } else {

        let channel = match state.channel_id.unwrap().to_channel(&ctx.http).await {
            Ok(channel) => channel.guild().unwrap(),
            Err(_) => return,
        };
        let members = channel.members(&ctx.cache).unwrap();

        if members.len() == 1 && members.first().unwrap().user.id == bot_id {

            match handler.db.collection::<Guild>("Guilds").find_one(doc! {"guildID": guild_id.to_string()}, None).await {
                Ok(guild) => {
                    match guild {
                        Some(config) => {
                            if config.premium.enabled && config.radio.enabled && config.radio.channel_id != "" {
                                if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
                                    player.set_pause(true).await.unwrap();
                                }

                                if let Some(mut player_info) = PlayerInfo::get(guild_id, bot_id, &handler.cache).await {
                                    player_info.paused = true;
                                    player_info.save(&handler.cache).await;
                                }

                            } else {
                                leave(guild_id, bot_id, &handler.cache, atenea_client, &ctx).await;
                            }
                        },
                        None => {
                            leave(guild_id, bot_id, &handler.cache, atenea_client, &ctx).await;
                        }
                    }
                }
                Err(_) => {
                    leave(guild_id, bot_id, &handler.cache, atenea_client, &ctx).await;
                }
            };
        }
    }
}

async fn leave(guild_id: GuildId, bot_id: UserId, cache: &RedisConnection, atenea_client: AteneaClient, context: &Context) {
    let manager = skua_voice::get(context).await.unwrap().clone();

    if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
        player.set_queue(QueueMessage::Clear).await.unwrap();
        player.stop_now().await.unwrap();
        atenea_client.delete_player(guild_id).await.unwrap();
    }

    if let Some(player_info) = PlayerInfo::get(guild_id, bot_id, cache).await {
        player_info.delete(cache).await;
    }

    if manager.get(guild_id).is_some() {
        manager.remove(guild_id).await.unwrap();
    }
}