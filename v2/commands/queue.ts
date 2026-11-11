/*
 * File:          queue.ts
 * Path:          v2\commands\queue.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: b9168e42c35010dbaa8747f41d7801e1199fe9687997d914d882a3334bab17eb
 *
 */


import Zeppelin from "../utils/Client";
import esES from "../langs/es-ES";
import Default from "../utils/config/default";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";
import chunk from "../utils/chunk";
import {stringFormat} from "../utils/stringFormat";
import {convertTime} from "../utils/convert";

module.exports = {
    name: "queue",
    desc: "Mostrar la lista de reproducción",
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

        const queue = player.queue;

        if (!queue) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (!queue.current) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        /*
        * Inspirado en el código fuente de holasoyender/Libra y toshR1/ChunkArrayCache
        * Contribuyentes: holasoyender, MicroondasV2, toshR1, jagrosh, sedmelluq, DV8FromTheWorld, SplitPixl
        * */

        let chunkQueue: Array<typeof queue> = chunk(queue, 5)

        if(queue.length == 0) chunkQueue = [queue]
        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setFooter({ text: lang.commands.queue.embed_footer.replace("%s%", "1").replace("%d%", chunkQueue.length.toString()).replace("%f%", (queue.length+1)+"") })
            .setAuthor({ name: lang.commands.queue.embed_author, iconURL: client.user?.displayAvatarURL() })

        if(queue.current) {
            embed.setDescription(`\`\`\`yml\n --- ${lang.commands.queue.now_playing} ---\n${stringFormat(queue.current.title)}\`\`\``)
        }
        let i = 0;
        for (const track of chunkQueue[0]) {
            i++;
            embed.addField(`${i}. ${stringFormat(track.title)}`, `**Autor:** ${track.author}\n**Duración**: ${convertTime(track.duration ?? 0)}`)
        }

        const row = new MessageActionRow().addComponents([
            new MessageButton()
                .setStyle("PRIMARY")
                .setLabel("◀")
                .setCustomId("cmd:queue:1:back:"+event.member.id),
            new MessageButton()
                .setStyle("PRIMARY")
                .setLabel("▶")
                .setCustomId("cmd:queue:1:next:"+event.member.id),
        ])

        return event.reply({ embeds: [embed], ephemeral: false, components: [ row ] })


    },
    slashInfo: new SlashCommandBuilder()
        .setName("queue")
        .setDescription("Mostrar la lista de reproducción")
}