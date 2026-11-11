/*
 * File:          memoryLeak.ts
 * Path:          v2\events\memoryLeak.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-28 13:03:25 +02:00
 *
 * SHA-256: fda9f6b03558360083f098b63d3cc17d1049b2953273ddc1b2382be213a2c614
 *
 */


import Zeppelin from "../utils/Client";
import { MessageEmbed } from "discord.js";
import { getLastState } from "../utils/State";

exports.run = async (client: Zeppelin, memory: number) => {
    if (!client.manager?.players || client.manager.players.size == 0) return;

    const embed = new MessageEmbed()
        .setColor(0xFEDA13)
        .setAuthor({ name: `Posible error de memoria detectado`, iconURL: `${client.user?.avatarURL()}` })
        .setFooter({ text: `Bot: ${client.user?.username} | Último estado: ${getLastState()}` })
        .setTimestamp()
    await client.webhook.send({ embeds: [embed] })
    client.Logger.error(`Se ha detectado un error en la memoria (${memory}MB), reiniciando el bot...`);
    //client.restart(pos);
}