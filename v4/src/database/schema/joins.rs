/*
 * File:          joins.rs
 * Path:          v4\src\database\schema\joins.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: fc4cf0b8c99d0aba2de6ba395925831bff2820769b0064d776f0a3fde688abab
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Joins {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    #[serde(rename = "channelID")]
    pub channel_id: String,
    pub joins: i32
}