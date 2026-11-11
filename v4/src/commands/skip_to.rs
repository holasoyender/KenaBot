/*
 * File:          skip_to.rs
 * Path:          v4\src\commands\skip_to.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: ea3b6b1813395a68b8f6ed84a22aae9540e17a9761dc1a8fbbdeb10bc85f91d7
 *
 */


use std::collections::VecDeque;
use async_trait::async_trait;
use skua::all::{CommandOptionType, Context, CreateCommand, CreateCommandOption, CreateEmbed, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use crate::atenea::player_context::QueueMessage;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::io::voice::RepeatMode;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct SkipToCommand;

#[async_trait]
impl Command for SkipToCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {
        let input = if let Some(ResolvedOption { value: ResolvedValue::Integer(i), .. }) = command_interaction.data.options().first() {
            i.to_owned()
        } else {
            return Ok(());
        };

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

        let queue = player.queue.clone();
        if queue.len() < input as usize {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.commands.remove.invalid.format(&[input.to_string().as_str()]))
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        }

        let index = input as usize - 1;

        let new_queue = queue.into_iter().skip(index).collect::<VecDeque<_>>();
        player.set_queue(QueueMessage::Replace(new_queue)).await?; // i should probably check if this really works
        player.set_repeat_mode(RepeatMode::None).await?;

        player.skip().await?;

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.skip_to.format(&[input.to_string().as_str()]));

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("skipto")
            .description("Skip to a specific song in the queue.")
            .description_localized(Locales::SPANISH.value(), "Saltar a una canción de la lista de reproducción en específico.")
            .description_localized(Locales::ENGLISH_US.value(), "Skip to a specific song in the queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Skip to a specific song in the queue.")
            .description_localized(Locales::FRENCH.value(), "Passer à une chanson spécifique dans la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Pule para uma música específica na fila.")
            .description_localized(Locales::ITALIAN.value(), "Salta a una canzone specifica nella coda.")
            .description_localized(Locales::GERMAN.value(), "Springe zu einem bestimmten Lied in der Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Перейдіть до певної пісні у черзі.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::Integer, "song", "Number in the queue of the song to play")
                    .required(true)
                    .min_int_value(1)
                    .description_localized(Locales::SPANISH.value(), "Número en la lista de reproducción de la canción a reproducir")
                    .description_localized(Locales::ENGLISH_US.value(), "Number in the queue of the song to play")
                    .description_localized(Locales::ENGLISH_UK.value(), "Number in the queue of the song to play")
                    .description_localized(Locales::FRENCH.value(), "Numéro dans la file d'attente de la chanson à jouer")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Número na fila da música a ser reproduzida")
                    .description_localized(Locales::ITALIAN.value(), "Numero nella coda della canzone da riprodurre")
                    .description_localized(Locales::GERMAN.value(), "Nummer in der Warteschlange des Songs, der gespielt werden soll")
                    .description_localized(Locales::UKRAINIAN.value(), "Номер у черзі пісні, яку потрібно відтворити")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}