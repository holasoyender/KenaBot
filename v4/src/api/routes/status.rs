/*
 * File:          status.rs
 * Path:          v4\src\api\routes\status.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: 3a1737469a2e626d3dd9e26b847597584ca22d32763e0b4a4ff09b823cbcca1e
 *
 */


use std::sync::Arc;
use std::time::Duration;
use rocket::get;
use mongodb::{Client as MongoClient};
use mongodb::bson::doc;
use rocket::http::Status;
use redis::aio::ConnectionManager as RedisConnection;
use rocket::serde::json::{Json, json};
use serde_json::Value;
use skua::all::{ShardManager, UserId};
use crate::api::util::ip::RemoteAddress;
use crate::api::util::responses::{DataResponse, Requester};
use crate::database::cache::stats::CacheStats;

#[get("/status")]
pub async fn get_status(db: &rocket::State<MongoClient>, cache: &rocket::State<RedisConnection>, bot_id: &rocket::State<UserId>, shard_manager: &rocket::State<Arc<ShardManager>>, remote: RemoteAddress) -> Result<Json<DataResponse>, Status> {
    let db = db as &MongoClient;
    let shard_manager = shard_manager as &Arc<ShardManager>;
    let cache = cache as &RedisConnection;
    let bot_id = bot_id as &UserId;

    let cache_stats = CacheStats::get(bot_id.clone(), &cache).await;
    let (user_count, guild_count) = match cache_stats {
        Some(stats) => (stats.user_count, stats.guild_count),
        None => (0, 0)
    };

    let shard_status: Vec<Value> = shard_manager.runners.lock().await.iter().map(|(shard_id, shard)| {

        let id = shard_id.to_string();
        let state = shard.stage.to_string().to_uppercase();
        let latency = shard.latency.unwrap_or(Duration::from_secs(0)).as_millis();

        json!({
            "id": id,
            "host-name": format!("shard-{}", id),
            "gatewayState": state,
            "latency": latency,
            //"guilds": shard.guilds.len(),
            //"users": shard.guilds.values().map(|guild| guild.member_count).sum::<u64>(),
            //"unavailableGuilds": shard.unavailable_guilds.len()
        })
    }).collect();

    let db_latency = db.database("Stats").run_command(doc! { "ping": 1 }, None).await.unwrap().get("ok").unwrap().as_f64().unwrap_or(0.0) as u64;

    let response = DataResponse {
        data: json!({
            "db_latency": db_latency,
            "shards": shard_status,
            "total_users": user_count,
            "total_guilds": guild_count,
        }),
        requester: Requester {
            ip: remote.ip,
            user_agent: remote.user_agent
        },
        status: 200
    };

    Ok(Json(response))
}