/*
 * File:          button.rs
 * Path:          v4\src\events\button.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-03-10 19:48:01 +01:00
 *
 * SHA-256: f09d9118ccb009b39ae51d6d66b42d67d240b44310262bb85456f3e104b1f528
 *
 */


use mongodb::bson::doc;
use skua::all::{ComponentInteraction, Context, Interaction};
use crate::commands::filter::FilterCommand;
use crate::commands::pause::PauseCommand;
use crate::commands::queue::QueueCommand;
use crate::commands::skip::SkipCommand;
use crate::commands::stop::StopCommand;
use crate::commands::repeat::LoopCommand;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::io::voice::connection::{check_voice_connections, VoiceConnection};
use crate::managers::component::{Button, KenaComponentInteraction};

pub async fn button_click(handler: &Handler, ctx: Context, _: Interaction, button: &ComponentInteraction) {

    /*
    Structure of a button ID:
    <allowed_user>::<button_type>::<command>::<args split by :>

    # allowed_user:
        - all: everyone can use this button
        - queue: only the users who can modify the queue can use this button
        - <user_id>: only the user with the specified ID can use this button
    # button_type:
        - cmd: a normal button
        - tgl: a toggle button
    # command:
        - the command to execute when the button is clicked
    # args:
        - the arguments to pass to the command, split by ":"

     # example
     the button with id "396683727868264449::command::queue::0:next" has this properties:
        - allowed_user: 396683727868264449
        - button_type: command
        - command: queue
        - args: [ 0, next ]
    */

    let mut interaction = KenaComponentInteraction::from(button.clone());
    let id = button.data.custom_id.clone();
    let guild_id = interaction.guild_id.unwrap().to_string();

    let args_dummy = id.split("::").collect::<Vec<&str>>();
    let allowed_user_id = args_dummy[0].parse::<String>().unwrap_or("all".to_string());

    let collection = handler.db.collection::<Guild>("Guilds");
    let query = doc! {"guildID": &guild_id};

    let guild = match collection.find_one(query, None).await.unwrap() {
        Some(guild) => guild,
        None => {
            let insert_guild = Guild::default(&guild_id);
            collection.insert_one(&insert_guild, None).await.unwrap();

            insert_guild
        }
    };

    let lang = handler.language_manager.get_language_or_default_str(&guild.lang);

    match allowed_user_id.as_str() {
        "all" => {},
        "queue" => {
            let guild_id = button.guild_id.unwrap();
            let bot_id = handler.bot_id.read().unwrap();

            match check_voice_connections(&guild_id, &ctx.cache, &button.user.id, &bot_id) {
                VoiceConnection::Neither | VoiceConnection::Separate(_, _) => {
                    interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(&lang.manager.cant_modify_queue)
                        .emphemeral()
                        .exec(&ctx.http)
                        .await
                        .unwrap();
                    return;
                },
                VoiceConnection::User(_) | VoiceConnection::Bot(_) => {
                    interaction.reply()
                        .emoji(shared::emojis::EMOJIS.error)
                        .content(&lang.manager.nothing_playing)
                        .emphemeral()
                        .exec(&ctx.http)
                        .await
                        .unwrap();
                    return;
                }
                _ => {}
            };
        }
        _ => {
            if allowed_user_id != button.user.id.to_string() {
                interaction.reply()
                    .content("You are not allowed to use this button")
                    .emphemeral()
                    .exec(&ctx.http)
                    .await
                    .unwrap();

                return;
            }
        }
    }

    let button_type = args_dummy[1];
    let command = args_dummy[2];
    let raw_args = args_dummy.get(3).unwrap_or(&"");
    let args = if raw_args == &"" {
        vec![]
    } else {
        raw_args.split(":").collect::<Vec<&str>>()
    };

    match button_type {
        "cmd" => {
            let a = match command {
                "queue" => QueueCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                "pause" => PauseCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                "skip" => SkipCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                "stop" => StopCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                "loop" => LoopCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                "filter" => FilterCommand::button_click(ctx.clone(), interaction.clone(), button, guild, lang.clone(), handler, args).await,
                _ => {
                    interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content("Unknown command")
                        .emphemeral()
                        .exec(&ctx.http)
                        .await
                        .unwrap();

                    return;
                }
            };

            match a {
                Ok(_) => {}
                Err(e) => {
                    
                    error!("Error executing button command: {}", e);
                    
                    interaction.reply()
                        .emoji(shared::emojis::EMOJIS.warning)
                        .content("Unknown error")
                        .emphemeral()
                        .exec(&ctx.http)
                        .await
                        .unwrap();

                    return;
                }
            }
        }
        _ => {
            interaction.reply()
                .emoji(shared::emojis::EMOJIS.warning)
                .content("Unknown button type")
                .emphemeral()
                .exec(&ctx.http)
                .await
                .unwrap();

            return;
        }
    };

}