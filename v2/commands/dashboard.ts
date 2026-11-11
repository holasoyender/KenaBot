/*
 * File:          dashboard.ts
 * Path:          v2\commands\dashboard.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-14 15:29:21 +01:00
 *
 * SHA-256: b6e804d0c6cdf08eb5eaa5ebb465a5fa6e58bc6f4173ed2d92c09066193a9a87
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction} from "discord.js";

module.exports = {
    name: "dashboard",
    desc: "Mandar un link a la dashboard de KenaBot",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction/*, config: typeof Default, lang: typeof esES*/) => {
        /*await event.reply({ content: lang.commands.dashboard, ephemeral: true, components: [ new MessageActionRow().addComponents([
                new MessageButton()
                    .setURL(`https://dash.kenabot.xyz/${event.guild?.id}`)
                    .setLabel("Dashboard")
                    .setStyle("LINK")
                    .setEmoji(emojis.dash.replace("  ", ""))
            ])]
        });*/
        await event.reply({content: "La dashboard se encuentra actualmente en desarrollo, para configurarme puedes usar el comando `/config`!", ephemeral: true});
    },
    slashInfo: new SlashCommandBuilder()
        .setName("dashboard")
        .setDescription("Mandar un link a la dashboard de KenaBot")
}
