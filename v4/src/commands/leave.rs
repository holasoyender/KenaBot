/*
 * File:          leave.rs
 * Path:          v4\src\commands\leave.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 2270098327c844f613355581125084eebb52535966e729911ac3e2680e6b75dd
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

pub(crate) struct LeaveCommand;

#[async_trait]
impl Command for LeaveCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        if config.premium.enabled && config.radio.enabled && config.radio.channel_id != "" {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.radio_enabled)
                .emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        }

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
        let manager = skua_voice::get(&context).await.unwrap().clone();

        if let Some(mut player) = atenea_client.get_player_context(guild_id).await {
            player.set_queue(QueueMessage::Clear).await.unwrap();
            player.stop_now().await.unwrap();
            atenea_client.delete_player(guild_id).await.unwrap();
        }

        if manager.get(guild_id).is_some() {
            manager.remove(guild_id).await.unwrap();
        }

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.stop_leave);

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("leave")
            .description("Make the bot leave the voice channel.")
            .description_localized(Locales::SPANISH.value(), "Sacar del canal de voz al bot")
            .description_localized(Locales::ENGLISH_US.value(), "Make the bot leave the voice channel.")
            .description_localized(Locales::ENGLISH_UK.value(), "Make the bot leave the voice channel.")
            .description_localized(Locales::FRENCH.value(), "Faire en sorte que le robot quitte le canal vocal.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Faça o bot sair do canal de voz.")
            .description_localized(Locales::ITALIAN.value(), "Fai uscire il bot dal canale vocale.")
            .description_localized(Locales::GERMAN.value(), "Lassen Sie den Bot den Sprachkanal verlassen.")
            .description_localized(Locales::UKRAINIAN.value(), "Зробіть бота покинути голосовий канал.")
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}