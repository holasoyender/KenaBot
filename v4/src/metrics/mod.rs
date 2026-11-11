/*
 * File:          mod.rs
 * Path:          v4\src\metrics\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 1f7d0639ccc9c32aecd71c44cd7f1a94b056d8703b0b1a3a5ede214a89e93844
 *
 */


pub(crate) mod bot;
pub(crate) mod player;
pub(crate) mod database;

pub fn register() {
    info!("Registering metrics managers...");
    bot::register();
    player::register();
    database::register();
}