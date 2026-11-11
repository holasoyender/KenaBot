/*
 * File:          invite.ts
 * Path:          v2\commands\invite.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: 683a14685aaf13b02a59952b49f9609df44e97d30d1420d59ede07082b19cee7
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, MessageActionRow, MessageButton} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import emojis from "../utils/emojis";

module.exports = {
    name: "invite",
    desc: "Mandar una invitación para añadirme a tu servidor",
    category: "Bot",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        await event.reply({ content: lang.commands.invite, ephemeral: true, components: [ new MessageActionRow().addComponents([
            new MessageButton()
                .setURL(`https://discord.com/api/oauth2/authorize?client_id=${client.user?.id}&permissions=3534272&scope=bot%20applications.commands`)
                .setLabel(lang.commands.help.add.add)
                .setStyle("LINK")
                .setEmoji(emojis.add.replace("  ", ""))
            ])]
        });
    },
    slashInfo: new SlashCommandBuilder()
        .setName("invite")
        .setDescription("Mandar una invitación para añadirme a tu servidor")
}
