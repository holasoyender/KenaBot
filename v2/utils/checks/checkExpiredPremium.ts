/*
 * File:          checkExpiredPremium.ts
 * Path:          v2\utils\checks\checkExpiredPremium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 17:03:01 +01:00
 *
 * SHA-256: 170f7483693092277e7e41de3d56f57b10c78ab2dbcc10b241d1a5c0a289ef06
 *
 */


import Default from "../config/default";
import Guild from "../../database/schema/Guild";
import {Logger} from "../Logger";
import {MessageEmbed} from "discord.js";
import Zeppelin from "../Client";

export default async (client: Zeppelin) => {
    const allGuilds: Array<typeof Default> = await Guild.find();

    for (const g of allGuilds) {
        if (g.premium.enabled) {
            // @ts-ignore
            if (g.premium.expires === "Forever") continue;
            if (g.premium.expires <= Date.now()) {
                await Guild.findOneAndUpdate({guildID: g.guildID}, {
                    $set: {
                        premium: {
                            enabled: false,
                            expires: ""
                        }
                    }
                });

                const guild = client.guilds.cache.get(g.guildID);
                if (guild) {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Premium expirado en un servidor`,
                            iconURL: `${guild.iconURL()}`
                        })
                        .setDescription("Se ha desactivado el premium en el servidor **" + guild.name + "** porque ha expirado.")
                    await client.webhook.send({embeds: [embed]})
                } else {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Premium expirado en un servidor`,
                        })
                        .setDescription("Se ha desactivado el premium en el servidor **Desconocido** porque ha expirado.")
                    await client.webhook.send({embeds: [embed]})
                }
                Logger.api(`El premium del servidor ${g.guildID} ha expirado.`);
            }
        }
    }
}