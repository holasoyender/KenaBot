/*
 * File:          responses.rs
 * Path:          v4\src\api\util\responses.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: f798dd322c5abf12f3522af57ef7bfe57977d135337bd3c95412726011c728f9
 *
 */


use rocket::serde::json::{Value};
use serde::Serialize;

#[derive(Serialize)]
pub struct GenericResponse {
    pub status: i32,
    pub message: String,
}

#[derive(Serialize)]
pub struct DataResponse {
    pub status: i32,
    pub data: Value,
    pub requester: Requester
}

#[derive(Serialize)]
pub struct Requester {
    pub ip: String,
    pub user_agent: Option<String>
}

#[derive(Serialize)]
pub struct DataResponseArray {
    pub status: i32,
    pub data: Vec<Value>,
    pub requester: Requester
}