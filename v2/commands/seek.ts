/*
 * File:          seek.ts
 * Path:          v2\commands\seek.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 21:21:38 +01:00
 *
 * SHA-256: f3b97ec117144ea60efc058cab1f8d43dee1d8fedf510c7b8fa8de81c7180dbf
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import ms from "../utils/ms";
import {convertTime} from "../utils/convert";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "seek",
    desc: "Saltar a un momento específico de la canción",
    category: "Music",
    permissions: "everyone",
    usage: "<Tiempo>",
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

        const $l = event.options.getString("tiempo");
        if(!$l) return;


        // @ts-ignore
        const time = ms($l)
        if(!time) return event.reply({
            content: lang.commands.seek.invalid_time,
            ephemeral: true
        })

        const position = player.position;
        const duration = player.queue.current.duration || 0;

        if (time <= duration) {
            if (time > position) {
                player.seek(time);

                const embed = new MessageEmbed()
                    //@ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.seek.forward_success.replace("%s%", `${convertTime(time)} / ${convertTime(duration)}`))

                return event.reply({
                    embeds: [embed],
                    ephemeral: false
                })
            } else {
                player.seek(time);

                const embed = new MessageEmbed()
                    //@ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.seek.backward_success.replace("%s%", `${convertTime(time)} / ${convertTime(duration)}`))

                return event.reply({
                    embeds: [embed],
                    ephemeral: false
                })
            }
        } else {
            return event.reply({
                content: lang.commands.seek.max_duration,
                ephemeral: true
            })
        }
    },
    slashInfo: new SlashCommandBuilder()
        .setName("seek")
        .setDescription("Saltar a un momento específico de la canción")
        .addStringOption(option =>
            option.setName('tiempo')
                .setDescription('Momento de la canción al que saltar')
                .setRequired(true))
}
