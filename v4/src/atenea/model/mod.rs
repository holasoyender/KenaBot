/*
 * File:          mod.rs
 * Path:          v4\src\atenea\model\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: a66a4d8dced7461d83063aa76cc2b033c46dcaa88d43a688501eeabc68b2900f
 *
 */


use std::fmt::Display;
use std::str::FromStr;

pub use futures::future::BoxFuture;
use serde::{de, Deserialize, Deserializer};

/// Models related to the atenea events.
pub mod events;
/// Models related to the atenea REST API.
pub mod http;
/// Models related to the atenea Player.
pub mod player;
/// Models related to search engines.
pub mod search;
/// Models related to the tracks.
pub mod track;
/*
#[derive(
    Hash, PartialEq, Eq, PartialOrd, Ord, Debug, Copy, Clone, Default, Serialize, Deserialize,
)]
/// A discord User ID.
pub struct UserId(pub u64);
#[derive(
    Hash, PartialEq, Eq, PartialOrd, Ord, Debug, Copy, Clone, Default, Serialize, Deserialize,
)]
/// A discord Guild ID.
pub struct GuildId(pub u64);

impl FromStr for UserId {
    type Err = ParseIntError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        u64::from_str(s).map(Self)
    }
}

impl FromStr for GuildId {
    type Err = ParseIntError;

    fn from_str(s: &str) -> Result<Self, Self::Err> {
        u64::from_str(s).map(Self)
    }
}

impl From<u64> for UserId {
    fn from(i: u64) -> Self {
        Self(i)
    }
}

impl From<u64> for GuildId {
    fn from(i: u64) -> Self {
        Self(i)
    }
}*/

pub(crate) fn deserialize_option_number<'de, D>(deserializer: D) -> Result<Option<u32>, D::Error>
where
    D: Deserializer<'de>,
{
    let n = i32::deserialize(deserializer)?;
    Ok(match n.cmp(&-1) {
        std::cmp::Ordering::Less => return Err(de::Error::custom("integer {n} is below -1")),
        std::cmp::Ordering::Equal => None,
        std::cmp::Ordering::Greater => Some(n.try_into().unwrap()),
    })
}

pub(crate) fn deserialize_number_from_string<'de, T, D>(deserializer: D) -> Result<T, D::Error>
where
    D: Deserializer<'de>,
    T: FromStr + serde::Deserialize<'de>,
    <T as FromStr>::Err: Display,
{
    #[derive(Deserialize)]
    #[serde(untagged)]
    enum StringOrInt<T> {
        String(String),
        Number(T),
    }

    match StringOrInt::<T>::deserialize(deserializer)? {
        StringOrInt::String(s) => s.parse::<T>().map_err(de::Error::custom),
        StringOrInt::Number(i) => Ok(i),
    }
}
/*
use skua::model::id::{GuildId as SerenityGuildId, UserId as SerenityUserId};

impl From<SerenityUserId> for UserId {
    fn from(id: SerenityUserId) -> UserId {
        UserId(id.get())
    }
}

impl From<SerenityGuildId> for GuildId {
    fn from(id: SerenityGuildId) -> GuildId {
        GuildId(id.get())
    }
}


use songbird::id::{GuildId as SongbirdGuildId, UserId as SongbirdUserId};

impl From<SongbirdUserId> for UserId {
    fn from(id: SongbirdUserId) -> UserId {
        UserId(id.0.get())
    }
}

impl From<SongbirdGuildId> for GuildId {
    fn from(id: SongbirdGuildId) -> GuildId {
        GuildId(id.0.get())
    }
}
*/