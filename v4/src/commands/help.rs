/*
 * File:          help.rs
 * Path:          v4\src\commands\help.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: ca955c808a95f4b92b3e786998fcbd3ec2223f551b7f08ae82fdced0eada5be7
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use skua::builder::CreateEmbedAuthor;
use shared::constants::get_constants;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct HelpCommand;

#[async_trait]
impl Command for HelpCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        let bot_name = event_handler.bot_name.read().clone().unwrap();
        let bot_id = event_handler.bot_id.read().clone().unwrap().to_string();
        let bot_avatar = context.cache.current_user().avatar_url().unwrap();

        let embed = CreateEmbed::new()
            .author(CreateEmbedAuthor::new(bot_name.clone())
                .icon_url(bot_avatar.clone()))
            .color(config.color)
            .description(lang.commands.help.desc.format(&[bot_name.as_str(), bot_id.as_str()]));

        let constants = get_constants(&event_handler.cache).await;

        command_interaction.reply()
            .embed(embed)
            .link_button(lang.commands.help.commands.title, format!("{}/commands", constants.website), None, false)
            .link_button(lang.commands.help.add.title, format!("https://discord.com/api/oauth2/authorize?client_id={}&permissions=3534272&scope=bot%20applications.commands", event_handler.bot_id.read().clone().unwrap().to_string()), None, false)
            .link_button(lang.commands.help.support.title, constants.support_invite, None, false)
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("help")
            .description("Useful information about the bot")
            .description_localized(Locales::SPANISH.value(), "Información útil sobre el bot")
            .description_localized(Locales::ENGLISH_US.value(), "Useful information about the bot")
            .description_localized(Locales::ENGLISH_UK.value(), "Useful information about the bot")
            .description_localized(Locales::FRENCH.value(), "Informations utiles sur le bot")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Informações úteis sobre o bot")
            .description_localized(Locales::ITALIAN.value(), "Informazioni utili sul bot")
            .description_localized(Locales::GERMAN.value(), "Nützliche Informationen über den Bot")
            .description_localized(Locales::UKRAINIAN.value(), "Корисна інформація про бота")

    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}