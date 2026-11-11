/*
 * File:          shardDisconnect.ts
 * Path:          v2\events\shardDisconnect.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-03 17:47:03 +01:00
 *
 * SHA-256: 62636a5b9bbd33dcdb00c182c646b3ba5bfd69bf9a3866b470188d99e488c94a
 *
 */


import Zeppelin from "../utils/Client";
import {getLastState, setLastState} from "../utils/State";
import {MessageEmbed} from "discord.js";

exports.run = async (client:Zeppelin, event:any, id:number) => {

    const embed = new MessageEmbed()
        .setColor(0xF4433F)
        .setAuthor({ name: `Shard ${id+1} desconectada`, iconURL: `${client.user?.avatarURL()}` })
        .setFooter({ text: `Bot: ${client.user?.username} | Último estado: ${getLastState()}` })
        .setTimestamp()
    await client.webhook.send({embeds: [embed]})

    client.Logger.shard(`Shard ${id+1} desconectada. Evento: ${event}`)
    setLastState("shardDisconnect")
};