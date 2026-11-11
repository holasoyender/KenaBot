/*
 * File:          seek.rs
 * Path:          v4\src\commands\seek.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 9cb5318a0f36db483f7fc0ab6b72078fc8e229b2139b66fdc984a2ecccaedf30
 *
 */


use std::time::Duration;
use async_trait::async_trait;
use skua::all::{CommandOptionType, Context, CreateCommand, CreateCommandOption, CreateEmbed, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::util::ms::ms;
use crate::util::string::player_timestamp;

pub(crate) struct SeekCommand;

#[async_trait]
impl Command for SeekCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        let input = if let Some(ResolvedOption { value: ResolvedValue::String(i), .. }) = command_interaction.data.options().first() {
            i.to_string()
        } else {
            return Ok(());
        };

        let seek = ms(&input);
        if seek < 1 {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.commands.seek.invalid_time)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        }
        let seek = seek as u64;

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

            let now_playing = now_playing.unwrap();
            if !now_playing.info.is_seekable {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(&lang.commands.seek.not_seekable)
                    .emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }

            let position = now_playing.info.position.clone();
            let duration = now_playing.info.length.clone();

            if seek <= duration {

                player.set_position(Duration::from_millis(seek)).await.unwrap();

                let mut embed = CreateEmbed::new()
                    .color(config.color);

                let timestamp = format!("`{} / {}`", player_timestamp(&seek), player_timestamp(&duration));
                if seek > position {
                    embed = embed.description(lang.commands.seek.forward_success.format(&[timestamp.as_str()]))
                } else {
                    embed = embed.description(lang.commands.seek.backward_success.format(&[timestamp.as_str()]))
                }

                command_interaction.reply()
                    .embed(embed)
                    .exec(&context.http)
                    .await?;

            } else {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(&lang.commands.seek.max_duration)
                    .emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }

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
        CreateCommand::new("seek")
            .description("Skip to a specific moment of the song.")
            .description_localized(Locales::SPANISH.value(), "Saltar a un momento específico de la canción.")
            .description_localized(Locales::ENGLISH_US.value(), "Skip to a specific moment of the song.")
            .description_localized(Locales::ENGLISH_UK.value(), "Skip to a specific moment of the song.")
            .description_localized(Locales::FRENCH.value(), "Passer à un moment spécifique de la chanson.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Pule para um momento específico da música.")
            .description_localized(Locales::ITALIAN.value(), "Salta a un momento specifico della canzone.")
            .description_localized(Locales::GERMAN.value(), "Springe zu einem bestimmten Moment des Songs.")
            .description_localized(Locales::UKRAINIAN.value(), "Перейдіть до певного моменту пісні.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::String, "time", "Song moment to skip to, for example: 1m, 30s, etc...")
                    .required(true)
                    .description_localized(Locales::SPANISH.value(), "Momento de la canción, por ejemplo: 1m, 30s, etc...")
                    .description_localized(Locales::ENGLISH_US.value(), "Song moment to skip to, for example: 1m, 30s, etc...")
                    .description_localized(Locales::ENGLISH_UK.value(), "Song moment to skip to, for example: 1m, 30s, etc...")
                    .description_localized(Locales::FRENCH.value(), "Moment de la chanson, par exemple: 1m, 30s, etc...")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Momento da música, por exemplo: 1m, 30s, etc...")
                    .description_localized(Locales::ITALIAN.value(), "Momento della canzone, per esempio: 1m, 30s, ecc...")
                    .description_localized(Locales::GERMAN.value(), "Song Moment zu überspringen, zum Beispiel: 1m, 30s, etc...")
                    .description_localized(Locales::UKRAINIAN.value(), "Момент пісні, наприклад: 1m, 30s, тощо...")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}