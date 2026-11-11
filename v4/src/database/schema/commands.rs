/*
 * File:          commands.rs
 * Path:          v4\src\database\schema\commands.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: f4e9029849ed6d6a2aa11655321dd2a9913be9937f4f7ec5b72ae2bfd0aff0c0
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Commands {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    pub command: String,
    pub uses: i32
}