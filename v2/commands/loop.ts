/*
 * File:          loop.ts
 * Path:          v2\commands\loop.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-31 14:26:48 +02:00
 *
 * SHA-256: ed8df2d927e001557c1ac8a35db7a28162d4fdfc4a1cdedb9038c9759e04b3b0
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "loop",
    desc: "Activar o desactivar el bucle de la canción actual.",
    category: "Music",
    permissions: "everyone",
    usage: "<Estado Loop>",
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
        if (channel && channel.type == "GUILD_VOICE") {
            if (checkChannel(channel, config, player)) {
                if (!checkRole(event.member, config, player)) {
                    const embed = new MessageEmbed()
                        // @ts-ignore
                        .setColor(config.color)
                        .setDescription(lang.manager.dj_role_required)
                    return event.reply({embeds: [embed], ephemeral: true})
                }
            }
        }

        const $l = event.options.getInteger("estado");

        if (!$l) {
            const $embed = new MessageEmbed()
                // @ts-ignore
                .setColor(config.color)
                .setDescription(lang.commands.loop.info.replace("%s%", player.trackRepeat ? lang.commands.loop.repeat : lang.commands.loop.no_repeat))

            return event.reply({
                embeds: [$embed],
                ephemeral: false
            })
        }

        if ($l == 2) {
            if (player.trackRepeat) {
                const $embed = new MessageEmbed()
                    // @ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.loop.already.replace("%s%", lang.commands.loop.repeat))

                return event.reply({
                    embeds: [$embed],
                    ephemeral: true
                })
            } else {
                player.setTrackRepeat(true);
                const $embed = new MessageEmbed()
                    // @ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.loop.action.replace("%s%", lang.commands.loop.repeat))

                return event.reply({
                    embeds: [$embed],
                    ephemeral: true
                })
            }
        }
        if ($l == 3) {
            if (!player.trackRepeat) {
                const $embed = new MessageEmbed()
                    // @ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.loop.already.replace("%s%", lang.commands.loop.no_repeat))

                return event.reply({
                    embeds: [$embed],
                    ephemeral: true
                })
            } else {
                player.setTrackRepeat(false);
                const $embed = new MessageEmbed()
                    // @ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.commands.loop.action.replace("%s%", lang.commands.loop.no_repeat))

                return event.reply({
                    embeds: [$embed],
                    ephemeral: true
                })
            }
        }

    },
    slashInfo: new SlashCommandBuilder()
        .setName("loop")
        .setDescription("Activar o desactivar el bucle de la canción actual.")
        .addIntegerOption(option =>
            option.setName('estado')
                .setDescription('Estado del bucle')
                .setRequired(false)
                .addChoices(
                    {name: "En bucle", value: 2},
                    {name: "Sin bucle", value: 3}
                ))
}