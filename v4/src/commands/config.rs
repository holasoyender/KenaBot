/*
 * File:          config.rs
 * Path:          v4\src\commands\config.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 156c7ff07e01ad99a24f12a92b25533ca5aa7e8a948024fb19471b92e416cd17
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

pub(crate) struct ConfigCommand;

#[async_trait]
impl Command for ConfigCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, _: Guild, lang: Language, handler: &Handler) -> Result<(), CommandError> {

        let id = match handler.bot_name.read().clone().unwrap().to_lowercase().as_str() {
            "kenabot" => "1",
            "kenabot 2" => "2",
            "kenabot 3" => "3",
            "kenabot 4" => "4",
            _ => "5"
        };

        command_interaction.reply()
            .emoji(shared::emojis::EMOJIS.tick)
            .content(lang.commands.dashboard)
            .link_button("Dashboard", format!("https://kenabot.xyz/dashboard/{}/{}", command_interaction.guild_id.unwrap(), id), None, false)
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("config")
            .description("Link for the bot's web dashboard.")
            .description_localized(Locales::SPANISH.value(), "Enlace del menú web de configuración del bot.")
            .description_localized(Locales::ENGLISH_US.value(), "Link for the bot's web dashboard.")
            .description_localized(Locales::ENGLISH_UK.value(), "Link for the bot's web dashboard.")
            .description_localized(Locales::FRENCH.value(), "Lien pour le tableau de bord Web du bot.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Link para o painel da web do bot.")
            .description_localized(Locales::ITALIAN.value(), "Link per il pannello web del bot.")
            .description_localized(Locales::GERMAN.value(), "Link für das Web-Dashboard des Bots.")
            .description_localized(Locales::UKRAINIAN.value(), "Посилання на веб-панель бота.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![Permissions::MANAGE_GUILD]
    }
}