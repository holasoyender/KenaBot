/*
 * File:          ping.rs
 * Path:          v4\src\commands\dev\ping.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: df0c2c6b87d404be4eb24df91bb80bcc95227e96ceac7074059b8fd08d1beb21
 *
 */


use async_trait::async_trait;
use skua::all::{Context, Message};
use skua::all::standard::CommandError;
use crate::discord::Handler;
use crate::managers::dev_command::DevCommand;
use crate::util;

pub(crate) struct DevPingCommand;

#[async_trait]
impl DevCommand for DevPingCommand {
    async fn run(&mut self, context: Context, message: Message, _: Vec<&str>, _: &Handler) -> Result<(), CommandError> {
        let shard_latency = util::shards::get_shard_ping(&context).await;

        message.reply(&context.http, format!("{} Pong! `{}ms`", shared::emojis::EMOJIS.ping_pong.normal(), shard_latency))
            .await?;

        Ok(())
    }

    fn name(&self) -> &str {
        "ping"
    }

    fn description(&self) -> &str {
        "Información sobre la latencia del bot"
    }
}