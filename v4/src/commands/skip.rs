/*
 * File:          skip.rs
 * Path:          v4\src\commands\skip.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: f1011ed703d4fdf43e9444ffedcda75f6ceb4241cb5369d64cc8bddae609c8b9
 *
 */


use async_trait::async_trait;
use skua::all::{ComponentInteraction, Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::io::voice::RepeatMode;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct SkipCommand;

#[async_trait]
impl Command for SkipCommand {
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
            if player.repeat_mode == RepeatMode::Track {
                player.set_repeat_mode(RepeatMode::None).await.unwrap();
            }

            player.skip().await.unwrap();

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.skip);

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
        CreateCommand::new("skip")
            .description("Skips the current song and plays the next one.")
            .description_localized(Locales::SPANISH.value(), "Salta la canción actual y reproduce la siguiente.")
            .description_localized(Locales::ENGLISH_US.value(), "Skips the current song and plays the next one.")
            .description_localized(Locales::ENGLISH_UK.value(), "Skips the current song and plays the next one.")
            .description_localized(Locales::FRENCH.value(), "Passe la chanson actuelle et joue la suivante.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Pula a música atual e toca a próxima.")
            .description_localized(Locales::ITALIAN.value(), "Salta la canzone corrente e ne riproduce un'altra.")
            .description_localized(Locales::GERMAN.value(), "Überspringt den aktuellen Song und spielt den nächsten.")
            .description_localized(Locales::UKRAINIAN.value(), "Пропускає поточну пісню та відтворює наступну.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

#[async_trait]
impl Button for SkipCommand {
    async fn button_click(ctx: Context, mut interaction: KenaComponentInteraction, _: &ComponentInteraction, config: Guild, lang: Language, _: &Handler, _: Vec<&str>) -> Result<(), CommandError> {

        let atenea_client = {
            let data_read = ctx.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let guild_id = interaction.guild_id.unwrap();

        let Some(mut player) = atenea_client.get_player_context(guild_id).await else {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
            return Ok(());
        };

        let now_playing = player.player_data.track.clone();

        if now_playing.is_some() {
            if player.repeat_mode == RepeatMode::Track {
                player.set_repeat_mode(RepeatMode::None).await.unwrap();
            }

            player.skip().await.unwrap();

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.skip);

            interaction.reply()
                .embed(embed)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
        } else {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.manager.nothing_playing)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
        }

        Ok(())
    }
}