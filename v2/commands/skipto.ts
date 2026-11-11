/*
 * File:          skipto.ts
 * Path:          v2\commands\skipto.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-11 23:34:41 +02:00
 *
 * SHA-256: 90f80a091d79d446c2b3dec2e1a546086769f790a8a03b3bfa54e388ac6966c4
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "skipto",
    desc: "Saltar a una canción de la lista de reproducción en específico.",
    category: "Music",
    permissions: "everyone",
    usage: "<Nª Canción>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const player:any = client.manager?.get(event.guild.id);
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

        const $l = event.options.getInteger("canción");
        if(!$l) return;

        if ($l > player.queue.size || $l < 0) return event.reply({
            content: lang.commands.remove.invalid.replace("%s%", $l.toString()),
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

        player.queue.remove(0, $l - 1);

	if(!player.metadata) player.metadata = [];
        player.metadata?.splice(0, $l - 1);

        player.stop();

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.skip_to.replace("%s%", $l.toString()))

        await event.reply({
            embeds: [embed],
            ephemeral: false
        })

    },
    slashInfo: new SlashCommandBuilder()
        .setName("skipto")
        .setDescription("Saltar a una canción de la lista de reproducción en específico.")
        .addIntegerOption(option =>
            option.setName('canción')
                .setDescription('Número de la canción a reproducir')
                .setRequired(true))
}
