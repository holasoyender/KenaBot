/*
 * File:          config.ts
 * Path:          v2\commands\config.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-09 17:43:12 +02:00
 *
 * SHA-256: b99a34fcce91bb79e68bc523543d3afac50c20d42370905c2b0b2f7ad58574b8
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {inArray} from "../utils/chunk";

module.exports = {
    name: "config",
    desc: "Menú de configuración del bot",
    category: "Bot",
    permissions: "Gestionar Servidor",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {

        // @ts-ignore
        if (!event.member.permissions.has("MANAGE_GUILD")) {
            if(!inArray(client.develop.owners, event.user.id))
            return event.reply({
                content: lang.manager.no_perms,
                ephemeral: true
            });
        }

        const embed = new MessageEmbed()
            // @ts-ignore
            .setColor(config.color)
            .setTitle(lang.commands.config.title)
            .setDescription(lang.commands.config.description.replace("%s%", `${client.user?.id}`))

        const langButton = new MessageButton()
            .setStyle("SECONDARY")
            .setLabel(lang.commands.config.buttons.lang)
            .setEmoji(lang.flag)
            .setCustomId("cmd:config:lang:"+event.user.id)

        const channelsButton = new MessageButton()
            .setStyle("SECONDARY")
            .setLabel(lang.commands.config.buttons.channels)
            .setEmoji("<:channels:940254447530016859>")
            .setCustomId("cmd:config:channels:"+event.user.id)

        const djButton = new MessageButton()
            .setStyle("SECONDARY")
            .setLabel(lang.commands.config.buttons.dj)
            .setEmoji("<:mention:940268589615812608>")
            .setCustomId("cmd:config:dj:"+event.user.id)

        const commandsButton = new MessageButton()
            .setStyle("SECONDARY")
            .setLabel(lang.commands.config.buttons.commands)
            .setEmoji("<:slash:941024012270710874>")
            .setCustomId("cmd:config:commands:"+event.user.id)

        const premiumButton = new MessageButton()
            .setStyle(config.premium.enabled ? "PRIMARY" : "DANGER")
            .setLabel(config.premium.enabled ? lang.commands.config.buttons.premium_on : lang.commands.config.buttons.premium_off)
            .setCustomId("cmd:config:premium:"+event.user.id)

        const moreButton = new MessageButton()
            .setStyle("SECONDARY")
            .setLabel(lang.commands.config.buttons.more)
            .setCustomId("cmd:config:more:"+event.user.id)

        await event.reply({ embeds: [embed], ephemeral: false, components: [new MessageActionRow()
                .addComponents([
                    langButton,
                    channelsButton,
                    djButton,

                    moreButton
                ]),
            new MessageActionRow()
                .addComponents([
                    commandsButton,
                    premiumButton
                ])] });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("config")
        .setDescription("Menú de configuración del bot")
}
