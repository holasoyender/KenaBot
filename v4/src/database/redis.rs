/*
 * File:          redis.rs
 * Path:          v4\src\database\redis.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-06 14:55:58 +01:00
 *
 * SHA-256: a3850522f8daf905d366f673229cfe520dc1282f14d45253830c119b2503ea41
 *
 */


use std::error::Error;
use redis::aio::ConnectionManager;

pub async fn connect() -> Result<ConnectionManager, Box<dyn Error>> {
    let uri = std::env::var("REDIS_URI").expect("REDIS_URI is missing");
    if uri.is_empty() {
        panic!("REDIS_URI is empty");
    }

    let client = redis::Client::open(uri)?;
    let manager = ConnectionManager::new(client).await?;
    info!("Connected to the Redis cache database");

    Ok(manager)
}