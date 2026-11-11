/*
 * File:          radio.rs
 * Path:          v4\src\commands\radio.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 159d60469ec402fe4a73bdd424034af91b3f7334b59c926768d7613aea06c705
 *
 */


use async_trait::async_trait;
use mongodb::bson::doc;
use skua::all::{ChannelType, CommandOptionType, Context, CreateCommand, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use skua::builder::CreateCommandOption;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct RadioCommand;

#[async_trait]
impl Command for RadioCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        if let Some(ResolvedOption { value: ResolvedValue::Channel(input), .. }) = command_interaction.data.options().first() {
            // TODO: Entrar y salir de los canales de voz al activar/desactivar esto
            if input.kind != ChannelType::Voice && input.kind != ChannelType::Stage {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(lang.commands.radio.no_channel)
                    .emphemeral()
                    .exec(&context.http)
                    .await.unwrap_or(());
                return Ok(());
            }

            let channel_id = input.id.to_string();
            let channel_mention = format!("<#{}>", &channel_id);

            if config.radio.enabled && config.radio.channel_id == channel_id {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.tick)
                    .content(lang.commands.radio.enabled.format(&[channel_mention.as_str()]))
                    .exec(&context.http)
                    .await.unwrap_or(());
                return Ok(());
            }

            match event_handler.db.collection::<Guild>("Guilds").find_one_and_update(doc! {"guildID": config.guild_id}, doc! {"$set": {"_247": { "enabled": true, "channelID": channel_id}}}, None).await {
                Ok(_) => {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.tick)
                        .content(lang.commands.radio.enabled.format(&[channel_mention.as_str()]))
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

        } else {

            if !config.radio.enabled {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.tick)
                    .content(lang.commands.radio.disabled)
                    .exec(&context.http)
                    .await.unwrap_or(());
                return Ok(());
            }

            match event_handler.db.collection::<Guild>("Guilds").find_one_and_update(doc! {"guildID": config.guild_id}, doc! {"$set": {"_247": { "enabled": false, "channelID": ""}}}, None).await {
                Ok(_) => {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.tick)
                        .content(lang.commands.radio.disabled)
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
        };

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("247")
            .description("Set a 24/7 channel for the radio mode.")
            .description_localized(Locales::SPANISH.value(), "Establecer un canal de 24/7 para el modo radio.")
            .description_localized(Locales::ENGLISH_US.value(), "Set a 24/7 channel for the radio mode.")
            .description_localized(Locales::ENGLISH_UK.value(), "Set a 24/7 channel for the radio mode.")
            .description_localized(Locales::FRENCH.value(), "Activer ou désactiver le mode de lecture automatique.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Ative ou desative o modo de reprodução automática.")
            .description_localized(Locales::ITALIAN.value(), "Attiva o disattiva la modalità di riproduzione automatica.")
            .description_localized(Locales::GERMAN.value(), "Aktivieren oder deaktivieren Sie den Autoplay-Modus.")
            .description_localized(Locales::UKRAINIAN.value(), "Увімкніть або вимкніть режим автовідтворення.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::Channel, "channel", "The channel to set as 24/7")
                    .required(false)
                    .description_localized(Locales::SPANISH.value(), "El canal a establecer como 24/7")
                    .description_localized(Locales::ENGLISH_US.value(), "The channel to set as 24/7")
                    .description_localized(Locales::ENGLISH_UK.value(), "The channel to set as 24/7")
                    .description_localized(Locales::FRENCH.value(), "Le canal à définir comme 24/7")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "O canal a definir como 24/7")
                    .description_localized(Locales::ITALIAN.value(), "Il canale da impostare come 24/7")
                    .description_localized(Locales::GERMAN.value(), "Der Kanal, der als 24/7 festgelegt werden soll")
                    .description_localized(Locales::UKRAINIAN.value(), "Канал, який слід встановити як 24/7")
            )
    }

    fn is_premium(&self) -> bool {
        true
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}