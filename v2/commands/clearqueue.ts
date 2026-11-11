/*
 * File:          clearqueue.ts
 * Path:          v2\commands\clearqueue.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: 9f5f696eae5083266c6f675e207a4e0effde27cfd7b9d3c3a4cf4eaa06121b68
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";

module.exports = {
    name: "clearqueue",
    desc: "Limpiar la lista de reproducción",
    category: "Music",
    permissions: "everyone",
    usage: "",
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

        player.queue.clear();
        player.metadata = [];

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.clear_queue)

        return event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("clearqueue")
        .setDescription("Limpiar la lista de reproducción")
}
