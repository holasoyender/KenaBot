/*
 * File:          select_menu.rs
 * Path:          v4\src\events\select_menu.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 6cd11b961c63dc7642324bbebcaab2374687bc87424fa52cd9b0733ac63302f6
 *
 */


use skua::all::{ChannelId, ComponentInteraction, Context, GenericId, Interaction, RoleId, UserId};
use crate::discord::Handler;

pub async fn channel_select_menu(handler: &Handler, ctx: Context, interaction: Interaction, mut select_menu: &ComponentInteraction, values: &Vec<ChannelId>) {
    info!("Select menu clicked: {:?}", select_menu);
}

pub async fn string_select_menu(handler: &Handler, ctx: Context, interaction: Interaction, mut select_menu: &ComponentInteraction, values: &Vec<String>) {
    info!("Select menu clicked: {:?}", select_menu);
}

pub async fn user_select_menu(handler: &Handler, ctx: Context, interaction: Interaction, mut select_menu: &ComponentInteraction, values: &Vec<UserId>) {
    info!("Select menu clicked: {:?}", select_menu);
}

pub async fn role_select_menu(handler: &Handler, ctx: Context, interaction: Interaction, mut select_menu: &ComponentInteraction, values: &Vec<RoleId>) {
    info!("Select menu clicked: {:?}", select_menu);
}

pub async fn mentionable_select_menu(handler: &Handler, ctx: Context, interaction: Interaction, mut select_menu: &ComponentInteraction, values: &Vec<GenericId>) {
    info!("Select menu clicked: {:?}", select_menu);
}