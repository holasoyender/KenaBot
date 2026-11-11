/*
 * File:          play.rs
 * Path:          v4\src\commands\play.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 58ac11b4906dff7266bdcde1189b3fdf196dc35e689531145bd30dc3258f57c8
 *
 */


use async_trait::async_trait;
use skua::all::{ChannelType, CommandOptionType, Context, CreateCommand, CreateEmbed, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use skua::builder::{CreateCommandOption};
use shared::constants::get_constants;
use shared::regex::REGEX;
use crate::atenea::model::search::SearchEngines;
use crate::atenea::model::track::{TrackData, TrackLoadData};
use crate::atenea::player_context::{QueueMessage, TrackInQueue};
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::globals::Atenea;
use crate::io::voice::connection::get_voice_channel_for_user;
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::locale::value::Value;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::util::string::{format_song_title, player_timestamp};

pub(crate) struct PlayCommand;

#[async_trait]
impl Command for PlayCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {
        let input = if let Some(ResolvedOption { value: ResolvedValue::String(i), .. }) = command_interaction.data.options().first() {
            i.to_string()
        } else {
            return Ok(());
        };

        command_interaction.defer(&context.http).await?;

        let member = command_interaction.member.as_ref().unwrap();

        let spotify_regex = regex::Regex::new(REGEX.spotify).unwrap();
        let soundcloud_regex = regex::Regex::new(REGEX.soundcloud).unwrap();
        let twitch_regex = regex::Regex::new(REGEX.twitch).unwrap();
        let deezer_regex = regex::Regex::new(REGEX.deezer).unwrap();
        let apple_music_regex = regex::Regex::new(REGEX.apple_music).unwrap();
        let tik_tok_regex = regex::Regex::new(REGEX.tik_tok).unwrap();
        let reddit_regex = regex::Regex::new(REGEX.reddit).unwrap();
        let youtube_regex = regex::Regex::new(REGEX.youtube).unwrap();

        if twitch_regex.is_match(&input) && !config.premium.enabled {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.manager.no_premium_twitch)
                //.emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        }

        if youtube_regex.is_match(&input) {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.commands.play.youtube_link)
                //.emphemeral()
                .exec(&context.http)
                .await?;
            return Ok(());
        }

        let guild_id = command_interaction.guild_id.unwrap();
        let constants = get_constants(&event_handler.cache).await;

        let self_channel_id = get_voice_channel_for_user(&guild_id, &context.cache, &context.cache.current_user().id);
        let channel_id = match get_voice_channel_for_user(&guild_id, &context.cache, &command_interaction.user.id) {
            Some(channel) => {

                //ignore if member is owner or has MANAGE_GUILD permission
                if config.disabled.voice.contains(&channel.to_string()) && !constants.owner_ids.contains(&command_interaction.user.id) && !member.permissions(&context).unwrap().manage_guild() {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(lang.commands.play.vc_disabled)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                }

                if let Some(self_channel_id) = self_channel_id {
                    if self_channel_id != channel {
                        command_interaction.reply()
                            .emoji(shared::emojis::EMOJIS.error)
                            .content(lang.commands.play.different_vc)
                            //.emphemeral()
                            .exec(&context.http)
                            .await?;
                        return Ok(());
                    }
                }

                match channel.to_channel(&context.http).await {
                    Ok(channel_info) => {
                        let channel_info = channel_info.guild().unwrap();

                        if channel_info.kind == ChannelType::Stage && !config.premium.enabled {
                            command_interaction.reply()
                                .emoji(shared::emojis::EMOJIS.error)
                                .content(lang.manager.no_premium_stage)
                                //.emphemeral()
                                .exec(&context.http)
                                .await?;
                            return Ok(());
                        }
                    }
                    Err(_) => {}
                };

                channel
            },
            None => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(lang.commands.play.no_vc)
                    //.emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }
        };

        let atenea_client = {
            let data_read = context.data.read().await;
            data_read.get::<Atenea>().unwrap().clone()
        };

        let manager = skua_voice::get(&context).await.unwrap();

        let mut player = if let Some(player) = atenea_client.get_player_context(guild_id).await {
            player
        } else {
            match manager.join_gateway(guild_id, channel_id).await {
                Ok((connection_info, _)) => {
                    atenea_client
                        .create_player_context(guild_id, connection_info, context.http.get_channel(command_interaction.channel_id).await.unwrap().guild().unwrap(), config.autoplay)
                        .await
                        .unwrap()
                },
                Err(_) => {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(lang.commands.play.no_vc_perms)
                        //.emphemeral()
                        .exec(&context.http)
                        .await
                        .unwrap();
                    return Ok(());
                }
            }
        };

        let query = if !spotify_regex.is_match(&input) &&
            !soundcloud_regex.is_match(&input) &&
            !twitch_regex.is_match(&input) &&
            !deezer_regex.is_match(&input) &&
            !apple_music_regex.is_match(&input) &&
            !tik_tok_regex.is_match(&input) &&
            !reddit_regex.is_match(&input)
        {
            SearchEngines::Spotify.to_query(&input)?
        } else {
            input
        };

        let loaded_tracks = atenea_client.load_tracks(guild_id, &query).await?;
        let requester_avatar = member.user.avatar_url();

        let tracks: Vec<TrackInQueue> = match loaded_tracks.data {
            Some(TrackLoadData::Track(mut track)) => {
                let explicit = track.info.title.starts_with("[EXPLICIT] ");
                let uri = track.info.uri.clone().unwrap_or("https://kenabot.xyz".to_string());

                if config.advanced.max_song_duration > 0 && track.info.length > config.advanced.max_song_duration {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(format!("{} (**{}**)", lang.manager.max_duration, config.advanced.max_song_duration))
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                if config.advanced.blocked_songs.contains(&uri) {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(lang.manager.blocked_song)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                if explicit && config.advanced.explicit_filter {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(lang.manager.explicit_song)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                track.requester = requester_avatar;

                let embed = CreateEmbed::new()
                    .color(config.color)
                    .description(lang.manager.song_added.format(&[format_song_title(&track.info.title).as_str(), uri.as_str(), player_timestamp(&track.info.length).as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .exec(&context.http)
                    .await?;

                vec![track.into()]
            },
            Some(TrackLoadData::Search(x)) => {
                let mut track = x[0].clone();

                let explicit = track.info.title.starts_with("[EXPLICIT] ");
                let uri = track.info.uri.clone().unwrap_or("https://kenabot.xyz".to_string());

                if config.advanced.max_song_duration > 0 && track.info.length > config.advanced.max_song_duration {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(format!("{} (**{}**)", lang.manager.max_duration, config.advanced.max_song_duration))
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                if config.advanced.blocked_songs.contains(&uri) {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(lang.manager.blocked_song)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                if explicit && config.advanced.explicit_filter {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content(lang.manager.explicit_song)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                };

                track.requester = requester_avatar;

                let embed = CreateEmbed::new()
                    .color(config.color)
                    .description(lang.manager.song_added.format(&[format_song_title(&track.info.title).as_str(), uri.as_str(), player_timestamp(&track.info.length).as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .exec(&context.http)
                    .await?;

                vec![track.into()]
            },
            Some(TrackLoadData::Playlist(x)) => {
                let tracks: Vec<TrackData> = x.tracks.into_iter().filter_map(|mut x| {
                    if config.advanced.max_song_duration > 0 && x.info.length > config.advanced.max_song_duration {
                        return None;
                    };
                    if config.advanced.blocked_songs.contains(&x.info.uri.clone().unwrap_or("https://kenabot.xyz".to_string())) {
                        return None;
                    };
                    if x.info.title.starts_with("[EXPLICIT] ") && config.advanced.explicit_filter {
                        return None;
                    };

                    x.requester = requester_avatar.clone();

                    x.into()
                }).collect();

                if tracks.is_empty() {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(lang.commands.play.no_song_found)
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                }
                
                let constants = get_constants(&event_handler.cache).await;

                if !config.premium.enabled && (tracks.len() as i32) > constants.max_playlist_size {
                    command_interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(lang.manager.max_queue_size.format(&[constants.max_playlist_size.to_string().as_str()]))
                        //.emphemeral()
                        .exec(&context.http)
                        .await?;
                    return Ok(());
                }

                let embed = CreateEmbed::new()
                    .color(config.color)
                    .description(lang.manager.playlist_added.format(&[tracks.len().to_string().as_str()]));

                command_interaction.reply()
                    .embed(embed)
                    .exec(&context.http)
                    .await?;

                //should_skip = false;
                tracks.iter().map(|x| x.into()).collect()
            }

            Some(TrackLoadData::Error(err)) => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(lang.commands.play.no_song_found)
                    //.emphemeral()
                    .exec(&context.http)
                    .await?;

                error!("Error loading tracks: {:?}", err);
                return Ok(());
            }

            _ => {
                command_interaction.reply()
                    .emoji(shared::emojis::EMOJIS.error)
                    .content(lang.commands.play.no_song_found)
                    //.emphemeral()
                    .exec(&context.http)
                    .await?;
                return Ok(());
            }
        };

        player.set_queue(QueueMessage::Append(tracks.into())).await?; // this is more optimal than replacing the entire queue with the new one

        if player.player_data.track.is_none() && !player.queue.is_empty() {
            player.skip().await?;
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("play")
            .description("Play a song or playlist in the voice channel.")
            .description_localized(Locales::SPANISH.value(), "Reproduce una canción o playlist en el canal de voz.")
            .description_localized(Locales::ENGLISH_US.value(), "Play a song or playlist in the voice channel.")
            .description_localized(Locales::ENGLISH_UK.value(), "Play a song or playlist in the voice channel.")
            .description_localized(Locales::FRENCH.value(), "Jouez une chanson ou une playlist dans le salon vocal.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Reproduza uma música ou playlist no canal de voz.")
            .description_localized(Locales::ITALIAN.value(), "Riproduci una canzone o una playlist nel canale vocale.")
            .description_localized(Locales::GERMAN.value(), "Spielen Sie einen Song oder eine Playlist im Sprachkanal.")
            .description_localized(Locales::UKRAINIAN.value(), "Відтворіть пісню або плейлист у голосовому каналі.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::String, "song", "Song to play, can be a Spotify, SoundCloud link or a title to search!")
                    .required(true)
                    .set_autocomplete(true)
                    .description_localized(Locales::SPANISH.value(), "Canción a reproducir, puede ser un link de Spotify, SoundCloud o un título a buscar!")
                    .description_localized(Locales::ENGLISH_US.value(), "Song to play, can be a Spotify, SoundCloud link or a title to search!")
                    .description_localized(Locales::ENGLISH_UK.value(), "Song to play, can be a Spotify, SoundCloud link or a title to search!")
                    .description_localized(Locales::FRENCH.value(), "Chanson à jouer, peut être un lien Spotify, SoundCloud ou un titre à rechercher!")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Música para tocar, pode ser um link do Spotify, SoundCloud ou um título para pesquisar!")
                    .description_localized(Locales::ITALIAN.value(), "Canzone da suonare, può essere un link Spotify, SoundCloud o un titolo da cercare!")
                    .description_localized(Locales::GERMAN.value(), "Song zum Abspielen, kann ein Spotify-, SoundCloud-Link oder ein Titel zum Suchen sein!")
                    .description_localized(Locales::UKRAINIAN.value(), "Пісня для відтворення, може бути посиланням Spotify, SoundCloud або назвою для пошуку!")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}