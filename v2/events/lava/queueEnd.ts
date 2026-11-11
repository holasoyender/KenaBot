/*
 * File:          queueEnd.ts
 * Path:          v2\events\lava\queueEnd.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 2aa99dba8ae619dc3ffa1801159761414d31eea5173597f7b8e70af981507816
 *
 */


import Zeppelin from "../../utils/Client";
import { Message, MessageEmbed } from "discord.js";
import Default from "../../utils/config/default";
import Guild from "../../database/schema/Guild";
import esES from "../../langs/es-ES";
import delay from "delay";

exports.run = async (client: Zeppelin, player: any) => {

    if (player.msg) {
        try {
            const message: Message = player.msg
            await message.edit({ embeds: message.embeds, components: [] })
        } catch (e) {
            return;
        }
    }

    player.metadata = [];

    const channel: any = client.channels.cache.get(player.textChannel);

    if (channel) {

        const db = await Guild.findOne({
            guildID: channel.guild.id
        })

        let guildConfig:any = db
        if (!db) guildConfig = Default;

        if (!guildConfig.lang) guildConfig.lang = "es-ES";

        const guildLang = client.langs?.[guildConfig.lang] ?? esES;

        const embed = new MessageEmbed()
            // @ts-ignore
            .setColor(guildConfig.color)
            .setDescription(guildLang.manager.queue_end)
        try {
            channel.send({ embeds: [embed] }).then((sent: Message) => {
                setTimeout(function () {
                    sent.delete()
                }, 5000);
            }).catch(() => { false });
        } catch (e) {
            return;
        }
    }

    if (!player.playing) {
        if (player.queue.length <= 0) {
            await delay(180000)
            const newPlayer = client.manager?.get(player.guild);
            if (newPlayer && !newPlayer.playing) {
                player.destroy();
                newPlayer.destroy();
            }
        }
    }
}