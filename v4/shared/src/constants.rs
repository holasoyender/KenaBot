/*
 * File:          constants.rs
 * Path:          v4\shared\src\constants.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 6773a299bfe963c4d446467d01df5d29591827ccecfeb663f5dec4c7d8daa19d
 *
 */


use serde::{Deserialize, Serialize};
use redis::aio::{ConnectionManager as RedisConnection};
use redis::AsyncCommands;
use skua::all::UserId;

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Constants {
    pub owner_ids: Vec<UserId>,
    pub translators: Vec<String>,
    pub prefix: String,
    pub support_invite: String,
    pub website: String,
    pub invite: String,
    pub max_playlist_size: i32,
    pub disabled_commands: Vec<String>,
    pub default_volume: i32,
    pub maintenance: bool,
    pub default_language: String
}

pub async fn load_constants(redis: &RedisConnection) {

    let redis = redis.clone();

    tokio::spawn(async move {
        loop {
            reload_constants(redis.clone()).await;
            tokio::time::sleep(tokio::time::Duration::from_secs(60 * 60)).await;
        }
    });
}

async fn reload_constants(redis: RedisConnection) -> Constants {
    let mut redis = redis.clone();
    let constants = std::fs::read_to_string("data/config.json").unwrap();
    let _: () = redis.set_ex("config", constants.clone(), 60 * 60 * 24).await.unwrap();
    serde_json::from_str(&constants).unwrap()
}

pub async fn get_constants(redis: &RedisConnection) -> Constants {
    let mut redis = redis.clone();
    let constants: String = redis.get("config").await.unwrap();
    serde_json::from_str(&constants).unwrap()
}