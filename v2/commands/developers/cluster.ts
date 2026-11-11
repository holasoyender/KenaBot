/*
 * File:          cluster.ts
 * Path:          v2\commands\developers\cluster.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-15 23:07:01 +02:00
 *
 * SHA-256: b802c2835efcbf5fb2f02b53edfadaab7b93d62ddb8b552082e86a9dd1a0c3a2
 *
 */


import Zeppelin from "../../utils/Client";
import { Message, MessageEmbed, WebSocketManager } from "discord.js";
import Cluster from "discord-hybrid-sharding";

module.exports = {
    name: "cluster",
    desc: "Cluster information",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        //if (!client.cluster)
        return message.channel.send("Not running on cluster mode!");

        /*const embed = new MessageEmbed()
            .setAuthor({ name: "Cluster Information", iconURL: `${client.user?.avatarURL()}` })
            .setColor(0x2f3136)
            .setDescription(`Server running on cluster mode (${Cluster.data.CLUSTER_COUNT} clusters) with ${Cluster.data.TOTAL_SHARDS} shards.`)

        let i = 0;

        const all_clusters = [];
        client.cluster.evalOnCluster("this.cluster.ids", { cluster: 0 })
        client.cluster.broadcastEval(c => c.cluster)//"client.cluster.ids")
            .then(results => {
                all_clusters.push([results])
                console.log(all_clusters);
            })

        client.cluster.broadcastEval("cluster", { cluster: 0 })

        //TODO: esto kekw



        for (const cluster of client.cluster.ids) {

            // @ts-expect-error: cluster.id is a number
            const clusterData: WebSocketManager = client.cluster.ids.get(cluster[0]);
            console.log(clusterData);

            let emoji = "<:online:983837897536987146>"

            switch (clusterData.status) {
                case 0:
                    emoji = "<:online:983837897536987146>"
                    break;
                case 1:
                case 2:
                case 3:
                case 4:
                    emoji = "<:idle:983837897536987147>"
                    break;
                case 5:
                case 7:
                case 8:
                    emoji = "<:dnd:983837897536987148>"
                    break;
                default:
                    emoji = "<:dnd:983837897536987148>"
                    break;
            }
            embed.addField(`${emoji} Cluster ${i}`, `\`\`\`Ping: ${clusterData.ping}  \`\`\``, true);
            i++
        }

        message.channel.send({ embeds: [embed] })

        message.channel.send("RustApi panicked at 'asserion failed: (0)', this is a bug in the library. The incident was automatically posted to https://api.kenabot.xyz/panic.");
*/
    },
}
