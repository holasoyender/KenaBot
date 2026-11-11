/*
 * File:          regex.rs
 * Path:          v4\shared\src\regex.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-28 01:17:14 +01:00
 *
 * SHA-256: c2e5d9037cec3a3045596b0b565688a24855b53b1e0f93103400d7b0ce85956d
 *
 */


pub struct Regex {
    pub spotify: &'static str,
    pub soundcloud: &'static str,
    pub twitch: &'static str,
    pub deezer: &'static str,
    pub apple_music: &'static str,
    pub tik_tok: &'static str,
    pub reddit: &'static str,
    pub youtube: &'static str,
}

pub const REGEX: Regex = Regex {
    spotify: "^https?://(open.spotify.com)/(.*)$",
    soundcloud: "^https?://(soundcloud.com|snd.sc)/(.*)$",
    twitch: "^(https?://)?((www.)?twitch.tv)/(.*)$",
    deezer: "^(https?://)?((www.)?deezer.com)/(.*)$",
    apple_music: "^(https?://)?music.apple.com/(?:.+)?(artist|album|music-video|playlist)/([\\w\\-.]+(/)+[\\w\\-.]+|[^&]+)/([\\w\\-.]+(/)+[\\w\\-.]+|[^&]+)",
    tik_tok: "^(https?://)?((www.)?tiktok.com)/(.*)$",
    reddit: "^(https?://)?((www.)?reddit.com)/(.*)$",
    youtube: "^((?:https?:)?//)?((?:www|m)\\.)?(youtube\\.com|youtu.be)(/(?:[\\w-]+\\?v=|embed/|v/)?)([\\w-]+)(\\S+)?$",
};