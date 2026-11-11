/*
 * File:          mongo.rs
 * Path:          v4\src\database\mongo.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: 8eca318b62ea4db73d10da38c949cac29c015ea8c3cba526de2c8c97ce3d470c
 *
 */


use std::error::Error;
use mongodb::{Client};
use mongodb::options::ClientOptions;

pub(crate) async fn connect() -> Result<Client, Box<dyn Error>> {

    let uri = std::env::var("MONGODB_URI").expect("MONGODB_URI is missing");
    if uri.is_empty() {
        panic!("MONGODB_URI is empty");
    }

    let mut options = ClientOptions::parse(&uri).await?;

    options.connect_timeout = Some(std::time::Duration::from_secs(5));
    options.server_selection_timeout = Some(std::time::Duration::from_secs(5));
    options.retry_reads = Some(true);
    options.retry_writes = Some(true);
    //options.load_balanced = Some(true);

    let db = Client::with_options(options)?;
    info!("Connected to the MongoDB database");

    Ok(db)
}