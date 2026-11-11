/*
 * File:          mod.rs
 * Path:          v4\src\events\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 9fa4a11ad2e54ac2d0134cfd4594fff0a7d32a7a74495384cb9280418c32425c
 *
 */


use async_trait::async_trait;
use skua::all::{ComponentInteractionDataKind, Context, Event, EventHandler, GuildId, Interaction, Message, RawEventHandler, Ready, ShardStageUpdateEvent, VoiceState};
use crate::discord::Handler;
use crate::metrics::bot::inc_websocket_event;

mod ready;
mod shards;
mod slash_command;
mod button;
mod select_menu;
mod autocomplete;
mod voice;
mod message;

#[async_trait]
impl RawEventHandler for Handler {
    async fn raw_event(&self, _ctx: Context, ev: Event) {
        inc_websocket_event(ev.name().unwrap_or("unknown".to_string()).to_string());
    }
}

#[async_trait]
impl EventHandler for Handler {
    async fn cache_ready(&self, ctx: Context, guilds: Vec<GuildId>) {
        ready::cache_ready(self, ctx, guilds).await;
    }

    async fn shards_ready(&self, ctx: Context, total_shards: u32) {
        ready::shards_ready(self, ctx, total_shards).await;
    }

    async fn ready(&self, ctx: Context, data_about_bot: Ready) {
        ready::ready(self, ctx, data_about_bot).await;
    }

    async fn shard_stage_update(&self, ctx: Context, event: ShardStageUpdateEvent) {
        shards::shard_stage_update(self, ctx, event).await;
    }

    async fn voice_state_update(&self, ctx: Context, old: Option<VoiceState>, new: VoiceState) {
        if old.is_none() && new.channel_id.is_some() {
            voice::voice_channel_join(self, ctx, new).await;
        } else if old.is_some() && new.channel_id.is_some() {
            let old = old.unwrap();

            if old.channel_id.unwrap() == new.channel_id.unwrap() {
                if old.mute != new.mute {
                    voice::voice_server_mute(self, ctx, old, new).await;
                }
            }
        } else if old.is_some() && new.channel_id.is_none() {
            voice::voice_channel_leave(self, ctx, old.unwrap()).await;
        }
    }

    async fn interaction_create(&self, ctx: Context, interaction: Interaction) {

        match interaction.clone() {
            Interaction::Ping(_) => {},
            Interaction::Command(x) => slash_command::slash_command(self, ctx, interaction, x).await,
            Interaction::Component(x) => {
                match &x.data.kind {
                    ComponentInteractionDataKind::Button => button::button_click(self, ctx, interaction, &x).await,
                    ComponentInteractionDataKind::ChannelSelect { values } => select_menu::channel_select_menu(self, ctx, interaction, &x, values).await,
                    ComponentInteractionDataKind::StringSelect { values } => select_menu::string_select_menu(self, ctx, interaction, &x, values).await,
                    ComponentInteractionDataKind::UserSelect { values } => select_menu::user_select_menu(self, ctx, interaction, &x, values).await,
                    ComponentInteractionDataKind::RoleSelect { values } => select_menu::role_select_menu(self, ctx, interaction, &x, values).await,
                    ComponentInteractionDataKind::MentionableSelect { values } => select_menu::mentionable_select_menu(self, ctx, interaction, &x, values).await,
                    _ => {}
                }
            },
            Interaction::Autocomplete(x) => autocomplete::autocomplete(self, ctx, interaction, x).await,
            Interaction::Modal(_x) => {},
            _ => {}
        };
    }
    
    async fn message(&self, ctx: Context, new_message: Message) {
        if new_message.is_private() {
            message::message(self, ctx, new_message).await;
        }
    }
}