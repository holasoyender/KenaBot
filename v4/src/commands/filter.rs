/*
 * File:          filter.rs
 * Path:          v4\src\commands\filter.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: fbd636f9e973250333e3b975484fcfc1598086c0b4dd6d6394412145da0844f7
 *
 */


use async_trait::async_trait;
use skua::all::{ButtonStyle, ComponentInteraction, Context, CreateCommand, CreateEmbed, Interaction, Permissions};
use skua::all::standard::CommandError;
use skua::builder::CreateEmbedAuthor;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::component::{Button, KenaComponentInteraction};
use crate::managers::interactions::KenaCommandInteraction;

pub(crate) struct FilterCommand;

#[async_trait]
impl Command for FilterCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {
        let guild_id = command_interaction.guild_id.unwrap();
        let bot_id = event_handler.bot_id.read().unwrap();
        let bot_avatar = context.cache.current_user().avatar_url().unwrap();

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

        let embed = CreateEmbed::default()
            .author(CreateEmbedAuthor::new(lang.commands.filters.embed_title).icon_url(bot_avatar))
            .color(config.color)
            .description(lang.commands.filters.embed_description);

        let user_id = command_interaction.user.id.to_string();
        command_interaction.reply()
            .embeds(vec![embed])
            .button("Bass Boost", format!("{}::cmd::filter::bass_boost", user_id), None, false, button_type(player.filters.bass_boost))
            .button("8D", format!("{}::cmd::filter::8d", user_id), None, false, button_type(player.filters.spacial))
            .button("Nightcore", format!("{}::cmd::filter::night_core", user_id), None, false, button_type(player.filters.night_core))
            .button("Vaporwave", format!("{}::cmd::filter::vapor_wave", user_id), None, false, button_type(player.filters.vapor_wave))
            .button("Treble", format!("{}::cmd::filter::treble", user_id), None, false, button_type(player.filters.treble))
            .button("Tremolo", format!("{}::cmd::filter::tremolo", user_id), None, false, button_type(player.filters.tremolo))
            .button("Karaoke", format!("{}::cmd::filter::karaoke", user_id), None, false, button_type(player.filters.karaoke))
            .button("Speed", format!("{}::cmd::filter::speed", user_id), None, false, button_type(player.filters.speed))
            .button("Pitch", format!("{}::cmd::filter::pitch", user_id), None, false, button_type(player.filters.pitch))
            .exec(&context.http)
            .await?;

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("filter")
            .description("Set audio filters to the songs in the queue.")
            .description_localized(Locales::SPANISH.value(), "Establece filtros de audio para las canciones de la cola.")
            .description_localized(Locales::ENGLISH_US.value(), "Set audio filters to the songs in the queue.")
            .description_localized(Locales::ENGLISH_UK.value(), "Set audio filters to the songs in the queue.")
            .description_localized(Locales::FRENCH.value(), "Définir des filtres audio pour les chansons de la file d'attente.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Definir filtros de áudio para as canções na fila de espera.")
            .description_localized(Locales::ITALIAN.value(), "Impostare i filtri audio sui brani in coda.")
            .description_localized(Locales::GERMAN.value(), "Legen Sie Audiofilter für die Titel in der Warteschlange fest.")
            .description_localized(Locales::UKRAINIAN.value(), "Встановіть аудіофільтри для пісень у черзі.")
    }

    fn is_premium(&self) -> bool {
        true
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}

fn button_type(is_active: bool) -> ButtonStyle {
    if is_active {
        ButtonStyle::Primary
    } else {
        ButtonStyle::Secondary
    }
}

#[async_trait]
impl Button for FilterCommand {
    async fn button_click(ctx: Context, mut interaction: KenaComponentInteraction, _: &ComponentInteraction, config: Guild, lang: Language, _: &Handler, args: Vec<&str>) -> Result<(), CommandError> {
        let atenea_client = {
            let data_read = ctx.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let guild_id = interaction.guild_id.unwrap();
        let bot_avatar = ctx.cache.current_user().avatar_url().unwrap();

        let Some(mut player) = atenea_client.get_player_context(guild_id).await else {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(&lang.manager.nothing_playing)
                .emphemeral()
                .exec(&ctx.http)
                .await?;
            return Ok(());
        };

        let embed = CreateEmbed::default()
            .author(CreateEmbedAuthor::new(lang.commands.filters.embed_title).icon_url(bot_avatar))
            .color(config.color)
            .description(lang.commands.filters.embed_description);

        player.toggle_filter(args[0]).await?;

        interaction.edit()
            .embeds(vec![embed])
            .button("Bass Boost", "queue::cmd::filter::bass_boost", None, false, button_type(player.filters.bass_boost))
            .button("8D", "queue::cmd::filter::8d", None, false, button_type(player.filters.spacial))
            .button("Nightcore", "queue::cmd::filter::night_core", None, false, button_type(player.filters.night_core))
            .button("Vaporwave", "queue::cmd::filter::vapor_wave", None, false, button_type(player.filters.vapor_wave))
            .button("Treble", "queue::cmd::filter::treble", None, false, button_type(player.filters.treble))
            .button("Tremolo", "queue::cmd::filter::tremolo", None, false, button_type(player.filters.tremolo))
            .button("Karaoke", "queue::cmd::filter::karaoke", None, false, button_type(player.filters.karaoke))
            .button("Speed", "queue::cmd::filter::speed", None, false, button_type(player.filters.speed))
            .button("Pitch", "queue::cmd::filter::pitch", None, false, button_type(player.filters.pitch))
            .exec(&ctx.http)
            .await?;

        Ok(())
    }
}