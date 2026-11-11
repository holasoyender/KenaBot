/*
 * File:          clear_queue.rs
 * Path:          v4\src\commands\clear_queue.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 45061e4ce36f93a4302d28b2150fea7ff47f5c52f389420149d7c92b4b559b78
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
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct ClearQueueCommand;

#[async_trait]
impl Command for ClearQueueCommand {
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

        player.set_queue(QueueMessage::Clear).await?;

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.clear_queue);

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("clearqueue")
            .description("Clears the current queue.")
            .description_localized(Locales::SPANISH.value(), "Vacía la lista de reproducción.")
            .description_localized(Locales::ENGLISH_US.value(), "Clears the current queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Clears the current queue.")
            .description_localized(Locales::FRENCH.value(), "Efface la file d'attente actuelle.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Limpa a fila atual.")
            .description_localized(Locales::ITALIAN.value(), "Cancella la coda corrente.")
            .description_localized(Locales::GERMAN.value(), "Löscht die aktuelle Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Очищає поточну чергу.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}