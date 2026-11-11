/*
 * File:          now_playing.rs
 * Path:          v4\src\commands\now_playing.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 42457b152c9ed98b1a45bb2ab1442ef5e3bac4643481c54ff76840440e6df8d3
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, CreateEmbed, CreateEmbedAuthor, CreateEmbedFooter, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::util::string::{format_song_title, player_timestamp, progress};

pub(crate) struct NowPlayingCommand;

#[async_trait]
impl Command for NowPlayingCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {
        let guild_id = command_interaction.guild_id.unwrap();
        let bot_id = event_handler.bot_id.read().unwrap();

        match check_voice_connections(&guild_id, &context.cache, &command_interaction.user.id, &bot_id) {
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

        let Some(player_context) = atenea_client.get_player_context(guild_id).await else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        };

        let player = player_context.player_data;

        let Some(now_playing) = player.track else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        };

        let seek = player.state.position;
        let left = now_playing.info.length - seek;

        let icon_url = command_interaction.user.avatar_url().unwrap_or(context.cache.current_user().avatar_url().unwrap());

        let mut embed = CreateEmbed::default()
            .color(config.color)
            .author(CreateEmbedAuthor::new(format_song_title(&now_playing.info.title)).url(now_playing.info.uri.unwrap_or("https://kenabot.xyz".to_string())).icon_url(icon_url))
            .description(format!("`{}` {} `{}`", player_timestamp(&seek), progress(&now_playing.info.length, &seek, 20, "▬", "🔵"), player_timestamp(&now_playing.info.length)));

        if left > 0 && now_playing.info.length > 0 {
            embed = embed.footer(CreateEmbedFooter::new(format!("{} {}", lang.commands.now_playing.remaining, player_timestamp(&left))));
        }

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("nowplaying")
            .description("Information about the song that is playing.")
            .description_localized(Locales::SPANISH.value(), "Información sobre la canción sonando.")
            .description_localized(Locales::ENGLISH_US.value(), "Information about the song that is playing.")
            .description_localized(Locales::ENGLISH_UK.value(), "Information about the song that is playing.")
            .description_localized(Locales::FRENCH.value(), "Informations sur la chanson qui joue.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Informações sobre a música que está tocando.")
            .description_localized(Locales::ITALIAN.value(), "Informazioni sulla canzone che sta suonando.")
            .description_localized(Locales::GERMAN.value(), "Informationen über das Lied, das gespielt wird.")
            .description_localized(Locales::UKRAINIAN.value(), "Інформація про пісню, яка грає.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}