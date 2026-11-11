/*
 * File:          api.rs
 * Path:          v4\src\api\api.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: 4bf97e13b010ec81a35f012efd8fbabdf96805ac87479d41a054671c9ad352d3
 *
 */


use std::sync::Arc;
use rocket::http::Method;
use rocket::{Error, Ignite, Rocket};
use rocket::shield::Shield;
use rocket_cors::AllowedHeaders;
use mongodb::{Client as MongoClient};
use redis::aio::{ConnectionManager as RedisConnection};
use skua::all::{ShardManager, UserId};
use crate::api::{routes, util};
use crate::locale::manager::LanguageManager;
use crate::managers::command::CommandManager;

pub async fn start_api(
    database: MongoClient,
    main_db_name: String,
    redis: RedisConnection,
    shard_manager: Arc<ShardManager>,
    bot_id: UserId,
    language_manager: LanguageManager,
    command_manager: CommandManager
) -> Result<Rocket<Ignite>, Error> {

    let cors = rocket_cors::CorsOptions {
        allowed_methods: vec![Method::Get, Method::Post, Method::Delete].into_iter().map(From::from).collect(),
        allowed_headers: AllowedHeaders::some(&["Authorization", "Accept", "Content-Type"]),
        allow_credentials: true,
        ..Default::default()
    }
        .to_cors();

    let prometheus = util::prometheus::configure();

    rocket::build()
        .manage(database)
        .manage(main_db_name)
        .manage(redis)
        .manage(shard_manager)
        .manage(bot_id)
        .manage(language_manager)
        .manage(command_manager)
        //.mount("/api", routes::api())
        //.mount("/stats", routes![routes::stats::get_stats])
        .mount("/", routes::common())
        .mount("/metrics", prometheus.clone())
        .register("/", routes::catchers())
        .attach(Shield::default())
        .attach(cors.unwrap())
        .attach(prometheus)
        .launch()
        .await
}