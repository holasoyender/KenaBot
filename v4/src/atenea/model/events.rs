/*
 * File:          events.rs
 * Path:          v4\src\atenea\model\events.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: b4f66404ed28936607bfdbc0da36b60984c28ddad32857ad4acdd579672fd995
 *
 */


use serde::Serialize;
use skua::all::GuildId;
use crate::atenea::client::AteneaClient;
use crate::atenea::model::*;

#[derive(Hash, Debug, Clone, Default)]
pub struct Events {
    /// Every single event will trigger this event with the raw data received.
    pub raw: Option<fn(AteneaClient, session_id: String, &serde_json::Value) -> BoxFuture<()>>,
    /// Dispatched by Atenea upon successful connection and authorization.
    pub ready: Option<fn(AteneaClient, session_id: String, &Ready) -> BoxFuture<()>>,
    /// Dispatched periodically with the current state of a player.
    pub player_update:
        Option<fn(AteneaClient, session_id: String, &PlayerUpdate) -> BoxFuture<()>>,
    /// A collection of statistics sent every minute.
    pub stats: Option<fn(AteneaClient, session_id: String, &Stats) -> BoxFuture<()>>,
    /// Dispatched when a track starts playing.
    pub track_start: Option<fn(AteneaClient, session_id: String, &TrackStart) -> BoxFuture<()>>,
    /// Dispatched when a track ends.
    /// track_exception and track_stuck will also trigger this event.
    pub track_end: Option<fn(AteneaClient, session_id: String, &TrackEnd) -> BoxFuture<()>>,
    /// Dispatched when a track throws an exception.
    pub track_exception:
        Option<fn(AteneaClient, session_id: String, &TrackException) -> BoxFuture<()>>,
    /// Dispatched when a track gets stuck while playing.
    pub track_stuck: Option<fn(AteneaClient, session_id: String, &TrackStuck) -> BoxFuture<()>>,
    /// Dispatched when an audio WebSocket to Discord is closed.
    pub websocket_closed:
        Option<fn(AteneaClient, session_id: String, &WebSocketClosed) -> BoxFuture<()>>,
}

#[derive(Hash, PartialEq, Eq, PartialOrd, Ord, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched by Atenea upon successful connection and authorization.
pub struct Ready {
    pub op: String,
    /// The atenea session ID, used for some REST requests and resuming.
    pub session_id: String,
    /// Whether this session was resumed.
    pub resumed: bool,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched periodically with the current state of a player.
pub struct PlayerUpdate {
    pub op: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The player state.
    pub state: player::State,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// A collection of statistics sent every minute.
pub struct Stats {
    #[serde(default)]
    pub op: String,
    /// The amount of players connected to the node.
    pub players: u64,
    /// The amount of players playing a track.
    pub playing_players: u64,
    /// The uptime of the node in milliseconds.
    pub uptime: u64,
    /// Memory statistics of the node.
    pub memory: Memory,
    /// CPU statistics of the node.
    pub cpu: Cpu,
    /// The frame stats of the node.
    ///
    /// This field is None if there's no players, or it was requested via the REST API.
    pub frame_stats: Option<FrameStats>,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Cpu {
    pub cores: u64,
    pub system_load: f64,
    pub atenea_load: f64,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct Memory {
    pub free: u64,
    pub used: u64,
    pub allocated: u64,
    pub reservable: u64,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct FrameStats {
    /// The amount of frames sent to Discord.
    pub sent: u64,
    /// The amount of frames that were nulled.
    pub nulled: u64,
    /// The difference between sent frames and the expected amount of frames.
    ///
    /// The expected amount of frames is 3000 (1 every 20 ms) per player.
    /// If the deficit is negative, too many frames were sent, and if it's positive, not enough frames got sent.
    pub deficit: i64,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched when a track starts playing.
pub struct TrackStart {
    pub op: String,
    #[serde(rename = "type")]
    pub event_type: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The track that started playing.
    pub track: track::TrackData,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched when a track ends.
/// track_exception and track_stuck will also trigger this event.
pub struct TrackEnd {
    pub op: String,
    #[serde(rename = "type")]
    pub event_type: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The track that finished playing.
    pub track: track::TrackData,
    /// The reason the track finished.
    pub reason: TrackEndReason,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// The reason the track finished.
pub enum TrackEndReason {
    Finished,
    LoadFailed,
    Stopped,
    Replaced,
    Cleanup,
}

impl From<TrackEndReason> for bool {
    /// If the player should continue playing with the next track on the queue or not.
    fn from(value: TrackEndReason) -> Self {
        match value {
            TrackEndReason::Finished => true,
            TrackEndReason::LoadFailed => true,
            TrackEndReason::Stopped => false,
            TrackEndReason::Replaced => false,
            TrackEndReason::Cleanup => false,
        }
    }
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched when a track throws an exception.
pub struct TrackException {
    pub op: String,
    #[serde(rename = "type")]
    pub event_type: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The track that threw the exception
    pub track: track::TrackData,
    /// The exception itself.
    pub exception: track::TrackError,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched when a track gets stuck while playing.
pub struct TrackStuck {
    pub op: String,
    #[serde(rename = "type")]
    pub event_type: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The track that got stuck.
    pub track: track::TrackData,
    /// The threshold in milliseconds that was exceeded.
    pub threshold_ms: u64,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Dispatched when an audio WebSocket to Discord is closed.
pub struct WebSocketClosed {
    pub op: String,
    #[serde(rename = "type")]
    pub event_type: String,
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// Status code returned by discord.
    ///
    /// See the [discord docs](https://discord.com/developers/docs/topics/opcodes-and-status-codes#voice-voice-close-event-codes)
    /// for a list of them.
    pub code: u16,
    /// The reason the socket was closed.
    pub reason: String,
    /// Whether the connection was closed by Discord or not.
    pub by_remote: bool,
}
