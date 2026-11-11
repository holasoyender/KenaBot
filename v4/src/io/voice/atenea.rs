/*
 * File:          atenea.rs
 * Path:          v4\src\io\voice\atenea.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 37c742a89b6a45f58da4bea86a6115df6f64c1bc009f4bde18d9bf1a74c0cf5b
 *
 */


use std::sync::Arc;
use skua::all::{Http, UserId};
use redis::aio::ConnectionManager as RedisConnection;
use mongodb::Database;

use crate::atenea::client::AteneaClient;
use crate::atenea::model::events;
use crate::io::voice::node;
use crate::io::voice::node::NodeType;
use crate::locale::manager::LanguageManager;

pub async fn load_client(user_id: UserId, redis: RedisConnection, db: Database, stats_db: Database, http: Arc<Http>, language_manager: LanguageManager) -> AteneaClient {

    let nodes = node::from_file("data/nodes.json", user_id, NodeType::Dev).await.unwrap();

    let events = events::Events {
        //raw: Some(crate::io::voice::events::raw),
        ready: Some(crate::io::voice::events::ready),
        player_update: Some(crate::io::voice::events::player_update),
        stats: Some(crate::io::voice::events::stats),
        track_start: Some(crate::io::voice::events::track_start),
        track_end: Some(crate::io::voice::events::track_end),
        track_exception: Some(crate::io::voice::events::track_exception),
        track_stuck: Some(crate::io::voice::events::track_stuck),
        websocket_closed: Some(crate::io::voice::events::websocket_closed),
        ..events::Events::default()
    };

    let client = AteneaClient::new(events, nodes, redis, db, stats_db, user_id, http, language_manager);
    client.start().await;

    client
}