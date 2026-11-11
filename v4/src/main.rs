/*
 * File:          main.rs
 * Path:          v4\src\main.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 4baa04e95919b7520a280e57e4f02ea7bac56c439cb27c5a7612f2c95e54ff8a
 *
 */


#[macro_use]
extern crate log;
extern crate chrono;
extern crate fern;

#[macro_use]
extern crate lazy_static;

use std::borrow::Cow;
use shared::constants::load_constants;
use shared::env::dotenv;

mod logger;
mod discord;
mod events;
mod database;
mod commands;
mod managers;
mod util;
mod locale;
mod io;
mod api;
mod atenea;
mod globals;
mod metrics;

#[tokio::main]
async fn main() {
    dotenv().ok();

    let _guard = sentry::init((std::env::var("SENTRY_DSN").expect("Sentry DSN is missing"), sentry::ClientOptions {
        release: sentry::release_name!(),
        environment: Some(Cow::from("development")),
        ..Default::default()
    }));

    logger::logger::setup_logger().expect("Failed to setup logger");
    metrics::register();

    let db = database::mongo::connect().await.expect("Failed to connect to MongoDB");
    let cache = database::redis::connect().await.expect("Failed to connect to Redis");
    
    load_constants(&cache).await;

    discord::setup_bot(&db, &cache).await.expect("Failed to start the bot");
}

/*
https://github.com/serenity-rs/serenity/tree/current/examples
https://gitlab.com/vicky5124/lavalink-rs
https://github.com/holasoyender/Kena/tree/v4-rs
https://github.com/Discusss/sailor/blob/main/Cargo.toml
*/
