/*
 * File:          blocked.rs
 * Path:          v4\src\api\routes\blocked.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-14 22:11:00 +01:00
 *
 * SHA-256: f75b60a5eee61487c730714f080fb04c5de5824450f0da0471f0dd5ee23bff1b
 *
 */


use mongodb::{Client as MongoClient};
use mongodb::bson::doc;
use rocket::get;
use rocket::http::Status;
use rocket::serde::json::{Json, json};
use crate::api::util::ip::RemoteAddress;
use crate::api::util::is_id;
use crate::api::util::responses::{DataResponseArray, Requester};
use crate::database::schema::guild::Guild;

#[get("/blocked?<guild_id>")]
pub async fn get_blocked(db: &rocket::State<MongoClient>, remote: RemoteAddress, bot_name: &rocket::State<String>, guild_id: String) -> Result<Json<DataResponseArray>, Status> {

    if !is_id(&guild_id) {
        return Err(Status::BadRequest);
    }

    let db = (db as &MongoClient).database(bot_name.as_str());

    let collection = db.collection::<Guild>("Guilds");
    let query = doc! {"guildID": &guild_id};
    let guild = match collection.find_one(query, None).await.unwrap() {
        Some(guild) => guild,
        None => {
            return Err(Status::NotFound);
        }
    };

    let response = DataResponseArray {
        data: guild.advanced.blocked_songs.iter().map(|uri| json!({ "uri": uri })).collect(),
        requester: Requester {
            ip: remote.ip,
            user_agent: remote.user_agent
        },
        status: 200
    };

    Ok(Json(response))
}
