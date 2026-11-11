/*
 * File:          player.rs
 * Path:          v4\src\atenea\model\player.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: 691176a43078ddc7d89bf21ce0647c072410d8f76796d9365c7de0cdc2a7010c
 *
 */


use serde::Serialize;
use skua::all::GuildId;
use crate::atenea::model::*;

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Information about the player of a guild.
pub struct Player {
    #[serde(deserialize_with = "deserialize_number_from_string")]
    pub guild_id: GuildId,
    /// The currently playing track.
    pub track: Option<track::TrackData>,
    /// The current volume of the player.
    pub volume: u16,
    /// Whether the player is paused or not.
    pub paused: bool,
    /// The state of the player.
    pub state: State,
    /// The filters currently in use by the player
    pub filters: Option<Filters>,
    /// The voice connection information of the player.
    pub voice: ConnectionInfo,
    // The queue of the player.
    //pub queue: VecDeque<TrackInQueue>,
    // The requester
    pub requester: Option<String>,
}

#[derive(Hash, PartialEq, Eq, PartialOrd, Ord, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct State {
    /// Unix timestamp in milliseconds.
    pub time: u64,
    /// The current position of the track in milliseconds.
    pub position: u64,
    /// Whether Atenea is connected to the discord voice gateway.
    pub connected: bool,
    #[serde(deserialize_with = "deserialize_option_number")]
    /// The latency of the node to the Discord voice gateway in milliseconds.
    ///
    /// None if not connected.
    pub ping: Option<u32>,
}

#[derive(Hash, PartialEq, Eq, PartialOrd, Ord, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Discord voice websocket connection information.
pub struct ConnectionInfo {
    /// The Discord voice endpoint to connect to.
    ///
    /// Provided by `Voice Server Update`.
    pub endpoint: String,
    /// The Discord voice token to authenticate with.
    ///
    /// Provided by `Voice Server Update`.
    pub token: String,
    /// The Discord voice session id to authenticate with.
    ///
    /// Not to be confused by the Atenea `session_id`.
    ///
    /// Provided by `Voice State Update`.
    pub session_id: String,
}

impl ConnectionInfo {
    pub fn fix(&mut self) {
        self.endpoint = self.endpoint.replace("wss://", "");
    }
}

use skua_voice::ConnectionInfo as SongbirdConnectionInfo;

impl From<SongbirdConnectionInfo> for ConnectionInfo {
    fn from(connection_info: SongbirdConnectionInfo) -> ConnectionInfo {
        ConnectionInfo {
            endpoint: connection_info.endpoint,
            token: connection_info.token,
            session_id: connection_info.session_id,
        }
    }
}

#[derive(PartialEq, Debug, Clone, Default, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct Filters {
    /// Adjusts the player volume from 0.0 to 5.0, where 1.0 is 100%.
    ///
    /// NOTE: Values >1.0 may cause clipping
    #[serde(skip_serializing_if = "Option::is_none")]
    pub volume: Option<u16>,
    /// Adjusts 15 different bands.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub equalizer: Option<Vec<Equalizer>>,
    /// Eliminates part of a band, usually targeting vocals.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub karaoke: Option<Karaoke>,
    /// Changes the speed, pitch, and rate.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub timescale: Option<Timescale>,
    /// Creates a shuddering effect, where the volume quickly oscillates.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub tremolo: Option<TremoloVibrato>,
    /// Creates a shuddering effect, where the pitch quickly oscillates.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub vibrato: Option<TremoloVibrato>,
    /// Rotates the audio around the stereo channels/user headphones (aka Audio Panning).
    #[serde(skip_serializing_if = "Option::is_none")]
    pub rotation: Option<Rotation>,
    /// Distorts the audio.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub distortion: Option<Distortion>,
    /// Mixes both stereo channels (left and right).
    #[serde(skip_serializing_if = "Option::is_none")]
    pub channel_mix: Option<ChannelMix>,
    /// Filters out higher frequencies.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub low_pass: Option<LowPass>,
    /// Filter plugin configurations.
    #[serde(skip_serializing_if = "Option::is_none")]
    pub plugin_filters: Option<serde_json::Value>,
}

