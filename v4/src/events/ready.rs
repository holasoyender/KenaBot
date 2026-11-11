/*
 * File:          ready.rs
 * Path:          v4\src\events\ready.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: afc02d12075726a149230811e2d8fa4c90bc376a183fe8f0d722c4fe13075381
 *
 */


use skua::all::{ActivityData, Command, Context, GuildId, Ready};
use skua::futures;
use crate::database::cache::stats::CacheStats;
use crate::discord::Handler;
use crate::metrics::bot::{set_guild_count, set_user_count};
use crate::util::shards::get_user_count;

pub async fn cache_ready(handler: &Handler, ctx: Context, guilds: Vec<GuildId>) {
    let bot_id = handler.bot_id.read().unwrap();
    let total_guilds = guilds.len();

    let stats = CacheStats {
        guild_count: total_guilds as u64,
        user_count: get_user_count(guilds, &ctx.cache),
        voice_connections: 0
    };
    stats.save(bot_id, &handler.cache).await;

    set_guild_count(total_guilds as u64);
    set_user_count(stats.user_count);

    info!("Cache is ready, {} guilds are cached", total_guilds);
}

pub async fn shards_ready(handler: &Handler, ctx: Context, total_shards: u32) {
    let bot_id = handler.bot_id.read().unwrap();
    info!("All {} shards are ready, bot should be online! ({}, {})", total_shards, ctx.cache.current_user().name, bot_id);

    /*let commands = handler.command_manager.commands.values().map(
        async |cmd| cmd.lock().await.register()
    ).collect::<Vec<CreateCommand>>();*/
    let futures = handler.command_manager.commands.values().map(|cmd| {
        let cmd = cmd.clone();
        async move {
            cmd.lock().await.register()
        }
    });
    let commands = futures::future::join_all(futures).await;
    let commands_len = &commands.len();

    Command::set_global_commands(&ctx.http, commands).await.unwrap();
    info!("Registered {} global commands", commands_len);
}

pub async fn ready(_: &Handler, ctx: Context, ready: Ready) {
    info!("Shard {} is ready!", ready.shard.unwrap().id);
    ctx.set_activity(Some(ActivityData::custom("/help - kenabot.xyz")));
}