/*
 * File:          message.rs
 * Path:          v4\src\events\message.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: 55b6cb2802c0e19a40c073727f800b24c795a82c9b56858ee0de1c815925a8e3
 *
 */


use skua::all::{Context, CreateMessage, Message};
use skua::builder::{CreateButton, CreateEmbed};
use shared::constants::get_constants;
use crate::discord::Handler;
use crate::locale::locales::Locales;
use crate::locale::value::Value;

pub async fn message(handler: &Handler, ctx: Context, message: Message) {
    
    if message.author.bot || message.author.system {
        return;
    }

    let constants = get_constants(&handler.cache).await;
    if constants.owner_ids.contains(&message.author.id) {

        let prefix = constants.prefix;
        let content = message.content.as_str();
        if !content.starts_with(&prefix) {
            return;
        }

        let args = content.split_whitespace().collect::<Vec<&str>>();
        let command_name = args[0].replace(prefix.as_str(), "");

        if let Some(cmd) = handler.dev_command_manager.commands.get(&command_name) {
            let mut runner = cmd.lock().await;

            if let Err(why) = runner.run(ctx.clone(), message.clone(), args.clone().split_off(1), handler).await {
                error!("Error running command {}: {:?}", command_name, why);
                message.reply(&ctx.http, format!("Unknown error: {:?}", why)).await.unwrap();
            }
        } else {
            message.reply(&ctx.http, "Command not found").await.unwrap();
        }
        
    } else {

        let user_lang = match message.author.locale {
            None => Locales::from_str_or_default(constants.default_language.as_str()),
            Some(locale_code) => Locales::from_str_or_default(locale_code.as_str())
        };

        let lang = handler.language_manager.get_language_or_default(&user_lang).clone();

        let embed = CreateEmbed::new()
            .color(0x2f3136)
            .title(lang.events.dm.embed_title.format(&[handler.bot_name.read().as_ref().unwrap()]))
            .description(lang.events.dm.embed_description);

        message.channel_id.send_message(&ctx.http,
                                        CreateMessage::new()
                                            .embed(embed)
                                            .button(CreateButton::new_link(constants.website).label(lang.events.guild_create.buttons.web))
                                            .button(CreateButton::new_link(constants.support_invite).label(lang.events.guild_create.buttons.support))
                                            .button(CreateButton::new_link(constants.invite).label(lang.events.guild_create.buttons.add))
        ).await.unwrap();
    }
    
}