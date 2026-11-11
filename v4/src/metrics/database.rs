/*
 * File:          database.rs
 * Path:          v4\src\metrics\database.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 29281cbddb67e4c1fc1aa1958b322e47b28bdb6af838d98150ce632f39b60aa3
 *
 */


use prometheus::{Gauge, GaugeVec, Opts};

struct DatabaseStats {
    guild_configs: Gauge,
    songs:  Gauge,
    voice_joins: GaugeVec,
    commands_executed: Gauge,

    latency: GaugeVec,
    connections: GaugeVec,
}

lazy_static!(
    static ref DATABASE_STATS: DatabaseStats = DatabaseStats {
    guild_configs: Gauge::new("database_guild_configs", "The number of guild configs in the database").unwrap(),
    songs: Gauge::new("database_songs", "The number of songs in the database").unwrap(),
    voice_joins: GaugeVec::new(
        Opts::new("database_voice_joins", "The number of voice joins in the database"),
        &["channel_id"]
    ).unwrap(),
    commands_executed: Gauge::new("database_commands_executed", "The number of commands executed").unwrap(),

    latency: GaugeVec::new(
        Opts::new("database_latency", "The latency of the database"),
        &["database"]
    ).unwrap(),
    connections: GaugeVec::new(
        Opts::new("database_connections", "The number of connections to the database"),
        &["database"]
    ).unwrap(),
};
);

pub fn register() {
    prometheus::register(Box::new(DATABASE_STATS.guild_configs.clone())).unwrap();
    prometheus::register(Box::new(DATABASE_STATS.songs.clone())).unwrap();
    prometheus::register(Box::new(DATABASE_STATS.voice_joins.clone())).unwrap();
    prometheus::register(Box::new(DATABASE_STATS.commands_executed.clone())).unwrap();
    prometheus::register(Box::new(DATABASE_STATS.latency.clone())).unwrap();
    prometheus::register(Box::new(DATABASE_STATS.connections.clone())).unwrap();
}