/*
 * File:          pause.rs
 * Path:          v4\src\commands\pause.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 1b840ae664a2b595be81ca68138aa2e72bd548ed70a83a71bde459e76812b638
 *
 */


use async_trait::async_trait;
use skua::all::{ComponentInteraction, Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct PauseCommand;

#[async_trait]
impl Command for PauseCommand {
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
            player.set_pause(true).await.unwrap();

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.pause.paused);

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
        CreateCommand::new("pause")
            .description("Pause the current song.")
            .description_localized(Locales::SPANISH.value(), "Pausar la canción actual.")
            .description_localized(Locales::ENGLISH_US.value(), "Pause the current song.")
            .description_localized(Locales::ENGLISH_UK.value(), "Pause the current song.")
            .description_localized(Locales::FRENCH.value(), "Mettre en pause la chanson en cours.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Pausar a música atual.")
            .description_localized(Locales::ITALIAN.value(), "Metti in pausa la canzone corrente.")
            .description_localized(Locales::GERMAN.value(), "Pausiere den aktuellen Song.")
            .description_localized(Locales::UKRAINIAN.value(), "Призупинити поточну пісню.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

#[async_trait]
impl Button for PauseCommand {
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
            player.set_pause(!player.player_data.paused).await.unwrap();

            let action = if player.player_data.paused {
                lang.commands.pause.paused
            } else {
                lang.commands.pause.resumed
            };

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*action);

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