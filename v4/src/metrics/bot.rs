/*
 * File:          bot.rs
 * Path:          v4\src\metrics\bot.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 2dc7fabbba68fdba50cbb1d914c064570d17b18a5312b895e5f2ed57d399c02e
 *
 */


use prometheus::{Gauge, GaugeVec, Opts};

struct BotStats {
    shard_latency: GaugeVec,
    rest_latency: Gauge,
    guild_count: Gauge,
    user_count: Gauge,
    websocket_events: GaugeVec,
}

lazy_static! (
    static ref BOT_STATS: BotStats = BotStats {
    shard_latency: GaugeVec::new(
        Opts::new("bot_shard_latency", "The latency of the bot's shards"),
        &["shard"],
    ).unwrap(),
    rest_latency: Gauge::new("bot_rest_latency", "The latency of the bot's rest requests").unwrap(),
    guild_count: Gauge::new("bot_guild_count", "The number of guilds the bot is in").unwrap(),
    user_count: Gauge::new("bot_user_count", "The number of users the bot can see").unwrap(),
    websocket_events: GaugeVec::new(
        Opts::new("bot_websocket_events", "The number of websocket events the bot has received"),
        &["event"],
    ).unwrap(),
};
);

pub(crate) fn register() {
    prometheus::register(Box::new(BOT_STATS.shard_latency.clone())).unwrap();
    prometheus::register(Box::new(BOT_STATS.rest_latency.clone())).unwrap();
    prometheus::register(Box::new(BOT_STATS.guild_count.clone())).unwrap();
    prometheus::register(Box::new(BOT_STATS.user_count.clone())).unwrap();
    prometheus::register(Box::new(BOT_STATS.websocket_events.clone())).unwrap();
}

pub fn set_shard_latency(shard: u64, latency: f64) {
    BOT_STATS.shard_latency.with_label_values(&[&shard.to_string()]).set(latency);
}

pub fn set_rest_latency(latency: f64) {
    BOT_STATS.rest_latency.set(latency);
}

pub fn set_guild_count(count: u64) {
    BOT_STATS.guild_count.set(count as f64);
}

pub fn set_user_count(count: u64) {
    BOT_STATS.user_count.set(count as f64);
}

pub fn set_websocket_event(event: String, count: u64) {
    BOT_STATS.websocket_events.with_label_values(&[&event]).set(count as f64);
}

pub fn inc_guild_count() {
    BOT_STATS.guild_count.inc();
}

pub fn dec_guild_count() {
    BOT_STATS.guild_count.dec();
}

pub fn inc_user_count() {
    BOT_STATS.user_count.inc();
}

pub fn dec_user_count() {
    BOT_STATS.user_count.dec();
}

pub fn inc_websocket_event(event: String) {
    BOT_STATS.websocket_events.with_label_values(&[&event]).inc();
}