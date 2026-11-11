/*
 * File:          autoplay.rs
 * Path:          v4\src\commands\autoplay.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 0f80e2bd1e80d0340b30fd401db0daad41537ce42686fd89d1da4f509a38540e
 *
 */


use async_trait::async_trait;
use mongodb::bson::doc;
use skua::all::{CommandOptionType, Context, CreateCommand, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use skua::builder::CreateCommandOption;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct AutoplayCommand;

#[async_trait]
impl Command for AutoplayCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        let input = if let Some(ResolvedOption { value: ResolvedValue::Integer(i), .. }) = command_interaction.data.options().first() {
            i == &0
        } else {
            return Ok(());
        };


        let (current_state, new_state) = if config.autoplay {
            (lang.commands.autoplay.enabled, lang.commands.autoplay.disabled)
        } else {
            (lang.commands.autoplay.disabled, lang.commands.autoplay.enabled)
        };

        if config.autoplay == input {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.commands.autoplay.already_in.format(&[current_state.as_str()]))
                .emphemeral()
                .exec(&context.http)
                .await.unwrap_or(());
            return Ok(());
        }

        match event_handler.db.collection::<Guild>("Guilds").find_one_and_update(doc! {"guildID": config.guild_id}, doc! {"$set": {"autoplay": input}}, None).await {
            Ok(_) => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.tick)
                    .content(lang.commands.autoplay.success.format(&[new_state.as_str()]))
                    .exec(&context.http)
                    .await.unwrap_or(());
            },
            Err(_) => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content("Unknown database error, please try again later")
                    .emphemeral()
                    .exec(&context.http)
                    .await.unwrap_or(());
            }
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("autoplay")
            .description("Enable or disable the autoplay mode.")
            .description_localized(Locales::SPANISH.value(), "Activar o desactivar el modo de reproducción automática.")
            .description_localized(Locales::ENGLISH_US.value(), "Enable or disable the autoplay mode.")
            .description_localized(Locales::ENGLISH_UK.value(), "Enable or disable the autoplay mode.")
            .description_localized(Locales::FRENCH.value(), "Activer ou désactiver le mode de lecture automatique.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Ative ou desative o modo de reprodução automática.")
            .description_localized(Locales::ITALIAN.value(), "Abilita o disabilita la modalità di riproduzione automatica.")
            .description_localized(Locales::GERMAN.value(), "Aktivieren oder deaktivieren Sie den Autoplay-Modus.")
            .description_localized(Locales::UKRAINIAN.value(), "Увімкніть або вимкніть режим автовідтворення.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::Integer, "state", "Autoplay state")
                    .required(true)
                    .description_localized(Locales::SPANISH.value(), "Estado de la reproducción automática")
                    .description_localized(Locales::ENGLISH_US.value(), "Autoplay state")
                    .description_localized(Locales::ENGLISH_UK.value(), "Autoplay state")
                    .description_localized(Locales::FRENCH.value(), "État de lecture automatique")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Estado de reprodução automática")
                    .description_localized(Locales::ITALIAN.value(), "Stato di riproduzione automatica")
                    .description_localized(Locales::GERMAN.value(), "Autoplay-Status")
                    .description_localized(Locales::UKRAINIAN.value(), "Стан автовідтворення")
                    .add_int_choice("On", 0)
                    .add_int_choice("Off", 1)
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}