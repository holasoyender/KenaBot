/*
 * File:          nodes.ts
 * Path:          v2\commands\developers\nodes.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-16 15:03:58 +01:00
 *
 * SHA-256: ec287710aba118e5bc9501734851a1cebabb6ad684293e1b9bd4f1d069de120a
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageEmbed} from "discord.js";

module.exports = {
    name: "nodes",
    desc: "Get the current nodes in the cluster.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const embed = new MessageEmbed()
            .setAuthor({ name: 'Cluster Nodes', iconURL: client.user?.displayAvatarURL()})
            .setColor("#5865F2")

        client.manager?.nodes.forEach((node: any) => {
            const all = `\`\`\`\nReproductor: ${node.stats.players}\nReproductores activos: ${node.stats.playingPlayers}\nUptime: ${new Date(node.stats.uptime).toISOString().slice(11, 19)}\n\nMemoria\nMemoria reservada: ${Math.round(node.stats.memory.reservable / 1024 / 1024)}mb\nMemoria usada: ${Math.round(node.stats.memory.used / 1024 / 1024)}mb\`\`\``;
            embed.addField(node.options.identifier, all, true)
        })

        await message.reply({embeds: [embed], allowedMentions: {repliedUser: false}})
    },
}
