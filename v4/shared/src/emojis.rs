/*
 * File:          emojis.rs
 * Path:          v4\shared\src\emojis.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 9719a615404aea477446f0924dd2375a8c95c68bbe6cc64b334f342759577430
 *
 */


use skua::all::ReactionType;

pub struct Emojis {
    pub ping_pong: Emoji,
    pub tick: Emoji,
    pub error: Emoji,
    pub explicit: Emoji,
    pub like: Emoji,
    pub warning: Emoji,
    pub online: Emoji,
    pub dnd: Emoji,
    pub left: Emoji,
    pub right: Emoji,
}

pub struct Emoji(&'static str);

impl Emoji {
    // A format function which adds "  " to the end of the emoji
    pub fn format(&self) -> String {
        format!("{}  ", self.0)
    }

    pub fn normal(&self) -> String {
        format!("{}", self.0)
    }

    pub fn reaction(&self) -> ReactionType {
        self.normal().parse().unwrap()
    }
}

pub const EMOJIS: Emojis = Emojis {
    ping_pong: Emoji("🏓"),
    tick: Emoji("<:tick:952250501779570738>"),
    error: Emoji("🚫"),
    explicit: Emoji("<:explicit:1073266738759729184>"),
    like: Emoji("<:heart:1190045281300009062>"),
    warning: Emoji("<:idle:983837896287092796>"),
    online: Emoji("<:online:983837897536987146>"),
    dnd: Emoji("<:dnd:983837895083323452>"),
    left: Emoji("◀"),
    right: Emoji("▶"),
};