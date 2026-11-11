/*
 * File:          shardReady.ts
 * Path:          v2\events\shardReady.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-18 13:52:59 +02:00
 *
 * SHA-256: 57d64812fbe8f53cc974416e7c7d65acca9d6eab597a90d93d4baee2fdae2787
 *
 */


import Zeppelin from "../utils/Client";
import {setLastState} from "../utils/State";
import {MessageEmbed} from "discord.js";

exports.run = async (client:Zeppelin, id:number) => {

    client.Logger.shard(`Shard ${id+1} ready.`)

    const embed = new MessageEmbed()
        .setColor(0x6600F4)
        .setAuthor({ name: `Shard ${id+1} lanzada`, iconURL: `${client.user?.avatarURL()}` })
        .setFooter({ text: `Bot: ${client.user?.username}`})
        .setTimestamp()
    if(client.user?.username !== "KenaBot Beta")
        await client.webhook.send({embeds: [embed]})

    try {
        await client.user?.setPresence({
            status: 'online',
            activities: [
                {
                    name: "kenabot.xyz",
                    type: "WATCHING"
                }
            ],
            shardId: id
        });
    }catch(e){return;}

    setLastState("shardReady")

};