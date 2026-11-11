/*
 * File:          remove.rs
 * Path:          v4\src\commands\remove.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: b91cc4d9bd7466465835795d71f420073447aa49175fec65b32078e3171989b7
 *
 */


use async_trait::async_trait;
use skua::all::{CommandOptionType, Context, CreateCommand, CreateCommandOption, CreateEmbed, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use crate::atenea::player_context::QueueMessage;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct RemoveCommand;

#[async_trait]
impl Command for RemoveCommand {
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
        let song = queue.get(index).unwrap().clone();
        player.set_queue(QueueMessage::Remove(index)).await?;

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.remove.success.format(&[song.track.info.title.as_str()]));

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("remove")
            .description("Removes a song from the queue given it's index.")
            .description_localized(Locales::SPANISH.value(), "Quitar una canción de la lista de reproducción.")
            .description_localized(Locales::ENGLISH_US.value(), "Removes a song from the queue given it's index.")
            .description_localized(Locales::ENGLISH_UK.value(), "Removes a song from the queue given it's index.")
            .description_localized(Locales::FRENCH.value(), "Supprime une chanson de la file d'attente en fonction de son index.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Remove uma música da fila de reprodução com base no seu índice.")
            .description_localized(Locales::ITALIAN.value(), "Rimuove una canzone dalla coda in base al suo indice.")
            .description_localized(Locales::GERMAN.value(), "Entfernt einen Song aus der Warteschlange anhand seines Index.")
            .description_localized(Locales::UKRAINIAN.value(), "Видаляє пісню з черги за її індексом.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::Integer, "song", "Number in the queue of the song to remove")
                    .required(true)
                    .min_int_value(1)
                    .description_localized(Locales::SPANISH.value(), "Número en la lista de reproducción de la canción a eliminar")
                    .description_localized(Locales::ENGLISH_US.value(), "Number in the queue of the song to remove")
                    .description_localized(Locales::ENGLISH_UK.value(), "Number in the queue of the song to remove")
                    .description_localized(Locales::FRENCH.value(), "Numéro dans la file d'attente de la chanson à supprimer")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Número na fila da música a ser removida")
                    .description_localized(Locales::ITALIAN.value(), "Numero nella coda della canzone da rimuovere")
                    .description_localized(Locales::GERMAN.value(), "Nummer in der Warteschlange des zu entfernenden Songs")
                    .description_localized(Locales::UKRAINIAN.value(), "Номер у черзі пісні, яку потрібно видалити")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}