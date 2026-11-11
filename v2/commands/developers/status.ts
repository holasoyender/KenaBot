/*
 * File:          status.ts
 * Path:          v2\commands\developers\status.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-04-28 16:21:48 +02:00
 *
 * SHA-256: f118fabfe079bf0195e688d80e90b95ff343d0d7f7dca033cf261daac9482f70
 *
 */


import Zeppelin from "../../utils/Client";
import { Formatters, Message, MessageEmbed } from "discord.js";
import { getLastState } from "../../utils/State";

module.exports = {
    name: "status",
    desc: "Estado del bot.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const embed = new MessageEmbed()
            .setAuthor({ name: 'Estado actual del bot', iconURL: client.user?.displayAvatarURL() })
            .setColor("#5865F2")
            .setThumbnail(`${client.user?.displayAvatarURL()}`)

        const userArray: Array<any> = []
        client.guilds.cache.map(g => userArray.push(g.memberCount))
        let suma = 0;
        userArray.forEach(function (numero) {
            suma += numero;
        });

        const s = `
        **Encendido en:** ${Formatters.time((client.readyAt as Date), "D")} (${Formatters.time((client.readyAt as Date), "R")})
        **Usuarios totales:** ${suma}
        **Servidores:** ${client.guilds.cache.size}
        **Ultimo estado:** \`${getLastState()}\`
        `

        embed.setDescription(s)

        await message.reply({
            embeds: [embed],
            allowedMentions: { repliedUser: false }
        })

    },
}
