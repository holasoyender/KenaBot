/*
 * File:          commands.rs
 * Path:          v4\src\commands\commands.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 2dc72ceff63f3abf7b0cc42329430fc06053ee9fe3250650c9803f41b1a43ff1
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, CreateEmbed, CreateEmbedAuthor, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct CommandsCommand;

#[async_trait]
impl Command for CommandsCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        let bot_name = event_handler.bot_name.read().clone().unwrap();
        let bot_avatar = context.cache.current_user().avatar_url().unwrap();

        let embed = CreateEmbed::new()
            .author(CreateEmbedAuthor::new(bot_name.clone())
                .icon_url(bot_avatar.clone()))
            .color(config.color)
            .description(lang.commands.commands);

        command_interaction.reply()
            .embed(embed)
            .link_button(lang.commands.help.commands.title, "https://kenabot.xyz/commands", None, false)
            .exec(&context.http)
            .await.unwrap_or(());

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("commands")
            .name_localized(Locales::SPANISH.value(), "comandos")
            .description("List of the bot's commands")
            .description_localized(Locales::SPANISH.value(), "Lista de comandos del bot")
            .description_localized(Locales::ENGLISH_US.value(), "List of the bot's commands")
            .description_localized(Locales::ENGLISH_UK.value(), "List of the bot's commands")
            .description_localized(Locales::FRENCH.value(), "Liste des commandes du bot")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Lista de comandos do bot")
            .description_localized(Locales::ITALIAN.value(), "Elenco dei comandi del bot")
            .description_localized(Locales::GERMAN.value(), "Liste der Befehle des Bots")
            .description_localized(Locales::UKRAINIAN.value(), "Список команд бота")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}