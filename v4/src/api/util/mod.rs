/*
 * File:          mod.rs
 * Path:          v4\src\api\util\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 19:54:39 +01:00
 *
 * SHA-256: a47926488e957295b3170092e93de0f0ecae651dec2a21948cc08226b8a56504
 *
 */


pub(crate) mod prometheus;
pub(crate) mod responses;
pub(crate) mod ip;

pub fn is_id(id: &String) -> bool {
    id.len() >= 17 && id.len() <= 21 && id.chars().all(char::is_numeric)
}