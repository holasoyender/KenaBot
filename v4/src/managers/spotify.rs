/*
 * File:          spotify.rs
 * Path:          v4\src\managers\spotify.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-29 20:05:11 +01:00
 *
 * SHA-256: dc2ae21ea22d9ad4d0f91fd984cf86e6af26fa818fd0636e9d59d6db739c9af7
 *
 */


use std::io::BufRead;
use crate::io::spotify::client::{AsyncSpotifyClient, SpotifyClientBuilder, UnscopedClient};
use crate::io::spotify::model::album::{PartialAlbum};
use crate::io::spotify::model::artist::FullArtist;
use crate::io::spotify::model::ItemType;
use crate::io::spotify::prelude::{AsyncRequestBuilder, CommonArtistInformation, CommonTrackInformation};
use crate::util::url;

#[derive(Debug, Clone)]
pub struct SpotifyManager {
    pub client: AsyncSpotifyClient,
}

#[derive(Debug, Clone)]
pub struct SpotifyKey {
    pub client_id: String,
    pub client_secret: String,
}

impl SpotifyManager {
    pub async fn new() -> Self {

        let keys = Self::load_file(std::path::Path::new("data/spotify.csv")).await.expect("failed to load Spotify keys");
        let keys_len = keys.len();

        let spotify_client =
            SpotifyClientBuilder::new(keys)
                .build_async()
                .await
                .expect("failed to build Spotify client");

        info!("Spotify client built with {} keys", keys_len);

        Self {
            client: spotify_client,
        }
    }

    pub async fn load_file(path: &std::path::Path) -> Result<Vec<SpotifyKey>, Box<dyn std::error::Error>> {
        let file = std::fs::File::open(path)?;
        let mut keys = Vec::new();
        let content = std::io::BufReader::new(file);
        for line in content.lines() {
            let line = line?;
            let mut split = line.split(",");
            let client_id = split.next().expect("failed to get client ID").to_owned();
            let client_secret = split.next().expect("failed to get client secret").to_owned();
            keys.push(SpotifyKey {
                client_id,
                client_secret,
            });
        }
        Ok(keys)
    }

    pub async fn search_artist(&self, artist: &str) -> Option<FullArtist> {
        let search = self.client.search(artist)
            .types([ItemType::Artist])
            .limit(1)
            .send_async()
            .await
            .ok()?;

        let artist_list = search.artists()?.items();
        let artist = artist_list.first()?;

        Some(artist.to_owned())
    }

    pub async fn get_artist_albums(&self, artist: FullArtist) -> Option<Vec<PartialAlbum>> {
        let albums = self.client.search(artist.name())
            .types([ItemType::Album])
            .limit(5)
            .send_async()
            .await
            .ok()?;

        let albums = albums.partial_albums()?;
        let albums = albums.items();

        Some(albums)
    }

    pub async fn search_autocomplete(&self, input: String) -> Vec<String> {

        let search = self.client.search(input)
            .types([ItemType::Track])
            .limit(10)
            .send_async()
            .await;

        match search {
            Ok(search) => {
                let tracks = search.tracks().unwrap().items();
                tracks.iter().map(|t| format!("{} - {}", t.name().to_owned(), t.artists().iter().map(|a| a.name().to_owned()).collect::<Vec<String>>().join(", "))).collect::<Vec<String>>()
            },
            Err(_) => vec![]
        }
    }
}

fn format_bio<S>(biograpy: S, artist_url: String) -> String
    where
        S: Into<String>,
{
    let mut bio = biograpy.into();
    bio = regex::Regex::new("<[^>]*>").unwrap().replace_all(bio.as_str(), "").to_string();
    bio = regex::Regex::new("Last.fm").unwrap().replace_all(bio.as_str(), "Spotify").to_string();
    bio = regex::Regex::new("last.fm").unwrap().replace_all(bio.as_str(), "Spotify").to_string();
    bio = regex::Regex::new("last.fm").unwrap().replace_all(bio.as_str(), "Spotify").to_string();
    bio = bio.replace("User-contributed text is available under the Creative Commons By-SA License; additional terms may apply.", "");

    if bio.contains("\n\n1) ") {
        bio = bio.split("\n\n1) ").collect::<Vec<&str>>()[0].to_string();
    }

    let first_paragraph = bio.split("\n").collect::<Vec<&str>>()[0].to_string();

    format!("{}\n\n[Read more here]({})", first_paragraph, artist_url)
}

pub async fn get_artist_bio<S>(artist: S, artist_url: &String) -> Option<String>
    where
        S: Into<String>,
{
    let artist = artist.into();
    let artist_name = url::encode(artist.as_str());
    let api_key = std::env::var("LASTFM_API_KEY").expect("failed to get LastFM API key");
    let url = format!("https://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist={}&api_key={}&format=json", artist_name, api_key);
    let response = reqwest::get(url.as_str()).await.ok()?;
    let json = response.json::<serde_json::Value>().await.ok()?;
    let bio = json["artist"]["bio"]["content"].as_str()?;

    if bio == "" {
        return None;
    }

    Some(format_bio(bio, artist_url.clone()))
}

pub async fn get_albums_banner<S>(artist: S, album_images: Vec<String>) -> Option<Vec<u8>>
    where
        S: Into<String>
{
    let api = format!("https://api.kenabot.xyz/images/artists-top-tracks/{}", artist.into());
    let body = serde_json::json!({ "images": album_images });
    let client = reqwest::Client::new();
    let response = client.post(api.as_str())
        .json(&body)
        .header("Content-Type", "application/json")
        .header("Accept", "image/png")
        .header("Authorization", "56bad67b2ae2408ba54470b0e75a7605")
        .timeout(std::time::Duration::from_secs(3))
        .send()
        .await
        .ok()?;

    let bytes = response.bytes().await.ok()?;
    Some(bytes.to_vec())
}