/*
 * File:          images.rs
 * Path:          v4\shared\src\images.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-29 00:34:20 +01:00
 *
 * SHA-256: a49b9beb8d1f686a6e7d47f1e082052c61c91d6ad5149264c0a56d820dc77f66
 *
 */


pub struct Images {
    pub soundcloud: &'static str,
    pub twitch: &'static str,
    pub deezer: &'static str,
    pub apple_music: &'static str,
    pub tik_tok: &'static str,
    pub reddit: &'static str,
    pub no_image: &'static str,
}

pub const IMAGES: Images = Images {
    soundcloud: "https://cdn.discordapp.com/attachments/755000173922615336/1012769193717923902/soundcloud.png",
    twitch: "https://cdn.discordapp.com/attachments/755000173922615336/1012766939824791643/twitch.png",
    deezer: "https://cdn.discordapp.com/attachments/755000173922615336/1012767607918694510/deezer.png",
    apple_music: "https://cdn.discordapp.com/attachments/755000173922615336/1012768596163821579/apple.png",
    tik_tok: "https://cdn.discordapp.com/attachments/755000173922615336/1012766231033552926/tiktok.png",
    reddit: "https://cdn.discordapp.com/attachments/755000173922615336/1012770087540903946/reddit.png",
    no_image: "https://cdn.discordapp.com/attachments/755000173922615336/955506132372291664/no_image.png",
};