/*
 * File:          slash_command.rs
 * Path:          v4\src\events\slash_command.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: d9867635f1e540cca51c8a979895fad48495a7854b30021a36e8ec12b097c506
 *
 */


use mongodb::bson::doc;
use skua::all::{CommandInteraction, Context, Interaction};
use shared::constants::get_constants;
use crate::database::schema::guild::Guild;
use crate::discord::Handler;
use crate::managers::interactions::KenaCommandInteraction;

pub async fn slash_command(handler: &Handler, ctx: Context, interaction: Interaction, command: CommandInteraction) {
    let command_name = command.data.name.clone();

    if let Some(cmd) = handler.command_manager.commands.get(&command_name) {
        let mut kena_command = KenaCommandInteraction::from(command);
        let guild_id = kena_command.guild_id.unwrap().to_string();

        let mut runner = cmd.lock().await;

        let collection = handler.db.collection::<Guild>("Guilds");
        let query = doc! {"guildID": &guild_id};
        let member = match kena_command.clone().member {
            Some(member) => member,
            None => {
                kena_command.reply()
                    .content("You must be in a server to use this command")
                    .exec(&ctx.http)
                    .await
                    .unwrap();

                return;
            }
        };

        let guild = match collection.find_one(query, None).await.unwrap() {
            Some(guild) => guild,
            None => {
                let insert_guild = Guild::default(&guild_id);
                collection.insert_one(&insert_guild, None).await.unwrap();

                insert_guild
            }
        };
        
        let constants = get_constants(&handler.cache).await;

        if guild.blocked.enabled && !constants.owner_ids.contains(&kena_command.user.id) {
            kena_command.reply()
                .content(format!("This server is blocked from using Kena, reason: {}", guild.blocked.reason))
                .exec(&ctx.http)
                .await
                .unwrap();

            return;
        }

        if guild.disabled.commands.contains(&command_name) && !constants.owner_ids.contains(&kena_command.user.id) && !member.permissions(&ctx).unwrap().manage_guild() {
            kena_command.reply()
                .content("This command is disabled in this server")
                .exec(&ctx.http)
                .await
                .unwrap();

            return;
        }

        if guild.disabled.channels.contains(&kena_command.channel_id.to_string()) && !constants.owner_ids.contains(&kena_command.user.id) && !member.permissions(&ctx).unwrap().manage_guild() {
            kena_command.reply()
                .content("This command is disabled in this channel")
                .exec(&ctx.http)
                .await
                .unwrap();

            return;
        }

        let command_permissions = runner.permissions();
        if command_permissions.len() > 0 && !constants.owner_ids.contains(&kena_command.user.id) {
            let mut has_permission = false;
            let user_permissions = member.permissions(&ctx).unwrap();

            for permission in command_permissions {
                if user_permissions.contains(permission) {
                    has_permission = true;
                    break;
                }
            }

            if !has_permission {
                kena_command.reply()
                    .content("You don't have permission to use this command")
                    .exec(&ctx.http)
                    .await
                    .unwrap();

                return;
            }
        }

        let lang = handler.language_manager.get_language_or_default_str(&guild.lang);

        if let Err(why) = runner.run(ctx.clone(), interaction, kena_command.clone(), guild, lang.clone(), handler).await {
            error!("Error running command {}: {:?}", command_name, why);
            kena_command.defer(&ctx.http).await.unwrap_or(());
            kena_command.reply()
                .emoji(shared::emojis::EMOJIS.error)
                .content("Unknown error!")
                .emphemeral()
                .exec(&ctx.http)
                .await
                .unwrap_or(());
        }
    } else {
        error!("Command {} not found", command_name);
    }
}