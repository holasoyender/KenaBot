/*
 * File:          ping.ts
 * Path:          v2\commands\ping.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-18 20:30:16 +01:00
 *
 * SHA-256: db4fc2fa41412570ef7474e4d037b2c673dbda74320993d5e9492a7de759a5ac
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction } from "discord.js";

module.exports = {
    name: "ping",
    desc: "Mostrar el ping del bot",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction) => {
        await event.reply({ content: `:ping_pong:  Pong! \`${Math.round(client.ws.ping)}ms\``, ephemeral: false });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("ping")
        .setDescription("Mostrar el ping del bot")
}
