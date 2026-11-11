/*
 * File:          shards.rs
 * Path:          v4\src\events\shards.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: a7ba0227f292330bd6a584b1ab1985713e09a2dcaeb41246459d4c16591208bb
 *
 */


use skua::all::{ConnectionStage, Context, ShardStageUpdateEvent};
use crate::discord::Handler;

pub async fn shard_stage_update(_: &Handler, _: Context, event: ShardStageUpdateEvent) {
    match event.new {
        ConnectionStage::Connected => {
            info!("Shard {} has connected", event.shard_id);
        }
        ConnectionStage::Connecting => {
            info!("Shard {} is reconnecting", event.shard_id);
        }
        ConnectionStage::Disconnected => {
            info!("Shard {} has disconnected", event.shard_id);
        }
        ConnectionStage::Identifying => {
            info!("Shard {} is identifying", event.shard_id);
        }
        ConnectionStage::Resuming => {
            info!("Shard {} is resuming", event.shard_id);
        }
        ConnectionStage::Handshake => {
            info!("Shard {} is handshaking", event.shard_id);
        }
        _ => {}
    }
}