impl From<crate::atenea::player_context::context::Filters> for Filters {
    fn from(filters: crate::atenea::player_context::context::Filters) -> Self {
        let mut audio_filters = Filters::default();

        if filters.bass_boost {
            audio_filters.equalizer = Some(vec![Equalizer { band: 1, gain: 0.25 }]);
        }

        if filters.spacial {
            audio_filters.rotation = Some(Rotation { rotation_hz: Some(0.2) });
        }

        if filters.night_core {
            audio_filters.timescale = Some(Timescale { speed: Some(1.3), pitch: Some(1.0), rate: Some(1.0) });
        }

        if filters.vapor_wave {
            audio_filters.timescale = Some(Timescale { speed: Some(0.85), pitch: Some(0.85), rate: Some(1.0) });
        }

        if filters.treble {
            audio_filters.equalizer = Some(vec![
                Equalizer { band: 0, gain: 0.6 },
                Equalizer { band: 1, gain: 0.67 },
                Equalizer { band: 2, gain: 0.67 },
                Equalizer { band: 3, gain: 0.0 },
                Equalizer { band: 4, gain: -0.25 },
                Equalizer { band: 5, gain: 0.15 },
                Equalizer { band: 6, gain: -0.25 },
                Equalizer { band: 7, gain: 0.23 },
                Equalizer { band: 8, gain: 0.35 },
                Equalizer { band: 9, gain: 0.45 },
                Equalizer { band: 10, gain: 0.55 },
                Equalizer { band: 11, gain: 0.6 },
                Equalizer { band: 12, gain: 0.55 },
                Equalizer { band: 13, gain: 0.0 },
                Equalizer { band: 14, gain: 0.0 },
            ]);
        }

        if filters.tremolo {
            audio_filters.tremolo = Some(TremoloVibrato { frequency: Some(10.0), depth: Some(0.3) });
        }

        if filters.karaoke {
            audio_filters.karaoke = Some(Karaoke { level: Some(1.0), mono_level: Some(1.0), filter_band: Some(220.0), filter_width: Some(100.0) });
        }

        if filters.speed {
            audio_filters.timescale = Some(Timescale { speed: Some(1.5010), pitch: Some(1.2450), rate: Some(1.9210) });
        }

        if filters.pitch {
            audio_filters.timescale = Some(Timescale { speed: Some(1.0), pitch: Some(1.5010), rate: Some(1.0) });
        }

        audio_filters
    }
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Mixes both channels (left and right), with a configurable factor on how much each channel affects the other.
///
/// With the defaults, both channels are kept independent of each other.
/// Setting all factors to 0.5 means both channels get the same audio.
/// All values are (0.0 <= x <= 1.0)
pub struct ChannelMix {
    pub left_to_left: Option<f64>,
    pub left_to_right: Option<f64>,
    pub right_to_left: Option<f64>,
    pub right_to_right: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Distortion effect.
///
/// It can generate some pretty unique audio effects.
pub struct Distortion {
    pub sin_offset: Option<f64>,
    pub sin_scale: Option<f64>,
    pub cos_offset: Option<f64>,
    pub cos_scale: Option<f64>,
    pub tan_offset: Option<f64>,
    pub tan_scale: Option<f64>,
    pub offset: Option<f64>,
    pub scale: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// A fixed band equalizer.
pub struct Equalizer {
    /// The band (0 to 14)
    pub band: u8,
    /// The gain (-0.25 to 1.0)
    ///
    /// -0.25 means the given band is completely muted, and 0.25 means it is doubled.
    /// Modifying the gain could also change the volume of the output.
    pub gain: f64,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Uses equalization to eliminate part of a band, usually targeting vocals.
pub struct Karaoke {
    /// The level (0 to 1.0 where 0.0 is no effect and 1.0 is full effect)
    pub level: Option<f64>,
    /// The mono level (0 to 1.0 where 0.0 is no effect and 1.0 is full effect)
    pub mono_level: Option<f64>,
    /// The filter band (in Hz)
    pub filter_band: Option<f64>,
    /// The filter width.
    pub filter_width: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Higher frequencies get suppressed, while lower frequencies pass through this filter.
pub struct LowPass {
    /// The smoothing factor (1.0 < x)
    ///
    /// Any smoothing values equal to or less than 1.0 will disable the filter.
    pub smoothing: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Rotates the sound around the stereo channels/user headphones (aka Audio Panning).
///
/// It can produce an effect similar to [this](https://youtu.be/QB9EB8mTKcc) without the reverb.
pub struct Rotation {
    /// The frequency of the audio rotating around the listener in Hz.
    ///
    /// 0.2 is similar to the example video above
    pub rotation_hz: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Changes the speed, pitch, and rate.
///
/// All default to 1.0.
pub struct Timescale {
    /// The playback speed (0.0 <= x)
    pub speed: Option<f64>,
    /// The pitch (0.0 <= x)
    pub pitch: Option<f64>,
    /// The rate (0.0 <= x)
    pub rate: Option<f64>,
}

#[derive(PartialEq, PartialOrd, Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
/// Tremolo uses amplification to create a shuddering effect, where the volume quickly oscillates.
///
/// [Demo](https://en.wikipedia.org/wiki/File:Fuse_Electronics_Tremolo_MK-III_Quick_Demo.ogv)
///
/// Vibrato is similar to tremolo, but rather than oscillating the volume, it oscillates the pitch.
pub struct TremoloVibrato {
    /// For tremolo (0.0 < x)
    /// For vibrato (0.0 < x <= 14.0)
    pub frequency: Option<f64>,
    /// For both tremolo and vibrato (0.0 < x <= 1.0)
    pub depth: Option<f64>,
}
