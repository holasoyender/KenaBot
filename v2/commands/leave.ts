/*
 * File:          leave.ts
 * Path:          v2\commands\leave.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-14 15:40:41 +01:00
 *
 * SHA-256: 6b42770e6c0bcefa31429488a21a9ff39fd21a766fe9a6f29bc7a11ac33bd3e8
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import {checkChannel, checkRole} from "../utils/djrole";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";

module.exports = {
    name: "leave",
    desc: "Sacar del canal de voz al bot",
    category: "Music",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        if(config.premium.enabled && config._247.enabled) return event.reply({
            content: lang.manager.is247,
            ephemeral: true
        });

        const player = client.manager?.get(event.guild.id);
        if (!player) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if(player && !event.guild.me.voice.channel) {
            player.setTrackRepeat(false);
            player.stop();
            player.queue.clear();
            if(config.premium.enabled){
                if(!config._247.enabled)
                    player.destroy();
            }else
                player.destroy();

            const embed = new MessageEmbed()
                //@ts-ignore
                .setColor(config.color)
                .setDescription(lang.commands.stop)

            return event.reply({
                embeds: [embed],
                ephemeral: false
            })
        }

        if (event.guild.me.voice.channel !== event.member.voice.channel) return event.reply({
            content: lang.manager.cant_modify_queue,
            ephemeral: true
        })

        const channel = event.guild.channels.cache.get(`${player.voiceChannel}`)
        if(channel && channel.type == "GUILD_VOICE") {
            if(checkChannel(channel, config, player)) {
                if(!checkRole(event.member, config, player)) {
                    const embed = new MessageEmbed()
                        // @ts-ignore
                        .setColor(config.color)
                        .setDescription(lang.manager.dj_role_required)
                    return event.reply({embeds: [embed], ephemeral: true})
                }
            }
        }

        player.setTrackRepeat(false);
        player.stop();
        player.queue.clear();
        if(config.premium.enabled){
            if(!config._247.enabled)
                player.destroy();
        }else
            player.destroy();

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.stop)

        return event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("leave")
        .setDescription("Sacar del canal de voz al bot")
}
