/*
 * File:          object.rs
 * Path:          v4\src\io\spotify\client\object.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-22 23:43:27 +01:00
 *
 * SHA-256: 531858ee70a33cd5cc415c4350f1e3995dfd4907545bfdc1e8bc2d0e8db8e3fc
 *
 */


use serde::{Deserialize, Serialize};

use crate::io::spotify::{
    client::request_builder::TryFromEmptyResponse,
    error::ConversionError,
    model::{
        playback::{CurrentlyPlayingItem, Device, PlaybackState},
        track::{FullTrack, TrackObject},
        user::{PublicUser, User},
    },
};

pub const DEVICE_ID_QUERY: &str = "device_id";
pub const QUEUE_URI_QUERY: &str = "uri";

pub const TRACKS_IDS_QUERY: &str = "ids";
pub const MARKET_QUERY: &str = "market";

#[derive(Debug, Serialize)]
pub struct PlayItemsBody {
    pub uris: Vec<String>,
}

#[derive(Debug, Serialize)]
pub struct PlayContextBody {
    pub context_uri: String,
    pub offset: PlayContextOffset,
}

#[derive(Debug, Serialize)]
pub struct PlayContextOffset {
    pub position: Option<u32>,
    // TODO: support URI offsets
    pub uri: Option<String>,
}

#[derive(Debug, Deserialize)]
pub struct DevicesResponse {
    pub devices: Vec<Device>,
}

#[derive(Debug, Deserialize)]
pub struct TracksResponse {
    tracks: Vec<Option<TrackObject>>,
}

impl TracksResponse {
    pub fn full_tracks(self) -> std::result::Result<Vec<FullTrack>, ConversionError> {
        self.tracks
            .into_iter()
            .filter_map(|obj| obj.map(FullTrack::try_from))
            .collect::<std::result::Result<Vec<_>, ConversionError>>()
    }
}

impl TryFrom<TracksResponse> for Vec<FullTrack> {
    type Error = ConversionError;

    fn try_from(value: TracksResponse) -> Result<Self, Self::Error> {
        value
            .tracks
            .into_iter()
            .filter_map(|obj| obj.map(FullTrack::try_from))
            .collect::<std::result::Result<Vec<_>, ConversionError>>()
    }
}

impl From<DevicesResponse> for Vec<Device> {
    fn from(response: DevicesResponse) -> Self {
        response.devices
    }
}

impl TryFromEmptyResponse for DevicesResponse {}
impl TryFromEmptyResponse for Option<PlaybackState> {}
impl TryFromEmptyResponse for Option<CurrentlyPlayingItem> {}
impl TryFromEmptyResponse for TracksResponse {}
impl TryFromEmptyResponse for FullTrack {}
impl TryFromEmptyResponse for TrackObject {}
impl TryFromEmptyResponse for Vec<FullTrack> {}
impl TryFromEmptyResponse for User {}
impl TryFromEmptyResponse for PublicUser {}
