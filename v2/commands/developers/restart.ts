/*
 * File:          restart.ts
 * Path:          v2\commands\developers\restart.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-21 22:24:27 +01:00
 *
 * SHA-256: d5f543c7233128ee0f2fb15060c410f7c39921546d2e3932c14379846c9f97ca
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";

module.exports = {
    name: "restart",
    desc: "Avisar sobre un renicio del bot",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const embed = new MessageEmbed()
            .setAuthor({ name: 'Reinicio del bot', iconURL: client.user?.displayAvatarURL()})
            .setColor("#5865F2")
            .setDescription("Haciendo click al botón el bot mandará un mensaje a todos los servidores que están reproduciendo una canción diciendo que se reiniciará el bot")

        const row = new MessageActionRow()
            .addComponents([
                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel("Reiniciar")
                    .setCustomId("cmd:dev:restart:"+message.author.id),
                new MessageButton()
                    .setStyle("DANGER")
                    .setLabel("Cancelar")
                    .setCustomId("cmd:dev:cancel:"+message.author.id)
            ])




        await message.reply({
            embeds:[embed],
            allowedMentions: {repliedUser: false},
            components: [row]
        })

    },
}
