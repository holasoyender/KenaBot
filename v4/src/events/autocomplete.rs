/*
 * File:          autocomplete.rs
 * Path:          v4\src\events\autocomplete.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-02-13 17:50:39 +01:00
 *
 * SHA-256: 5122c6cb674e339c98a2b03cb196c841788702ee3bf5c8b44f53c11b59998694
 *
 */


use skua::all::{CommandInteraction, Context, CreateAutocompleteResponse, CreateInteractionResponse, Interaction};
use crate::discord::Handler;
use crate::io::voice::connection::get_voice_channel_for_user;
use crate::util::string::is_url;

pub async fn autocomplete(handler: &Handler, ctx: Context, interaction: Interaction, _: CommandInteraction) {

    let event = interaction.as_autocomplete().unwrap();
    if event.data.name == "play" {

        let mut search = event.data.options.first().unwrap().value.as_str().unwrap().to_string();

        if search.len() == 0 {
            event.create_response(ctx.http, CreateInteractionResponse::Autocomplete(CreateAutocompleteResponse::default())).await.unwrap();
            return;
        }

        if search.len() > 100 {
            search = format!("{}...", &search[..97]);
        }

        let mut response = CreateAutocompleteResponse::default();

        if search.len() < 3 ||
            event.member.is_none() ||
            is_url(&search) ||
            get_voice_channel_for_user(&event.guild_id.unwrap(), &ctx.cache, &event.member.as_ref().unwrap().user.id).is_none()
        {
            response = response.add_string_choice(search.clone(), search.clone());
        } else {
            let names = handler.spotify_manager.search_autocomplete(search.clone()).await;

            if names.is_empty() {
                response = response.add_string_choice(search.clone(), search);
            } else {
                for name in names {
                    if name.len() > 100 {
                        let new_name = format!("{}...", &name[..97]);
                        response = response.add_string_choice(new_name.clone(), new_name);
                    } else {
                        response = response.add_string_choice(name.clone(), name);
                    }
                }
            }
        }

        event.create_response(ctx.http, CreateInteractionResponse::Autocomplete(response)).await.unwrap();
    }
}