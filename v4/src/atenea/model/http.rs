/*
 * File:          http.rs
 * Path:          v4\src\atenea\model\http.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-27 16:37:33 +01:00
 *
 * SHA-256: 6161a07c2f8227b7f04f0fab1d6103a200b481de19eb6468d7d5e6720752a282
 *
 */


use serde::Serialize;
use crate::atenea::model::*;

#[derive(Debug, Clone, PartialEq, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Updates or creates the player for this guild.
///
/// If every field is None, the player will stop playing.
pub struct UpdatePlayer {
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The base64 encoded track to play.
    ///
    /// Mutually exclusive with `identifier`.
    pub encoded_track: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The identifier of the track to play.
    ///
    /// Mutually exclusive with `encoded_track`.
    pub identifier: Option<String>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The track end time in milliseconds.
    ///
    /// It must be a value above 0 or None.
    ///
    /// None resets this if it was set previously.
    pub end_time: Option<u64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The player volume.
    ///
    /// In percentage, from 0 to 1000.
    pub volume: Option<u16>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The track position in milliseconds.
    ///
    /// This value can be set to start a track at a specific time.
    pub position: Option<u64>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// Whether the player should be paused.
    pub paused: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The filters to apply.
    ///
    /// This will override all previously applied filters.
    pub filters: Option<player::Filters>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The discord websocket connection information.
    ///
    /// Required for creating a player.
    pub voice: Option<player::ConnectionInfo>,
}

#[derive(Debug, Clone, PartialEq, Default, Serialize, Deserialize)]
/// Updates the session with the resuming state and timeout.
///
/// You must call this method if you wish to restart the discord bot without having all players
/// stop, and provide the current `session_id` when creating the node connection.
pub struct ResumingState {
    #[serde(skip_serializing_if = "Option::is_none")]
    /// Whether resuming should be, or is enabled for this session or not.
    pub resuming: Option<bool>,
    #[serde(skip_serializing_if = "Option::is_none")]
    /// The timeout in seconds.
    ///
    /// default is 60s
    pub timeout: Option<u32>,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Information about the Atenea node.
pub struct Info {
    /// The semver version of the Atenea server.
    pub version: Version,
    /// The millisecond unix timestamp when the Atenea jar was built.
    pub build_time: u64,
    /// The git information of the Atenea server.
    pub git: Git,
    /// The JVM version the Atenea server is running on.
    pub jvm: String,
    /// The Lavaplayer version being used by the Atenea server.
    pub lavaplayer: String,
    /// The enabled source managers for the Atenea server.
    pub source_managers: Vec<String>,
    /// The enabled filters for the Atenea server.
    pub filters: Vec<String>,
    /// The enabled plugins for the Atenea server.
    pub plugins: Vec<Plugin>,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Git {
    /// The branch the Atenea server was built on.
    pub branch: String,
    /// The commit the Atenea server was built on.
    pub commit: String,
    /// The millisecond unix timestamp for when the commit was created.
    pub commit_time: u64,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
pub struct Plugin {
    /// The name of the plugin
    pub name: String,
    /// The version of the plugin
    pub version: String,
}

#[derive(Debug, Clone, PartialEq, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Check out [Semantic Versioning 2.0.0](https://semver.org/) to know what these fields mean.
pub struct Version {
    pub semver: String,
    pub major: u8,
    pub minor: u8,
    pub patch: u8,
    pub pre_release: Option<String>,
    pub build: Option<String>,
}
