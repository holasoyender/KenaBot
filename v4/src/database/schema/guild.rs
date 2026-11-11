/*
 * File:          guild.rs
 * Path:          v4\src\database\schema\guild.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-29 20:05:11 +01:00
 *
 * SHA-256: 6278be26d7a4924a3a60dc257938642df973bf33d9b3298c38db3f0fb74af815
 *
 */


use serde::{Serialize, Deserialize};
use mongodb::bson::{Bson, doc, oid::ObjectId};

#[derive(Debug, Serialize, Deserialize)]
pub struct Guild {
    #[serde(rename = "_id", skip_serializing_if = "Option::is_none")]
    pub id: Option<ObjectId>,
    #[serde(rename = "guildID")]
    pub guild_id: String,
    pub lang: String,
    #[serde(with = "color_serde")]
    pub color: i32,
    pub premium: Premium,
    #[serde(rename = "_247")]
    pub radio: Radio,
    pub blocked: Blocked,
    pub disabled: Disabled,
    pub advanced: Advanced,
    pub beta: bool,
    pub silent: bool,
    pub dj_role: String,
    pub autoplay: bool,
}

mod color_serde {
    use serde::{Serializer, Deserializer, de, Deserialize};

    pub fn serialize<S>(value: &i32, serializer: S) -> Result<S::Ok, S::Error>
        where
            S: Serializer,
    {
        // Convert the i32 to a hex string and serialize it
        serializer.serialize_str(&format!("{:06X}", value))
    }

    pub fn deserialize<'de, D>(deserializer: D) -> Result<i32, D::Error>
        where
            D: Deserializer<'de>,
    {
        // Deserialize the hex string and convert it to an i32
        let mut s: String = Deserialize::deserialize(deserializer)?;
        if s.starts_with("#") {
            s = s.replace("#", "");
        }

        i32::from_str_radix(&s, 16)
            .map_err(|e| de::Error::custom(format!("Failed to deserialize color: {}", e)))
    }
}

impl Guild {

    pub fn default(guild_id: &String) -> Self {
        Guild {
            id: None,
            guild_id: guild_id.clone(),
            lang: "es-ES".to_string(),
            color: 0x2f3136,//"#2f3136".to_string(),
            premium: Premium {
                enabled: false,
                expires: "".to_string().into()
            },
            radio: Radio {
                channel_id: "".to_string(),
                enabled: false,
            },
            blocked: Blocked {
                enabled: false,
                reason: "".to_string(),
            },
            disabled: Disabled {
                channels: vec![],
                commands: vec![],
                voice: vec![],
            },
            advanced: Advanced {
                max_song_duration: 0,
                blocked_songs: vec![],
                explicit_filter: false,
            },
            beta: false,
            silent: false,
            dj_role: "".to_string(),
            autoplay: false,
        }
    }

}

#[derive(Debug, Serialize, Deserialize)]
pub struct Premium {
    pub enabled: bool,
    pub expires: Bson,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Radio {
    #[serde(rename = "channelID")]
    pub channel_id: String,
    pub enabled: bool,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Disabled {
    pub channels: Vec<String>,
    pub commands: Vec<String>,
    pub voice: Vec<String>,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Blocked {
    pub enabled: bool,
    pub reason: String,
}

#[derive(Debug, Serialize, Deserialize)]
pub struct Advanced {
    #[serde(rename = "maxSongDuration")]
    pub max_song_duration: u64,
    #[serde(rename = "blockedSongs")]
    pub blocked_songs: Vec<String>,
    #[serde(rename = "explicitFilter")]
    pub explicit_filter: bool,
}