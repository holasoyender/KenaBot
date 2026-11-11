/*
 * File:          premium.rs
 * Path:          v4\src\database\schema\premium.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-11 23:31:59 +01:00
 *
 * SHA-256: 0ad157767340005348251ca6292fd9d591811db232201a675a1724b9878ba568
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Premium {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    pub code: String,
    pub duration: String,
    #[serde(rename = "createdAt")]
    pub created_at: i64,
    pub claimed: bool,
    #[serde(rename = "claimedAt")]
    pub claimed_in_server: String,
    #[serde(rename = "claimedOn")]
    pub claimed_date: Option<i64>,
    #[serde(rename = "createdBy")]
    pub created_by: String,
    pub bots: Vec<String>,
}