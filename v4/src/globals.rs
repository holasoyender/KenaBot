/*
 * File:          globals.rs
 * Path:          v4\src\globals.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: cd2079e722d12986337619e14dce828583d3bbc7f979eafcfc3ac81f39545e43
 *
 */


use std::{sync::Arc, time::Instant};
use skua::all::ShardManager;

use typemap_rev::TypeMapKey;
use redis::aio::ConnectionManager as RedisConnection;
use mongodb::Client as MongoClient;

use crate::atenea::client::AteneaClient;

// Defining the structures to be used for "global" data
// this data is not really global, it's just shared with Context.data
pub struct ShardManagerContainer; // Shard manager to use for the latency.
pub struct DatabasePool; // A pool of connections to the database.
pub struct CachePool; // The connection to the redis cache database.
pub struct Atenea; //  This is the struct for the lavalink client.
pub struct Uptime; //  This is for the startup time of the bot.

impl TypeMapKey for ShardManagerContainer {
    type Value = Arc<ShardManager>;
}

impl TypeMapKey for DatabasePool {
    type Value = MongoClient;
}

impl TypeMapKey for CachePool {
    type Value = RedisConnection;
}

impl TypeMapKey for Atenea {
    type Value = AteneaClient;
}

impl TypeMapKey for Uptime {
    type Value = Arc<Instant>;
}