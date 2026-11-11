/*
 * File:          duration_millis.rs
 * Path:          v4\src\io\spotify\util\duration_millis.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-22 14:21:17 +01:00
 *
 * SHA-256: 6df86ebb586f9f36f8326f143c19e1e28e47c2356913c03df1c3323eef0d167a
 *
 */


use std::time::Duration;

use serde::{Deserialize, Deserializer, Serialize, Serializer};

#[allow(dead_code)]
pub(crate) fn serialize<S>(duration: &Duration, serializer: S) -> Result<S::Ok, S::Error>
where
    S: Serializer,
{
    duration.as_millis().serialize(serializer)
}

#[allow(dead_code)]
pub(crate) fn deserialize<'de, D>(deserializer: D) -> Result<Duration, D::Error>
where
    D: Deserializer<'de>,
{
    Ok(Duration::from_millis(Deserialize::deserialize(deserializer)?))
}
