/*
 * File:          shardError.ts
 * Path:          v2\events\shardError.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-03 17:47:03 +01:00
 *
 * SHA-256: 71a93c238bc0125672bce3f88d2419fa97c366b18487b4873e70469840d366c8
 *
 */


import Zeppelin from "../utils/Client";
import {getLastState, setLastState} from "../utils/State";
import {MessageEmbed} from "discord.js";

exports.run = async (client:Zeppelin, error:any, id:number) => {

    const embed = new MessageEmbed()
        .setColor(0xF4433F)
        .setAuthor({ name: `Shard ${id+1} error`, iconURL: `${client.user?.avatarURL()}` })
        .setFooter({ text: `Bot: ${client.user?.username} | Último estado: ${getLastState()}` })
        .setTimestamp()
    await client.webhook.send({embeds: [embed]})

    client.Logger.shard(`Shard ${id+1} error. Error: ${error}.`)
    setLastState("shardError")
};