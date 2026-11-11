/*
 * File:          support.rs
 * Path:          v4\src\commands\support.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: d23d9b6a7c8c5a9194462af5785d5d72dd72d3771c50d97c425d530ecb9180f5
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

pub(crate) struct SupportCommand;

#[async_trait]
impl Command for SupportCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, _: Guild, lang: Language, _: &Handler) -> Result<(), CommandError> {

        command_interaction.reply()
            .emoji(shared::emojis::EMOJIS.tick)
            .content(lang.commands.support)
            .emphemeral()
            .link_button(lang.commands.help.support.support, "https://discord.gg/kenabot-910907192989843548", None, false)
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("support")
            .name_localized(Locales::SPANISH.value(), "soporte")
            .description("Send an invite to the bot's support server")
            .description_localized(Locales::SPANISH.value(), "Envía una invitación al servidor de soporte del bot")
            .description_localized(Locales::ENGLISH_US.value(), "Send an invite to the bot's support server")
            .description_localized(Locales::ENGLISH_UK.value(), "Send an invite to the bot's support server")
            .description_localized(Locales::FRENCH.value(), "Envoyer une invitation au serveur de support du bot")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Enviar um convite para o servidor de suporte do bot")
            .description_localized(Locales::ITALIAN.value(), "Invia un invito al server di supporto del bot")
            .description_localized(Locales::GERMAN.value(), "Senden Sie eine Einladung zum Support-Server des Bots")
            .description_localized(Locales::UKRAINIAN.value(), "Надішліть запрошення на сервер підтримки бота")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}