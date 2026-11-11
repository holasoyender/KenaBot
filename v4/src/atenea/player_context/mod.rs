/*
 * File:          mod.rs
 * Path:          v4\src\atenea\player_context\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: aed8cca14cdc0e5ec5750e19df83dc49afc2f951636527cabcdbbac7fffadc0a
 *
 */


pub(crate) mod context;

use crate::atenea::model::*;

use std::collections::VecDeque;
use rocket::serde::Serialize;
use serde::Deserialize;

pub use context::PlayerContext;
pub use context::PlayerContextDummy;

#[derive(Debug, Clone, Default, Serialize, Deserialize)]
/// A track that's inside the queue.
pub struct TrackInQueue {
    /// The track itself.
    pub track: track::TrackData,
    /// The time the track should start at.
    pub start_time: Option<std::time::Duration>,
    /// The time the track should end at.
    pub end_time: Option<std::time::Duration>,
    /// The volume the track should start at.
    pub volume: Option<u16>,
    /// The filters the track should start at.
    pub filters: Option<player::Filters>,
}

#[derive(Debug, Clone)]
pub enum QueueMessage {
    /// Add a track to the end of the queue.
    PushToBack(TrackInQueue),
    /// Add a track to the start of the queue.
    PushToFront(TrackInQueue),
    /// Insert a track to a specific position in the queue.
    Insert(usize, TrackInQueue),
    /// Remove a track from the queue.
    Remove(usize),
    /// Clear the queue.
    Clear,
    /// Replace the entire queue with another one.
    Replace(VecDeque<TrackInQueue>),
    /// Append a queue to the end of the current one.
    Append(VecDeque<TrackInQueue>),
}

impl TrackInQueue {
    fn into_update_player(self) -> http::UpdatePlayer {
        http::UpdatePlayer {
            encoded_track: self.track.encoded.into(),
            position: self.start_time.map(|x| x.as_millis() as u64),
            end_time: self.end_time.map(|x| x.as_millis() as u64),
            volume: self.volume,
            filters: self.filters,
            ..Default::default()
        }
    }
}

impl From<track::TrackData> for TrackInQueue {
    fn from(track: track::TrackData) -> Self {
        Self {
            track,
            ..Default::default()
        }
    }
}

impl From<&track::TrackData> for TrackInQueue {
    fn from(track: &track::TrackData) -> Self {
        Self {
            track: track.clone(),
            ..Default::default()
        }
    }
}
