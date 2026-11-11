/*
 * File:          back.rs
 * Path:          v4\src\commands\back.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: b4dad13354154b70b9aa5dddd247639fc68248b02160bba565702d35d9460fcb
 *
 */


use async_trait::async_trait;
use skua::all::{Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::atenea::player_context::QueueMessage;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::io::voice::RepeatMode;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct BackCommand;

#[async_trait]
impl Command for BackCommand {
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

        let last_track = player.last_track.clone();

        if last_track.is_some() {
            player.set_repeat_mode(RepeatMode::None).await.unwrap();
            player.set_queue(QueueMessage::PushToFront(last_track.unwrap().into())).await?;
            player.skip().await.unwrap();

            let embed = CreateEmbed::new()
                .color(config.color)
                .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.back.success);

            command_interaction.reply()
                .embed(embed)
                .exec(&context.http)
                .await?;
        } else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.commands.back.no_previous)
                .emphemeral()
                .exec(&context.http)
                .await?;
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("back")
            .description("Go to the previous song in the queue.")
            .description_localized(Locales::SPANISH.value(), "Ir a la canción anterior de la cola de reproducción.")
            .description_localized(Locales::ENGLISH_US.value(), "Go to the previous song in the queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Go to the previous song in the queue.")
            .description_localized(Locales::FRENCH.value(), "Aller à la chanson précédente dans la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Ir para a música anterior na fila.")
            .description_localized(Locales::ITALIAN.value(), "Vai alla canzone precedente nella coda.")
            .description_localized(Locales::GERMAN.value(), "Gehe zum vorherigen Song in der Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Перейдіть до попередньої пісні у черзі.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}