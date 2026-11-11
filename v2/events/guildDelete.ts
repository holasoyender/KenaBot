/*
 * File:          guildDelete.ts
 * Path:          v2\events\guildDelete.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-04-28 16:21:48 +02:00
 *
 * SHA-256: 726cb9c5747ed839719cff186d7a80c03209d40b4b41dcad1d6340bae10231b6
 *
 */


import Zeppelin from "../utils/Client";
import { Guild, MessageEmbed } from "discord.js";

exports.run = async (client: Zeppelin, guild: Guild) => {

    const userArray: Array<any> = []
    client.guilds.cache.map(g => userArray.push(g.memberCount))
    let suma = 0;
    userArray.forEach(function (numero) {
        suma += numero;
    });

    const logEmbed = new MessageEmbed()
        .setColor(0xFA753B)
        .setAuthor({ name: `Me han echado de un servidor`, iconURL: `${guild.iconURL() ?? "https://cdn.discordapp.com/attachments/755000173922615336/953028314593919006/emoji.png"}` })
        .addField(`Nombre del servidor`, `${guild.name} (\`${guild.id}\`)`, true)
        .addField(`Miembros`, `${guild.memberCount}`, true)
        .addField("Propietario", `\`${guild.ownerId}\``, true)
        .addField("Servidores totales", `${client.guilds.cache.size} (\`${suma}\` usuarios totales)`, true)
        .addField("Bot", `${client.user?.tag} (\`${client.user?.id}\`)`, true)
    await client.webhook.send({ embeds: [logEmbed] })
}