/*
 * File:          connection.rs
 * Path:          v4\src\io\voice\connection.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 51e8497156b80c9301e75db07e763f44b9e77e5c2230cddd4cdb546c97e237ee
 *
 */


use skua::all::{Cache, GuildId};
use skua::model::{
    id::{ChannelId, UserId},
};

pub enum VoiceConnection {
    User(ChannelId),
    Bot(ChannelId),
    Mutual(ChannelId, ChannelId),
    Separate(ChannelId, ChannelId),
    Neither,
}

pub fn check_voice_connections(guild_id: &GuildId, cache: &Cache, user_id: &UserId, bot_id: &UserId) -> VoiceConnection {
    let user_channel = get_voice_channel_for_user(guild_id, cache, user_id);
    let bot_channel = get_voice_channel_for_user(guild_id, cache, bot_id);

    if let (Some(bot_id), Some(user_id)) = (bot_channel, user_channel) {
        if bot_id.to_string() == user_id.to_string() {
            VoiceConnection::Mutual(bot_id, user_id)
        } else {
            VoiceConnection::Separate(bot_id, user_id)
        }
    } else if let (Some(bot_id), None) = (bot_channel, user_channel) {
        VoiceConnection::Bot(bot_id)
    } else if let (None, Some(user_id)) = (bot_channel, user_channel) {
        VoiceConnection::User(user_id)
    } else {
        VoiceConnection::Neither
    }
}

pub fn get_voice_channel_for_user(guild_id: &GuildId, cache: &Cache, user_id: &UserId) -> Option<ChannelId> {

    let guild = cache.guild(*guild_id)?;

    guild
        .voice_states
        .get(user_id)
        .and_then(|voice_state| voice_state.channel_id)
}