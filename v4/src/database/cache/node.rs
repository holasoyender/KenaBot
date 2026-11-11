/*
 * File:          node.rs
 * Path:          v4\src\database\cache\node.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: eb164894b7a9ba37b91ea19c0fb47a28ea8e656a22f9662e3cb2a55ef269a79b
 *
 */


use std::collections::HashMap;
use skua::all::UserId;
use redis::aio::{ConnectionManager as RedisConnection};
use redis::AsyncCommands;
use crate::atenea::model::events::Stats;

pub async fn save_node_stats(bot_id: UserId, redis: &RedisConnection, session_id: String, stats: &Stats) {
    let mut redis = redis.clone();
    let key = format!("stats:{}:{}", bot_id, session_id);
    let stats_info = serde_json::to_string(&stats).unwrap();
    let _: () = redis.set_ex(&key, stats_info, 60).await.unwrap();
}

pub async fn get_node_stats(bot_id: UserId, session_id: String, redis: &RedisConnection) -> Option<Stats> {
    let mut redis = redis.clone();
    let key = format!("stats:{}:{}", bot_id, session_id);
    let stats: Option<String> = redis.get(&key).await.ok();
    let stats = match stats {
        Some(stats) => stats,
        None => return None,
    };

    let stats: Stats = serde_json::from_str(&stats).unwrap();
    Some(stats)
}

pub async fn get_all_node_stats(bot_id: UserId, redis: &RedisConnection) -> HashMap<String, Stats> {
    let mut redis = redis.clone();
    let key = format!("stats:{}:*", bot_id);
    let keys: Vec<String> = redis.keys(key).await.unwrap();
    let mut stats = HashMap::new();

    for key in keys {
        let _stats: Option<String> = redis.get(&key).await.ok();
        let raw_stats = match _stats {
            Some(stats) => stats,
            None => continue,
        };

        let stats_info: Stats = serde_json::from_str(&raw_stats).unwrap();
        let node_id = key.split(":").collect::<Vec<&str>>()[2].to_string();
        stats.insert(node_id, stats_info);
    }

    stats
}