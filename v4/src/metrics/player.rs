/*
 * File:          player.rs
 * Path:          v4\src\metrics\player.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: bca4fc4a6c92964d5bf86e1f50cc2ce3991feeb6ae4843dc55a928da800af6aa
 *
 */


use prometheus::{Gauge, GaugeVec};

struct PlayerStats {
    player_count: Gauge,
    playing_count: Gauge,
    regions: GaugeVec
}

lazy_static!(
    static ref PLAYER_STATS: PlayerStats = PlayerStats {
    player_count: Gauge::new("player_count", "The number of players").unwrap(),
    playing_count: Gauge::new("playing_count", "The number of players playing").unwrap(),
    regions: GaugeVec::new(
        prometheus::Opts::new("player_regions", "The number of joins in each region"),
        &["region"]
    ).unwrap()
};
);

pub fn register() {
    prometheus::register(Box::new(PLAYER_STATS.player_count.clone())).unwrap();
    prometheus::register(Box::new(PLAYER_STATS.playing_count.clone())).unwrap();
}