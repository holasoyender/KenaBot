/*
 * File:          mod.rs
 * Path:          v4\src\io\voice\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-29 00:34:20 +01:00
 *
 * SHA-256: 5b0634235f9c55df364c968711cf9bd7f1804461518b068ed81d509b860d7263
 *
 */


use serde::{Deserialize, Serialize};

pub(crate) mod connection;
mod node;
pub(crate) mod atenea;
pub(crate) mod events;

#[derive(Debug, Clone, Copy, PartialEq, Eq, Serialize, Deserialize)]
pub enum RepeatMode {
    None,
    Track,
    Queue,
}