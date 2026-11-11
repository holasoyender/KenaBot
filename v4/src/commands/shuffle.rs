/*
 * File:          shuffle.rs
 * Path:          v4\src\commands\shuffle.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 9ea72ac86851b596bb29099216c0b41f3d3a44dafc4c82e1c3b5f1144e5c0f82
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct ShuffleCommand;

#[async_trait]
impl Command for ShuffleCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {
        let guild_id = command_interaction.guild_id.unwrap();
        let bot_id = event_handler.bot_id.read().unwrap();

        match check_voice_connections(&guild_id, &context.cache, &command_interaction.user.id, &bot_id) {
            VoiceConnection::Neither | VoiceConnection::Separate(_, _) => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(lang.manager.cant_modify_queue)
                    .emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            },
            VoiceConnection::User(_) | VoiceConnection::Bot(_) => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(&lang.manager.nothing_playing)
                    .emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }
            _ => {}
        };

        let atenea_client = {
            let data_read = context.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let Some(mut player) = atenea_client.get_player_context(guild_id).await else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        };

        let now_playing = player.player_data.track.clone();

        if now_playing.is_some() {

            player.shuffle_queue().await?;

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.shuffle);

            command_interaction.reply()
                .embed(embed)
                .exec(&context.http)
                .await?;
        } else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("shuffle")
            .description("Shuffle the queue.")
            .description_localized(Locales::SPANISH.value(), "Poner la lista de reproducción en orden aleatorio.")
            .description_localized(Locales::ENGLISH_US.value(), "Shuffle the queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Shuffle the queue.")
            .description_localized(Locales::FRENCH.value(), "Mélanger la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Embaralhar a fila de reprodução.")
            .description_localized(Locales::ITALIAN.value(), "Mischia la coda delle canzoni.")
            .description_localized(Locales::GERMAN.value(), "Mischen Sie die Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Перемішайте чергу.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}