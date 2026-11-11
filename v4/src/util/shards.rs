/*
 * File:          shards.rs
 * Path:          v4\src\util\shards.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 7bccee945bd1ca778f53465d2cedb24fe824eb1c3ed6e16f40750be85f607b92
 *
 */


use skua::all::{Cache, Context, GuildId, ShardId};
use crate::globals::ShardManagerContainer;

pub async fn get_shard_ping(context: &Context) -> String {
    let shard_latency = {
        let shard_manager = {
            let data_read = context.data.read().await;
            data_read.get::<ShardManagerContainer>().unwrap().clone()
        };

        let runners = shard_manager.runners.lock().await;

        let runner_raw = runners.get(&ShardId(context.shard_id.0));
        if let Some(runner) = runner_raw {
            match runner.latency {
                Some(ms) => format!("{}", ms.as_millis()),
                _ => "50".to_string(),
            }
        } else {
            "50".to_string()
        }
    };
    shard_latency
}

pub fn get_user_count(guilds: Vec<GuildId>, cache: &Cache) -> u64 {
    guilds.iter().map(|guild| match cache.guild(guild) {
        Some(x) => x.member_count,
        None => 0
    }).sum()
}