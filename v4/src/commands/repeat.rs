/*
 * File:          repeat.rs
 * Path:          v4\src\commands\repeat.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-19 23:43:04 +01:00
 *
 * SHA-256: 86be30a83698c277a25ad087e0f8d5b8451c17d5d7b1b701a286758244880664
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
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct LoopCommand;

#[async_trait]
impl Command for LoopCommand {
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

        let Some(player) = atenea_client.get_player_context(guild_id).await else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        };

        let mut embed = CreateEmbed::new()
            .color(config.color);

        match player.repeat_mode {
            RepeatMode::None => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.no_repeat.as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .success_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, true)
                    .secondary_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, false)
                    .secondary_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, false)
                    .exec(&context.http)
                    .await?;
            }
            RepeatMode::Track => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.repeat_track.as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .secondary_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, false)
                    .success_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, true)
                    .secondary_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, false)
                    .exec(&context.http)
                    .await?;
            }
            RepeatMode::Queue => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.repeat_queue.as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .secondary_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, false)
                    .secondary_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, false)
                    .success_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, true)
                    .exec(&context.http)
                    .await?;
            }
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("loop")
            .description("Changue the queue loop state.")
            .description_localized(Locales::SPANISH.value(), "Cambiar el estado del bucle de la cola de reproducción.")
            .description_localized(Locales::ENGLISH_US.value(), "Changue the queue loop state.")
            .description_localized(Locales::ENGLISH_UK.value(), "Pause the current song.")
            .description_localized(Locales::FRENCH.value(), "Changer l'état de la boucle de la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Mudar o estado do loop da fila de reprodução.")
            .description_localized(Locales::ITALIAN.value(), "Cambia lo stato di loop della coda di riproduzione.")
            .description_localized(Locales::GERMAN.value(), "Ändern Sie den Wiederholungsstatus der Warteschlange.")
            .description_localized(Locales::UKRAINIAN.value(), "Змінити стан повторення черги.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

#[async_trait]
impl Button for LoopCommand {
    async fn button_click(ctx: Context, mut interaction: KenaComponentInteraction, _: &ComponentInteraction, config: Guild, lang: Language, _: &Handler, args: Vec<&str>) -> Result<(), CommandError> {
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

        let mut embed = CreateEmbed::new()
            .color(config.color);

        match args[0] {
            "none" => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.no_repeat.as_str()]));
                player.set_repeat_mode(RepeatMode::None).await.unwrap();

                interaction.edit()
                    .embed(embed)
                    .success_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, true)
                    .secondary_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, false)
                    .secondary_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, false)
                    .exec(&ctx.http)
                    .await?;
            }
            "track" => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.repeat_track.as_str()]));
                player.set_repeat_mode(RepeatMode::Track).await.unwrap();

                interaction.edit()
                    .embed(embed)
                    .secondary_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, false)
                    .success_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, true)
                    .secondary_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, false)
                    .exec(&ctx.http)
                    .await?;
            }
            "queue" => {
                embed = embed.description(lang.commands.repeat.info.format(&[lang.commands.repeat.repeat_queue.as_str()]));
                player.set_repeat_mode(RepeatMode::Queue).await.unwrap();

                interaction.edit()
                    .embed(embed)
                    .secondary_button(lang.commands.repeat.no_repeat.capitalize(), "queue::cmd::loop::none", None, false)
                    .secondary_button(lang.commands.repeat.repeat_track.capitalize(), "queue::cmd::loop::track", None, false)
                    .success_button(lang.commands.repeat.repeat_queue.capitalize(), "queue::cmd::loop::queue", None, true)
                    .exec(&ctx.http)
                    .await?;
            }
            _ => {
                interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content("Unknown error")
                    .emphemeral()
                    .exec(&ctx.http)
                    .await?;
            }
        }

        Ok(())
    }
}