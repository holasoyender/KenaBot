/*
 * File:          stop.rs
 * Path:          v4\src\commands\stop.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: a4f91d40e07893a9c6bfa9ccd8a5292556d8b1a64bd4970cc8c9bbe7374b8c47
 *
 */


use async_trait::async_trait;
use skua::all::{ComponentInteraction, Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use crate::atenea::player_context::QueueMessage;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct StopCommand;

#[async_trait]
impl Command for StopCommand {
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

        if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
            player.set_queue(QueueMessage::Clear).await.unwrap();
            player.stop_now().await.unwrap();
            atenea_client.delete_player(guild_id).await.unwrap();
        }

        let mut out = lang.commands.stop;

        if !config.premium.enabled || !config.radio.enabled || config.radio.channel_id == "" {
            let manager = skua_voice::get(&context).await.unwrap().clone();

            if manager.get(guild_id).is_some() {
                manager.remove(guild_id).await.unwrap();
                out = lang.commands.stop_leave;
            }
        }

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*out);

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("stop")
            .description("Stops the queue and removes the bot from the voice channel.")
            .description_localized(Locales::SPANISH.value(), "Finaliza la lista de reproducción y saca al bot del canal de voz.")
            .description_localized(Locales::ENGLISH_US.value(), "Stops the queue and removes the bot from the voice channel.")
            .description_localized(Locales::ENGLISH_UK.value(), "Stops the queue and removes the bot from the voice channel.")
            .description_localized(Locales::FRENCH.value(), "Arrête la file d'attente et supprime le bot du salon vocal.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Para a fila e remove o bot do canal de voz.")
            .description_localized(Locales::ITALIAN.value(), "Interrompe la coda e rimuove il bot dal canale vocale.")
            .description_localized(Locales::GERMAN.value(), "Stoppt die Warteschlange und entfernt den Bot aus dem Sprachkanal.")
            .description_localized(Locales::UKRAINIAN.value(), "Зупиняє чергу та видаляє бота з голосового каналу.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

#[async_trait]
impl Button for StopCommand {
    async fn button_click(ctx: Context, mut interaction: KenaComponentInteraction, _: &ComponentInteraction, config: Guild, lang: Language, _: &Handler, _: Vec<&str>) -> Result<(), CommandError> {
        let atenea_client = {
            let data_read = ctx.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let guild_id = interaction.guild_id.unwrap();

        if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
            player.set_queue(QueueMessage::Clear).await.unwrap();
            player.stop_now().await.unwrap();
            atenea_client.delete_player(guild_id).await.unwrap();
        }

        let mut out = lang.commands.stop;

        if !config.premium.enabled || !config.radio.enabled || config.radio.channel_id == "" {
            let manager = skua_voice::get(&ctx).await.unwrap().clone();

            if manager.get(guild_id).is_some() {
                manager.remove(guild_id).await.unwrap();
                out = lang.commands.stop_leave;
            }
        }

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*out);

        interaction.reply()
            .embed(embed)
            .emphemeral()
            .exec(&ctx.http)
            .await?;

        Ok(())
    }
}