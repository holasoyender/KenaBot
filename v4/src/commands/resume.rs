/*
 * File:          resume.rs
 * Path:          v4\src\commands\resume.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: d4bab757a7b521969ba234e2177204fd2439d19dcf8f1905b864471d32d8f431
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

pub(crate) struct ResumeCommand;

#[async_trait]
impl Command for ResumeCommand {
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
            player.set_pause(false).await.unwrap();

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.pause.resumed);

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
        CreateCommand::new("resume")
            .description("Resume the current song.")
            .description_localized(Locales::SPANISH.value(), "Reanudar la canción actual.")
            .description_localized(Locales::ENGLISH_US.value(), "Resume the current song.")
            .description_localized(Locales::ENGLISH_UK.value(), "Resume the current song.")
            .description_localized(Locales::FRENCH.value(), "Reprendre la chanson en cours.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Retomar a música atual.")
            .description_localized(Locales::ITALIAN.value(), "Riprendi la canzone corrente.")
            .description_localized(Locales::GERMAN.value(), "Setzen Sie den aktuellen Song fort.")
            .description_localized(Locales::UKRAINIAN.value(), "Відновити поточну пісню.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}