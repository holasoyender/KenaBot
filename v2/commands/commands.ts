/*
 * File:          commands.ts
 * Path:          v2\commands\commands.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-18 21:57:20 +01:00
 *
 * SHA-256: 6957f1024adaa5990750708fd5d542257620206da1b911fa182e91bebdc3c657
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, MessageActionRow, MessageButton, MessageEmbed } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";

module.exports = {
    name: "commands",
    desc: "Lista de comandos del bot",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setAuthor({ name: client.user?.username + "", iconURL: client.user?.avatarURL() + "" })
            .setDescription(lang.commands.commands)
        await event.reply({
            embeds: [embed], ephemeral: false, components: [
                new MessageActionRow().addComponents([
                    new MessageButton().setLabel(lang.commands.help.commands.title).setStyle("LINK").setURL("https://kenabot.xyz/commands"),
                ])
            ]
        });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("commands")
        .setDescription("Lista de comandos del bot")
}