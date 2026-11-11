/*
 * File:          stop.ts
 * Path:          v2\commands\stop.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-15 17:52:41 +01:00
 *
 * SHA-256: d3e864b42e529884c6ec3bc1761d2b13b14b92047c71f1fcf922f724ac5b7aba
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "stop",
    desc: "Finaliza la lista de reproducción y saca al bot del canal de voz.",
    category: "Music",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const player = client.manager?.get(event.guild.id);
        if (!player) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (!player.queue.current) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

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

        let isLeave = false;

        if(config.premium.enabled){
            if(!config._247.enabled) {
                player.destroy();
                isLeave = true;
            }
        }else {
            player.destroy();
            isLeave = true;
        }

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            isLeave ? embed.setDescription(lang.commands.stop_leave) : embed.setDescription(lang.commands.stop)

        return event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("stop")
        .setDescription("Finaliza la lista de reproducción y saca al bot del canal de voz.")
}
