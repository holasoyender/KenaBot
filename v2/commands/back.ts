/*
 * File:          back.ts
 * Path:          v2\commands\back.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 21:21:38 +01:00
 *
 * SHA-256: 902ffe06c5c12543ebe08e31230a1ed3cdf2a8d04d7aabbc00ad5bc125bab645
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "back",
    desc: "Ir a la canción anterior de la cola",
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

        const track = player.queue.previous;
        if (!track) return event.reply({
            content: lang.commands.back.no_previous,
            ephemeral: true
        });

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

        player.queue.add(track, 0);
        player.stop();

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.back.success)

        return event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("back")
        .setDescription("Ir a la canción anterior de la cola")
}
