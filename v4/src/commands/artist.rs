/*
 * File:          artist.rs
 * Path:          v4\src\commands\artist.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 0a2ac9a036b64fc53a12e62f630e9baeba110810362c4c2de91a9c44b48d8033
 *
 */


use async_trait::async_trait;
use skua::all::{CommandOptionType, Context, CreateAttachment, CreateCommand, CreateEmbedAuthor, Interaction, Permissions, ResolvedOption, ResolvedValue};
use skua::all::standard::CommandError;
use skua::builder::{CreateCommandOption, CreateEmbed};
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::io::spotify::prelude::{CommonAlbumInformation, CommonArtistInformation, FullArtistInformation};
use crate::locale::language::Language;
use crate::locale::locales::Locales;
use crate::managers::command::Command;
use crate::managers::interactions::KenaCommandInteraction;
use crate::managers::spotify::{get_albums_banner, get_artist_bio};
use crate::util::format_number;

pub(crate) struct ArtistCommand;

#[async_trait]
impl Command for ArtistCommand {
    async fn run(&mut self, context: Context, _: Interaction, mut command_interaction: KenaCommandInteraction, config: Guild, lang: Language, event_handler: &Handler) -> Result<(), CommandError> {

        let input = if let Some(ResolvedOption { value: ResolvedValue::String(i), .. }) = command_interaction.data.options().first() {
            i.to_string()
        } else {
            return Ok(());
        };

        command_interaction.defer(&context.http).await.expect("failed to defer command");

        let artist = event_handler.spotify_manager.search_artist(&input).await;

        if let Some(artist) = artist {

            let artist_uri = artist.external_urls().clone().spotify.unwrap_or("".to_string());

            let image = match artist.images().first() {
                Some(img) => img.clone().url,
                None => context.cache.current_user().avatar_url().unwrap()
            };

            let bio = get_artist_bio(artist.name(), &artist_uri).await.unwrap_or_else(|| "No bio".to_string());
            let albums = event_handler.spotify_manager.get_artist_albums(artist.clone()).await.unwrap_or_else(|| vec![]);
            let album_images = albums.iter().map(|a| a.images().first().unwrap().clone().url).collect::<Vec<String>>();
            let mut genres = artist.genres().join(", ");
            if genres.is_empty() {
                genres = "No genres".to_string();
            }

            let banner = get_albums_banner(artist.name(), album_images).await;

            let mut embed = CreateEmbed::new()
                .color(config.color)
                .author(CreateEmbedAuthor::new(artist.name())
                    .url(&artist_uri)
                    .icon_url(&image)
                )
                .title(lang.commands.artist.embed_title)
                .thumbnail(&image)
                .description(format!("{}\n\n**{}:** ```{}```", bio, lang.commands.artist.genres, genres))
                .field(lang.commands.artist.followers, format_number(artist.followers()), true)
                .field(lang.commands.artist.popularity, format!("⭐ {}", artist.popularity()), true);

            if let Some(banner) = banner {
                let attachment = CreateAttachment::bytes(banner, "banner.png".to_string());
                embed = embed.image("attachment://banner.png");

                command_interaction.reply()
                    .embed(embed)
                    .add_file(attachment)
                    .link_button(lang.commands.artist.open_spotify, artist_uri, None, false)
                    .exec(&context.http)
                    .await.unwrap_or(());
            } else {
                command_interaction.reply()
                    .embed(embed)
                    .link_button(lang.commands.artist.open_spotify, artist_uri, None, false)
                    .exec(&context.http)
                    .await.unwrap_or(());
            }

        } else {
            command_interaction.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content(lang.commands.artist.no_artist)
                .emphemeral()
                .exec(&context.http)
                .await.unwrap_or(());
        }

        Ok(())
    }

    fn register(&self) -> CreateCommand {
        CreateCommand::new("artist")
            .description("Search information about a Spotify artist.")
            .description_localized(Locales::SPANISH.value(), "Buscar información sobre un artista de Spotify.")
            .description_localized(Locales::ENGLISH_US.value(), "Search information about a Spotify artist.")
            .description_localized(Locales::ENGLISH_UK.value(), "Search information about a Spotify artist.")
            .description_localized(Locales::FRENCH.value(), "Rechercher des informations sur un artiste Spotify.")
            .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Pesquise informações sobre um artista do Spotify.")
            .description_localized(Locales::ITALIAN.value(), "Cerca informazioni su un artista di Spotify.")
            .description_localized(Locales::GERMAN.value(), "Suchen Sie nach Informationen zu einem Spotify-Künstler.")
            .description_localized(Locales::UKRAINIAN.value(), "Шукайте інформацію про виконавця Spotify.")
            .add_option(
                CreateCommandOption::new(CommandOptionType::String, "artist", "Name of the artist to search.")
                    .required(true)
                    .description_localized(Locales::SPANISH.value(), "Nombre del artista a buscar.")
                    .description_localized(Locales::ENGLISH_US.value(), "Name of the artist to search.")
                    .description_localized(Locales::ENGLISH_UK.value(), "Name of the artist to search.")
                    .description_localized(Locales::FRENCH.value(), "Nom de l'artiste à rechercher.")
                    .description_localized(Locales::PORTUGUESE_BRAZILIAN.value(), "Nome do artista a pesquisar.")
                    .description_localized(Locales::ITALIAN.value(), "Nome dell'artista da cercare.")
                    .description_localized(Locales::GERMAN.value(), "Name des zu suchenden Künstlers.")
                    .description_localized(Locales::UKRAINIAN.value(), "Ім'я виконавця, якого потрібно знайти.")
            )
    }

    fn is_premium(&self) -> bool {
        false
    }

    fn permissions(&self) -> Vec<Permissions> {
        vec![]
    }
}