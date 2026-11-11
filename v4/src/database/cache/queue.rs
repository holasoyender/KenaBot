/*
 * File:          queue.rs
 * Path:          v4\src\database\cache\queue.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: bf61af20afc0e743428e9927c2cf9fd155fde12e10c2b8d3b6187406a5efc68e
 *
 */


use std::collections::VecDeque;
use serde::{Deserialize, Serialize};
use skua::all::{GuildChannel, GuildId, MessageId, UserId};
use redis::aio::ConnectionManager as RedisConnection;
use redis::AsyncCommands;

use crate::io::voice::RepeatMode;
use crate::atenea::model::deserialize_number_from_string;
use crate::atenea::player_context::TrackInQueue;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct PlayerInfo {
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    pub channel: GuildChannel,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub bot_id: UserId,
    pub last_message_id: Option<MessageId>,
    pub paused: bool,
    pub repeat_mode: RepeatMode,
    pub position: u64,
    pub volume: u16,
    pub autoplay: bool,
    pub current_track: Option<TrackInQueue>,
    pub last_track: Option<TrackInQueue>,
    pub queue: VecDeque<TrackInQueue>
}

impl PlayerInfo {
    pub async fn get(guild_id: GuildId, bot_id: UserId, redis: &RedisConnection) -> Option<Self> {
        let mut redis = redis.clone();
        let key = format!("player:{}:{}", bot_id, guild_id);
        let player_info: Option<String> = redis.get(&key).await.ok();
        let player_info = match player_info {
            Some(player_info) => player_info,
            None => return None,
        };

        let player_info: PlayerInfo = serde_json::from_str(&player_info).unwrap();
        Some(player_info)
    }

    pub async fn save(&self, redis: &RedisConnection) {
        let mut redis = redis.clone();
        let key = format!("player:{}:{}", self.bot_id, self.guild_id);
        let player_info = serde_json::to_string(self).unwrap();
        let _: () = redis.set_ex(&key, player_info, 3600).await.unwrap();
    }

    pub async fn delete(&self, redis: &RedisConnection) {
        let mut redis = redis.clone();
        let key = format!("player:{}:{}", self.bot_id, self.guild_id);
        let _: () = redis.del(&key).await.unwrap();
    }

}