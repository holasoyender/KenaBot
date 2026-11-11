/*
 * File:          language.rs
 * Path:          v4\src\locale\language.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: a79ca08318636e468bcbd3b4681b352ea37195935c86c00b8cd6a273676a7fcf
 *
 */


use serde::{Deserialize, Serialize};

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct Language {
    pub flag: String,
    pub name: String,
    pub description: String,
    pub commands: LocalizedCommands,
    pub events: LocalizedEvents,
    pub manager: LocalizedManager,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct LocalizedCommands {
    pub invite: String,
    pub help: HelpCommand,
    pub support: String,
    pub commands: String,
    pub autoplay: AutoplayCommand,
    pub artist: ArtistCommand,
    #[serde(rename = "_247")]
    pub radio: RadioCommand,
    pub play: PlayCommand,
    pub skip: String,
    pub skip_to: String,
    pub stop: String,
    pub stop_leave: String,
    pub pause: PauseCommand,
    pub clear_queue: String,
    pub volume: VolumeCommand,
    pub remove: RemoveCommand,
    pub queue: QueueCommand,
    pub now_playing: NowPlayingCommand,
    pub back: BackCommand,
    pub seek: SeekCommand,
    pub status: StatusCommand,
    pub shuffle: String,
    pub dashboard: String,
    #[serde(rename = "loop")]
    pub repeat: LoopCommand,
    pub filters: FilterCommand
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct LocalizedEvents {
    #[serde(rename = "trackStart")]
    pub track_start: TrackStartEvent,
    pub dm: DmReceivedEvent,
    #[serde(rename = "guildCreate")]
    pub guild_create: GuildCreateEvent,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct TrackStartEvent {
    pub embed_author: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct DmReceivedEvent {
    pub embed_title: String,
    pub embed_description: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct GuildCreateEvent {
    pub embed_author: String,
    pub embed_description: String,
    pub buttons: GuildCreateEventButtons
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct GuildCreateEventButtons {
    pub web: String,
    pub support: String,
    pub add: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct HelpCommand {
    pub add: HelpCommandAdd,
    pub desc: String,
    pub commands: HelpCommandCommands,
    pub support: HelpCommandSupport,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct HelpCommandAdd {
    pub title: String,
    pub add: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct HelpCommandCommands {
    pub title: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct HelpCommandSupport {
    pub title: String,
    pub support: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct AutoplayCommand {
    pub enabled: String,
    pub disabled: String,
    pub already_in: String,
    pub success: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct ArtistCommand {
    pub no_artist: String,
    pub embed_title: String,
    pub genres: String,
    pub followers: String,
    pub popularity: String,
    pub open_spotify: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct RadioCommand {
    pub no_channel: String,
    pub enabled: String,
    pub disabled: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct PlayCommand {
    pub no_song_found: String,
    pub duration: String,
    pub in_queue: String,
    pub pause_resume: String,
    pub stop: String,
    pub youtube_link: String,
    pub vc_disabled: String,
    pub different_vc: String,
    pub no_vc: String,
    pub no_vc_perms: String,
    pub songs: String,
    pub song: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct PauseCommand {
    pub already_paused: String,
    pub already_resumed: String,
    pub paused: String,
    pub resumed: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct VolumeCommand {
    pub set: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct RemoveCommand {
    pub invalid: String,
    pub success: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct QueueCommand {
    pub embed_author: String,
    pub embed_footer: String,
    pub now_playing: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct NowPlayingCommand {
    pub remaining: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct BackCommand {
    pub no_previous: String,
    pub success: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct SeekCommand {
    pub invalid_time: String,
    pub not_seekable: String,
    pub max_duration: String,
    pub forward_success: String,
    pub backward_success: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct StatusCommand {
    pub embed_title: String,
    pub embed_description: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct LoopCommand {
    pub repeat_track: String,
    pub repeat_queue: String,
    pub no_repeat: String,
    pub info: String
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct FilterCommand {
    pub embed_title: String,
    pub embed_description: String,
}

#[derive(Debug, Serialize, Deserialize, Clone)]
pub struct LocalizedManager {
    pub song_added: String,
    pub playlist_added: String,
    pub no_premium_twitch: String,
    pub no_premium_stage: String,
    pub max_duration: String,
    pub blocked_song: String,
    pub explicit_song: String,
    pub max_queue_size: String,
    pub cant_modify_queue: String,
    pub nothing_playing: String,
    #[serde(rename = "is247")]
    pub radio_enabled: String
}

impl Language {

    pub fn load_from_file(path: &std::path::Path) -> Result<Language, Box<dyn std::error::Error>> {
        let file = std::fs::File::open(path)?;
        let language: Language = serde_json::from_reader(file)?;
        Ok(language)
    }

}