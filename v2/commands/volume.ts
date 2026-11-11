/*
 * File:          volume.ts
 * Path:          v2\commands\volume.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 21:21:38 +01:00
 *
 * SHA-256: 728a70abe240a301d55f70706cb6da353a1d99fbe16183d6f1b587d4dfd918d1
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "volume",
    desc: "Establecer el volumen de la canción actual.",
    category: "Music",
    permissions: "everyone",
    usage: "<New Volume>",
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

        const $vl = event.options.getInteger("nivel");

        if (!$vl) {
            const $embed = new MessageEmbed()
                // @ts-ignore
                .setColor(config.color)
                .setDescription(lang.commands.volume.info.replace("%s%", player.volume.toString()))

            return event.reply({
            embeds: [$embed],
            ephemeral: true
        })}

        if ($vl > 100 || $vl < 0) return event.reply({
            content: lang.commands.volume.invalid_level,
            ephemeral: true
        })

        player.setVolume($vl);

        const $embed = new MessageEmbed()
            // @ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.volume.set.replace("%s%", $vl.toString()))

        await event.reply({
            embeds: [$embed],
            ephemeral: false
        })



    },
    slashInfo: new SlashCommandBuilder()
        .setName("volume")
        .setDescription("Establecer el volumen de la canción actual.")
        .addIntegerOption(option =>
            option.setName('nivel')
                .setDescription('Nivel de volumen a establecer ( 0 - 100 )')
                .setRequired(false))
}
