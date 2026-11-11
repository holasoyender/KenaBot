/*
 * File:          stats.rs
 * Path:          v4\src\database\cache\stats.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 9372f80c15dcc99df1330c3b1e10412cfedfb9f1238778c02413e5bb295aba22
 *
 */


use serde::{Deserialize, Serialize};
use redis::aio::ConnectionManager as RedisConnection;
use redis::AsyncCommands;
use skua::all::UserId;

#[derive(Debug, Serialize, Deserialize, Clone, PartialEq, Eq, Hash)]
pub struct CacheStats {
    pub user_count: u64,
    pub guild_count: u64,
    pub voice_connections: u64,
}

impl CacheStats {

    pub async fn get(bot_id: UserId, redis: &RedisConnection) -> Option<Self> {
        let mut redis = redis.clone();
        let key = format!("stats:{}", bot_id);
        let stats: Option<String> = redis.get(&key).await.ok();
        let stats = match stats {
            Some(stats) => stats,
            None => return None,
        };

        let stats: CacheStats = serde_json::from_str(&stats).unwrap();
        Some(stats)
    }

    pub async fn save(&self, bot_id: UserId, redis: &RedisConnection) {
        let mut redis = redis.clone();
        let key = format!("stats:{}", bot_id);
        let stats = serde_json::to_string(self).unwrap();
        let _: () = redis.set_ex(&key, stats, 3600 * 24).await.unwrap();
    }

    pub async fn delete(bot_id: UserId, redis: &RedisConnection) {
        let mut redis = redis.clone();
        let key = format!("stats:{}", bot_id);
        let _: () = redis.del(&key).await.unwrap();
    }

}