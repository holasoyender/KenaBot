/*
 * File:          unscoped.rs
 * Path:          v4\src\io\spotify\client\unscoped.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-24 14:15:54 +01:00
 *
 * SHA-256: 8451c8905a31c76f852f58fb761785f197c2dcb7a48ae38ecb5de45934646f69
 *
 */


use log::warn;
use reqwest::{Method, StatusCode};

use super::{API_ARTIST_ENDPOINT, request_builder::RequestBuilder};
use crate::io::spotify::client::request_builder::AsyncResponseHandler;
use crate::io::spotify::{
    client::{
        object,
        request_builder::{BaseRequestBuilderContainer, CatalogItemRequestBuilder, SearchBuilder},
        API_SEARCH_ENDPOINT, API_TRACKS_ENDPOINT, API_USER_PROFILE_ENDPOINT
    },
    error::Error,
    model::{
        id::{Id, IdTrait, TrackId, UserId},
        track::FullTrack,
        user::PublicUser,
    },
};
use crate::io::spotify::model::album::FullAlbum;
use crate::io::spotify::model::artist::FullArtist;
use crate::io::spotify::model::id::ArtistId;

/// All unscoped Spotify endpoints. The functions in this trait do not require user authentication to use. All Spotify
/// clients implement this trait.
pub trait UnscopedClient
where
    Self: crate::io::spotify::private::Sealed + Clone + Sized,
{
    /// Get Spotify catalog information for a single track identified by its unique Spotify ID.
    ///
    /// An optional market country may be specified with the [`market`-function in the request builder this function
    /// returns](CatalogItemRequestBuilder::market). Only content that is available in that market will be returned and
    /// [track relinking](crate::io::spotify::model::track#track-equality-and-track-relinking) may be applied.
    fn track<'a>(&'a self, track: Id<'a, TrackId>) -> CatalogItemRequestBuilder<Self, FullTrack> {
        let mut builder = CatalogItemRequestBuilder::new(
            Method::GET,
            format!("{}/{}", API_TRACKS_ENDPOINT, track.as_str()),
            self.clone(),
        );

        {
            builder = builder.with_async_response_handler(track_response_handler_async_fn(track.as_owned()));
        }

        builder
    }

    /// Get Spotify catalog information for multiple tracks based on their Spotify IDs.
    ///
    /// Up to 50 IDs may be given. In case some IDs cannot be found, they will be omitted from the result.
    ///
    /// An optional market country may be specified with the [`market`-function in the request builder this function
    /// returns](CatalogItemRequestBuilder::market). Only content that is available in that market will be returned and
    /// [track relinking](crate::io::spotify::model::track#track-equality-and-track-relinking) may be applied.
    fn tracks<'a, I>(&'a self, tracks: I) -> CatalogItemRequestBuilder<Self, object::TracksResponse, Vec<FullTrack>>
    where
        I: IntoIterator<Item = Id<'a, TrackId>>,
    {
        CatalogItemRequestBuilder::new(Method::GET, API_TRACKS_ENDPOINT, self.clone()).append_query(
            object::TRACKS_IDS_QUERY,
            tracks
                .into_iter()
                .map(|id| id.as_str().to_owned())
                .collect::<Vec<_>>()
                .join(","),
        )
    }

    fn artist<'a>(&'a self, artist: Id<'a, ArtistId>) -> CatalogItemRequestBuilder<Self, FullArtist> {
        CatalogItemRequestBuilder::new(
            Method::GET,
            format!("{}/{}", API_ARTIST_ENDPOINT, artist.as_str()),
            self.clone(),
        )
    }

    fn albums<'a>(&'a self, artist: Id<'a, ArtistId>) -> CatalogItemRequestBuilder<Self, Vec<FullAlbum>> {
        let mut builder = CatalogItemRequestBuilder::new(
            Method::GET,
            format!("{}/{}/albums?include_groups=album,single&limit=5", API_ARTIST_ENDPOINT, artist.as_str()),
            self.clone(),
        );

        builder = builder.with_async_response_handler(artist_albums_response_handler_async_fn(artist.as_owned()));

        builder
    }

    /// Get Spotify catalog information about albums, artists, playlists, tracks, shows or episodes that match a keyword
    /// string.
    ///
    /// This function returns a [SearchBuilder](self::SearchBuilder) that you can use to configure the various search
    /// parameters and finally send the search query and get the results back.
    fn search<S>(&self, query: S) -> SearchBuilder<Self>
    where
        S: Into<String>,
    {
        SearchBuilder::new(Method::GET, API_SEARCH_ENDPOINT, self.clone()).query(query.into())
    }

    /// Get public information about a Spotify user.
    fn user_profile<'a>(&'a self, user_id: Id<'a, UserId>) -> RequestBuilder<Self, PublicUser> {
        RequestBuilder::new(
            Method::GET,
            format!("{API_USER_PROFILE_ENDPOINT}/{}", user_id.as_str()),
            self.clone(),
        )
    }
}

fn track_response_handler_async_fn(track_id: Id<'static, TrackId>) -> AsyncResponseHandler {
    Box::new(move |response| {
        Box::pin(async move {
            match response.status() {
                StatusCode::OK => Ok(response),

                StatusCode::NOT_FOUND => {
                    warn!("Got 404 Not Found to track call");
                    Err(Error::NonexistentTrack(track_id))
                }

                other => Err(Error::UnhandledSpotifyResponseStatusCode(other.as_u16())),
            }
        })
    })
}

fn artist_albums_response_handler_async_fn(artist_id: Id<'static, ArtistId>) -> AsyncResponseHandler {
    Box::new(move |response| {
        Box::pin(async move {
            match response.status() {
                StatusCode::OK => Ok(response),

                StatusCode::NOT_FOUND => {
                    warn!("Got 404 Not Found to artist albums call");
                    Err(Error::NonexistentArtist(artist_id))
                }

                other => Err(Error::UnhandledSpotifyResponseStatusCode(other.as_u16())),
            }
        })
    })
}