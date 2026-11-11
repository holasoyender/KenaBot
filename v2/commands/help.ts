/*
 * File:          help.ts
 * Path:          v2\commands\help.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 16:44:29 +01:00
 *
 * SHA-256: 8a095e100ba3a08ea447d84c5b2603a246ce69d0ace6f88f6d06922ed491b054
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";

module.exports = {
    name: "help",
    desc: "Información útil sobre el bot",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setAuthor({name: client.user?.username+"", iconURL: client.user?.avatarURL()+""})
            .setDescription(lang.commands.help.desc.replace("%s%", client.user?.username+"").replace("%i%", client.user?.id+""))
        await event.reply({ embeds: [embed], ephemeral: false, components: [
            new MessageActionRow().addComponents([
                new MessageButton().setLabel(lang.commands.help.commands.title).setStyle("LINK").setURL("https://kenabot.xyz/commands"),
                new MessageButton().setLabel(lang.commands.help.add.title).setStyle("LINK").setURL(`https://discord.com/api/oauth2/authorize?client_id=${client.user?.id}&permissions=3534272&scope=bot%20applications.commands`),
                new MessageButton().setLabel(lang.commands.help.support.title).setStyle("LINK").setURL("https://kenabot.xyz/support")
            ])
            ] });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("help")
        .setDescription("Información útil sobre el bot")
}