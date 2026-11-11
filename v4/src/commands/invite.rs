/*
 * File:          invite.rs
 * Path:          v4\src\commands\invite.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: f8c54166fa77cb21cb9a07c47739f031ae34eef7a96a201c4973f22d7d04c2c3
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

pub(crate) struct InviteCommand;

#[async_trait]
impl Command for InviteCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, _: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        command_interaction.reply()
            .emoji(shared::emojis::EMOJIS.tick)
            .content(lang.commands.invite)
            .emphemeral()
            .link_button(lang.commands.help.add.add, format!("https://discord.com/api/oauth2/authorize?client_id={}&permissions=3534272&scope=bot%20applications.commands", event_handler.bot_id.read().clone().unwrap().to_string()), None, false)
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("invite")
            .description("Send an invitation to add me to your server")
            .description_localized(Locales::SPANISH.value(), "Envía una invitación para añadirme a su servidor")
            .description_localized(Locales::ENGLISH_US.value(), "Send an invitation to add me to your server")
            .description_localized(Locales::ENGLISH_UK.value(), "Send an invitation to add me to your server")
            .description_localized(Locales::FRENCH.value(), "Envoyer une invitation pour m'ajouter à votre serveur")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Envie um convite para me adicionar ao seu servidor")
            .description_localized(Locales::ITALIAN.value(), "Invia un invito per aggiungermi al tuo server")
            .description_localized(Locales::GERMAN.value(), "Senden Sie eine Einladung, um mich zu Ihrem Server hinzuzufügen")
            .description_localized(Locales::UKRAINIAN.value(), "Надішліть запрошення, щоб додати мене до свого сервера")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}