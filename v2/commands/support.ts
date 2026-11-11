/*
 * File:          support.ts
 * Path:          v2\commands\support.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: acd94738dc8c9cae3407e08665389131e841f93add4da677dff81e9aa105c8f0
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, MessageActionRow, MessageButton} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import emojis from "../utils/emojis";

module.exports = {
    name: "support",
    desc: "Mandar una invitación al servidor de soporte del bot",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        await event.reply({ content: lang.commands.support, ephemeral: true, components: [ new MessageActionRow().addComponents([
                new MessageButton()
                    .setURL(`https://kenabot.xyz/support`)
                    .setLabel(lang.commands.help.support.support)
                    .setStyle("LINK")
                    .setEmoji(emojis.gear.replace("  ", ""))
            ])]
        });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("support")
        .setDescription("Mandar una invitación al servidor de soporte del bot")
}
