/*
 * File:          mod.rs
 * Path:          v4\src\io\spotify\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-22 23:43:27 +01:00
 *
 * SHA-256: fb4be1fe84b581ba96386c8bade12600e4ed9b0252918d3a9051b7ace571ad3a
 *
 */


//! A wrapper for the Spotify Web API that (hopefully) doesn't suck (too much) (I think).
//!
//! A lot of the functionality is largely opinionated for my own use but I'm trying to make the library ergonomic and
//! efficient to use. So far only the endpoints I care about are implemented, but if you need some endpoints
//! implemented, feel free to open an issue.
//!
//! # Features
//!
//! - Type-safe clients and model.
//! - Asynchronous and synchronous (blocking) clients.
//! - Every OAuth authorization flow Spotify supports is implemented.
//! - Supports multiple simultaneous user clients.
//! - Automatically refreshes access tokens when they expire, where applicable.
//! - Reacts to API rate limits using either Tokio's or async-std's sleep functions at your discretion when using an
//!   asynchronous client. Synchronous clients block the running thread.
//!
//! # Usage
//!
//! See the module-level docs for [client] for instructions how to use the various Spotify clients.

pub mod client;

pub mod error;
pub mod model;
pub mod scope;

mod util;

pub(crate) mod private {
    pub trait Sealed {}
}

pub mod prelude {
    //! Re-exports of all common traits in the crate.
    //!
    //! A lot of various objects are similar in the crate; Spotify clients, model tracks (the song kind, not the train
    //! kind), IDs etc. Their common functionality is grouped into traits. All such traits are re-exported here for
    //! convenience.

    pub use crate::io::spotify::client::{request_builder::AsyncRequestBuilder, AccessTokenRefreshAsync};
    pub use crate::io::spotify::client::{request_builder::BaseRequestBuilder, UnscopedClient};
    pub use crate::io::spotify::{
        model::{
            album::{CommonAlbumInformation, FullAlbumInformation, NonLocalAlbumInformation},
            artist::{CommonArtistInformation, FullArtistInformation, NonLocalArtistInformation},
            id::{IdFromBare, IdFromKnownKind, IdTrait},
            search::ToTypesString,
            track::{CommonTrackInformation, FullTrackInformation, NonLocalTrackInformation, RelinkedTrackEquality},
            user::{CommonUserInformation, CurrentUserInformation, PrivateUserInformation},
        },
        scope::ToScopesString,
    };
}
