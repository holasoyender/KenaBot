/*
 * File:          volume.rs
 * Path:          v4\src\commands\volume.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: bd6d4de711abc7f8c9b02b430718709ac2ef22ed16ebaf29ee629a2274bad5a0
 *
 */


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

pub(crate) struct VolumeCommand;

#[async_trait]
impl Command for VolumeCommand {
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

        player.set_volume(input as u16).await?;

        let embed = CreateEmbed::new()
            .color(config.color)
            .description(shared::emojis::EMOJIS.tick.normal() + &*lang.commands.volume.set.format(&[input.to_string().as_str()]));

        command_interaction.reply()
            .embed(embed)
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("volume")
            .name_localized(Locales::SPANISH.value(), "volumen")
            .description("Set the volume of the current song.")
            .description_localized(Locales::SPANISH.value(), "Establecer el volumen de la canción actual.")
            .description_localized(Locales::ENGLISH_US.value(), "Set the volume of the current song.")
            .description_localized(Locales::ENGLISH_UK.value(), "Set the volume of the current song.")
            .description_localized(Locales::FRENCH.value(), "Définir le volume de la chanson actuelle.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Defina o volume da música atual.")
            .description_localized(Locales::ITALIAN.value(), "Imposta il volume della canzone corrente.")
            .description_localized(Locales::GERMAN.value(), "Stellen Sie die Lautstärke des aktuellen Songs ein.")
            .description_localized(Locales::UKRAINIAN.value(), "Встановіть гучність поточної пісні.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::Integer, "level", "Volume level to set ( 0 - 100 )")
                    .required(true)
                    .max_int_value(100)
                    .min_int_value(0)
                    .description_localized(Locales::SPANISH.value(), "Nivel de volumen a establecer ( 0 - 100 )")
                    .description_localized(Locales::ENGLISH_US.value(), "Volume level to set ( 0 - 100 )")
                    .description_localized(Locales::ENGLISH_UK.value(), "Volume level to set ( 0 - 100 )")
                    .description_localized(Locales::FRENCH.value(), "Niveau de volume à définir ( 0 - 100 )")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Nível de volume a definir ( 0 - 100 )")
                    .description_localized(Locales::ITALIAN.value(), "Livello di volume da impostare ( 0 - 100 )")
                    .description_localized(Locales::GERMAN.value(), "Lautstärkepegel einstellen ( 0 - 100 )")
                    .description_localized(Locales::UKRAINIAN.value(), "Рівень гучності для встановлення ( 0 - 100 )")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}