/*
 * File:          queue.rs
 * Path:          v4\src\commands\queue.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: f10bb00ad312d9f5c8f9bc452eea8bf77976e15c7bf1a35a049f05a613f719da
 *
 */


use async_trait::async_trait;
use skua::all::{ComponentInteraction, Context, CreateCommand, CreateEmbed, CreateEmbedAuthor, CreateEmbedFooter, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::atenea::player_context::TrackInQueue;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::util::chunk_vecdeque;
use crate::util::string::{format_song_title, player_timestamp};

pub(crate) struct QueueCommand;

#[async_trait]
impl Command for QueueCommand {
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

        let Some(player) = atenea_client.get_player_context(guild_id).await else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        };

        let queue = player.queue;

        let queue_chunked: Vec<Vec<TrackInQueue>> = chunk_vecdeque(queue.clone(), 6);
        let now_playing = player.player_data.track;

        let bot_avatar = context.cache.current_user().avatar_url().unwrap();

        let mut embed = CreateEmbed::new()
            .color(config.color)
            .author(CreateEmbedAuthor::new(lang.commands.queue.embed_author).icon_url(bot_avatar))
            .footer(CreateEmbedFooter::new(lang.commands.queue.embed_footer.format(&["1", queue_chunked.len().to_string().as_str(), queue.len().to_string().as_str()])));

        if now_playing.is_some() {
            let now_playing = now_playing.unwrap();
            embed = embed.description(format!("```yml\n --- {} ---\n{} - {}```",
                lang.commands.queue.now_playing,
                format_song_title(&now_playing.info.title),
                player_timestamp(&now_playing.info.length)
            ));
        } else {
            if queue.len() == 0 {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(&lang.manager.nothing_playing)
                    .emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }
        }

        if queue_chunked.len() > 0 {
            for (index, track) in queue_chunked.first().unwrap().iter().enumerate() {
                let authors = &track.track.info.author;
                let duration = player_timestamp(&track.track.info.length);
                embed = embed.field(format!("{}. `{}`", index + 1, format_song_title(&track.track.info.title)), format!("**Author:** {}\n**Duration:** {}", authors, duration), true);
            }
        }

        command_interaction.reply()
            .embed(embed)
            .primary_button(shared::emojis::EMOJIS.left.normal(), format!("{}::cmd::queue::0:back", command_interaction.user.id), None, true)
            .primary_button(shared::emojis::EMOJIS.right.normal(), format!("{}::cmd::queue::0:next", command_interaction.user.id), None, queue_chunked.len() == 1)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("queue")
            .description("Show the queue.")
            .description_localized(Locales::SPANISH.value(), "Mostrar la lista de reproducción.")
            .description_localized(Locales::ENGLISH_US.value(), "Show the queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Show the queue.")
            .description_localized(Locales::FRENCH.value(), "Affiche la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Mostra a fila de reprodução.")
            .description_localized(Locales::ITALIAN.value(), "Mostra la coda.")
            .description_localized(Locales::GERMAN.value(), "Zeigt die Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Показати чергу.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

#[async_trait]
impl Button for QueueCommand {
    async fn button_click(ctx: Context, mut interaction: KenaComponentInteraction, _: &ComponentInteraction, config: Guild, lang: Language, _: &Handler, args: Vec<&str>) -> Result<(), CommandError> {
        let atenea_client = {
            let data_read = ctx.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let guild_id = interaction.guild_id.unwrap();

        let Some(player) = atenea_client.get_player_context(guild_id).await else {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
            return Ok(());
        };

        let queue = player.queue;
        let queue_chunked: Vec<Vec<TrackInQueue>> = chunk_vecdeque(queue.clone(), 6);
        let now_playing = player.player_data.track;

        let bot_avatar = ctx.cache.current_user().avatar_url().unwrap();

        if args.is_empty() { // show queue

            let mut embed = CreateEmbed::new()
                .color(config.color)
                .author(CreateEmbedAuthor::new(lang.commands.queue.embed_author).icon_url(bot_avatar))
                .footer(CreateEmbedFooter::new(lang.commands.queue.embed_footer.format(&["1", queue_chunked.len().to_string().as_str(), queue.len().to_string().as_str()])));

            if now_playing.is_some() {
                let now_playing = now_playing.unwrap();
                embed = embed.description(format!("```yml\n --- {} ---\n{} - {}```",
                                                  lang.commands.queue.now_playing,
                                                  format_song_title(&now_playing.info.title),
                                                  player_timestamp(&now_playing.info.length)
                ));
            } else {
                if queue.len() == 0 {
                    interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(&lang.manager.nothing_playing)
                        .emphemeral()
                        .exec(&ctx.http)
                        .await?;
                    return Ok(());
                }
            }

            if queue_chunked.len() > 0 {
                for (index, track) in queue_chunked.first().unwrap().iter().enumerate() {
                    let authors = &track.track.info.author;
                    let duration = player_timestamp(&track.track.info.length);
                    embed = embed.field(format!("{}. `{}`", index + 1, format_song_title(&track.track.info.title)), format!("**Author:** {}\n**Duration:** {}", authors, duration), true);
                }
            }

            interaction.reply()
                .embed(embed)
                .primary_button(shared::emojis::EMOJIS.left.normal(), format!("{}::cmd::queue::0:back", interaction.user.id), None, true)
                .primary_button(shared::emojis::EMOJIS.right.normal(), format!("{}::cmd::queue::0:next", interaction.user.id), None, queue_chunked.len() == 1)
                .emphemeral()
                .exec(&ctx.http)
                .await?;

            return Ok(())
        }

        if queue_chunked.len() == 0 {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
            return Ok(());
        }

        let current_page = args[0].parse::<usize>().unwrap();
        let action = args[1].to_string();

        let mut page = match action.as_str() {
            "back" => current_page - 1,
            "next" => current_page + 1,
            _ => current_page
        };

        if page > queue_chunked.len() - 1 {
            page = queue_chunked.len() - 1;
        }

        let mut embed = CreateEmbed::new()
            .color(config.color)
            .author(CreateEmbedAuthor::new(lang.commands.queue.embed_author).icon_url(bot_avatar))
            .footer(CreateEmbedFooter::new(lang.commands.queue.embed_footer.format(&[(page + 1).to_string().as_str(), queue_chunked.len().to_string().as_str(), queue.len().to_string().as_str()])));

        if now_playing.is_some() {
            let now_playing = now_playing.unwrap();
            embed = embed.description(format!("```yml\n --- {} ---\n{} - {}```",
                                              lang.commands.queue.now_playing,
                                              format_song_title(&now_playing.info.title),
                                              player_timestamp(&now_playing.info.length)
            ));
        }

        if queue_chunked.len() > 0 {
            for (index, track) in queue_chunked[page].iter().enumerate() {
                let authors = &track.track.info.author;
                let duration = player_timestamp(&track.track.info.length);
                embed = embed.field(format!("{}. `{}`", index + 1 + (page * 6), format_song_title(&track.track.info.title)), format!("**Author:** {}\n**Duration:** {}", authors, duration), true);
            }
        }

        interaction.edit()
            .embed(embed)
            .primary_button(shared::emojis::EMOJIS.left.normal(), format!("{}::cmd::queue::{}:back", interaction.user.id, page), None, page == 0)
            .primary_button(shared::emojis::EMOJIS.right.normal(), format!("{}::cmd::queue::{}:next", interaction.user.id, page), None, page == queue_chunked.len() - 1)
            .exec(&ctx.http)
            .await?;

        Ok(())
    }
}