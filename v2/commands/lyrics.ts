/*
 * File:          lyrics.ts
 * Path:          v2\commands\lyrics.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 01:14:19 +02:00
 *
 * SHA-256: 1e2704160949804e9e77b59582936615f4b6b66344fd25cb0cbb4bf89117d837
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember, MessageEmbed } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import { getSong } from "../functions/GeniusAPI";
import { getTrackByURL } from "../plugins/spotify/api/api";

module.exports = {
    name: "lyrics",
    desc: "Mostrar los lyrics de una canción",
    category: "Music",
    permissions: "everyone",
    usage: "<Canción>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const opt = event.options.getString("canción");
        let options;

        if (opt) {
            options = {
                apiKey: process.env.GENIUS_API,
                title: opt,
                artist: '‎',
                optimizeQuery: true,
            }
        } else {
            const player: any = client.manager?.players.get(event.guild.id);
            if (!player) return event.reply({
                content: lang.commands.play.no_song,
                ephemeral: true
            });
            if (!player.queue.current) return event.reply({
                content: lang.commands.play.no_song,
                ephemeral: true
            });

            let spInfo
            if (player.metadata)
                spInfo = await getTrackByURL(player.metadata[0])

            const title = spInfo?.name ?? player.queue.current.title
            const author = spInfo?.artists[0].name ?? player.queue.current.author

            options = {
                apiKey: process.env.GENIUS_API,
                title: `${title} ${author}`,
                artist: '‎',
                optimizeQuery: true,
            }
        }

        await event.deferReply({ ephemeral: false });

        try {
            const info = await getSong(options);

            if (!info || !info.lyrics) return;
            const embed = new MessageEmbed()
                //@ts-ignore
                .setColor(config.color)
                .setTitle(info.title)
                .setURL(info.url)
                .setThumbnail(info.albumArt)
                .setDescription("```" + info.lyrics + "```")

            if (!embed.description || embed.description.length >= 2048)
                embed.description = `\`\`\`${embed.description?.substr(0, 2045)}...\`\`\``;
            return event.editReply({ embeds: [embed] })

        } catch (e) {
            console.log(e);
            event.channel.send({
                content: lang.commands.lyrics.error.replace("%s%", event.user.id),
            });
        }

    },
    slashInfo: new SlashCommandBuilder()
        .setName("lyrics")
        .setDescription("Mostrar los lyrics de una canción")
        .addStringOption(option =>
            option.setName('canción')
                .setDescription('Canción a buscar los lyrics.')
                .setRequired(false))
}
