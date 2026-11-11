/*
 * File:          regions.rs
 * Path:          v4\src\database\schema\regions.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: e75e031379abe76cab8f09ccbd438ec2ec00f8df2ef9552133ef1a844b80431d
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Regions {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    pub region: String,
    pub count: i32
}