/*
 * File:          nowplaying.ts
 * Path:          v2\commands\nowplaying.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 01:14:19 +02:00
 *
 * SHA-256: c2a6ad9fad8ef2e3913d31344cd1473ba7fba6e21183393a494be5d4618fd30a
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember, MessageEmbed } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import { convertTime } from "../utils/convert";
import progress from "../functions/progessbar"
import { stringFormat } from "../utils/stringFormat";
import { getTrackByURL } from "../plugins/spotify/api/api";

module.exports = {
    name: "nowplaying",
    desc: "Información de la canción sonando",
    category: "Music",
    permissions: "everyone",
    usage: "",
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

        if (!player.queue.current || !player.queue.current.duration) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        const song = player.queue.current;
        const seek = player.position;

        let spInfo
        if (player.metadata)
            spInfo = await getTrackByURL(player.metadata[0])

        const metadata = {
            url: player.metadata[0] ?? song.uri,
            title: spInfo?.name ?? song.title,
            duration: spInfo?.duration_ms ?? song.duration,
        }

        const left = metadata.duration - seek;

        const nowPlaying = new MessageEmbed()
            .setAuthor({ name: stringFormat(metadata.title), iconURL: client.user?.displayAvatarURL(), url: metadata.url })
            //@ts-ignore
            .setColor(config.color).setDescription(`\`${convertTime(seek)}\`  ` + progress((metadata.duration == 0 ? seek : metadata.duration), seek, 20, "▬", "\\🔵")[0] + `  \`${convertTime(metadata.duration)}\``);

        // @ts-ignore
        if (song.duration > 0) nowPlaying.setFooter({ text: lang.commands.now_playing.remaining + convertTime(left) });

        await event.reply({
            embeds: [nowPlaying],
            ephemeral: false
        })


    },
    slashInfo: new SlashCommandBuilder()
        .setName("nowplaying")
        .setDescription("Información de la canción sonando")
}
