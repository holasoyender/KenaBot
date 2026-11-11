/*
 * File:          session.rs
 * Path:          v4\src\database\schema\session.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: 38340f08784e1908ffed139616c276d267174fac5c85d188e2a283f7c7093248
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Session {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    #[serde(rename = "sourceGuildId")]
    pub source_guild_id: String,
    #[serde(rename = "sourceBotName")]
    pub source_bot_name: String,
    #[serde(rename = "inviteCode")]
    pub invite_code: String,
    #[serde(rename = "createdAt")]
    pub created_at: i64,
    pub listeners: Vec<SessionListener>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct SessionListener {
    #[serde(rename = "guildId")]
    pub guild_id: String,
    #[serde(rename = "botName")]
    pub bot_name: String,
    #[serde(rename = "channelId")]
    pub channel_id: String,
}