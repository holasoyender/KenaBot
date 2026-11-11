/*
 * File:          remove.ts
 * Path:          v2\commands\remove.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-18 21:11:10 +02:00
 *
 * SHA-256: 1149fd04b84106f560fe646d23faa364c5e4d929988d27a9f9a3ceb7be274290
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember, MessageEmbed } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import { checkChannel, checkRole } from "../utils/djrole";
import { getTrackByURL } from "../plugins/spotify/api/api";

module.exports = {
    name: "remove",
    desc: "Quitar una canción de la lista de reproducción.",
    category: "Music",
    permissions: "everyone",
    usage: "<Nº Canción>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const player: any = client.manager?.get(event.guild.id);
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
        if (!$l && $l?.toString() !== "0") return;

        if (($l) > player.queue.size || $l < 1) return event.reply({
            content: lang.commands.remove.invalid.replace("%s%", $l.toString()),
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
                    return event.reply({ embeds: [embed], ephemeral: true })
                }
            }
        }

        const t = player.metadata[0];
        let m = player.metadata;

        m.shift();

        const song = player.metadata[$l - 1];
        const rawSong = player.queue[$l - 1];

        m.splice($l - 1, 1);
        player.queue.remove($l - 1);

        m = [t, ...m];
        player.metadata = m;

        const spInfo = await getTrackByURL(song)

        const metadata = {
            title: spInfo?.name ?? rawSong?.title,
        }

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.remove.success.replace("%s%", metadata.title))

        await event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("remove")
        .setDescription("Quitar una canción de la lista de reproducción.")
        .addIntegerOption(option =>
            option.setName('canción')
                .setDescription('Número de la canción a quitar')
                .setRequired(true))
}
