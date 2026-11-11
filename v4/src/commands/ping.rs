/*
 * File:          ping.rs
 * Path:          v4\src\commands\ping.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 8cbd27771b4357f52498edc1cb2fdc7563afcb7a81f597848f6b49f34000b4ff
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::util;

pub(crate) struct PingCommand;

#[async_trait]
impl Command for PingCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, _: Guild, _: Language, _: &Handler) -> Result<(), CommandError> {
        let shard_latency = util::shards::get_shard_ping(&context).await;

        command_interaction.reply()
            .emoji(shared::emojis::EMOJIS.ping_pong)
            .content(format!("Pong! `{}ms`", shard_latency))
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("ping")
            .description("Show the bot's ping")
            .description_localized(Locales::SPANISH.value(), "Mostrar el ping del bot")
            .description_localized(Locales::ENGLISH_US.value(), "Show the bot's ping")
            .description_localized(Locales::ENGLISH_UK.value(), "Show the bot's ping")
            .description_localized(Locales::FRENCH.value(), "Afficher le ping du bot")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Mostrar o ping do bot")
            .description_localized(Locales::ITALIAN.value(), "Mostra il ping del bot")
            .description_localized(Locales::GERMAN.value(), "Zeigen Sie den Ping des Bots an")
            .description_localized(Locales::UKRAINIAN.value(), "Показати пінг бота")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}