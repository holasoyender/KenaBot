/*
 * File:          check247.ts
 * Path:          v2\utils\checks\check247.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-02 11:11:00 +02:00
 *
 * SHA-256: b8d37f6a99a40abb281ee6047965d6f99cd69353269b2e57ac13cbcf931823e0
 *
 */


import Zeppelin from "../Client";
import Default from "../config/default";
import Guild from "../../database/schema/Guild";

export default async (client: Zeppelin) => {
    const allGuilds: Array<typeof Default> = await Guild.find();

    for (const g of allGuilds) {
        if (g.premium.enabled) {
            if (g._247.enabled) {
                if (g._247.channelID) {
                    try {
                        const guild = client.guilds.cache.get(g.guildID);
                        if (guild) {
                            const channel = guild.channels.cache.get(g._247.channelID)
                            if (channel) {
                                let player = client.manager?.get(guild.id)
                                if (!player) {
                                    player = client.manager?.create({
                                        guild: guild.id,
                                        voiceChannel: channel.id,
                                        textChannel: guild.channels.cache.filter(c => c.type === "GUILD_TEXT").first()?.id || "",
                                        volume: 50,
                                        selfDeafen: true,
                                    });

                                    if (player?.state !== "CONNECTED")
                                        player?.connect();
                                } else {
                                    // @ts-ignore
                                    player.voiceChannel = channel;
                                    player.setVoiceChannel(channel.id);
                                }
                            }
                        }
                    } catch (e) {
                        //lol
                    }
                }

            }
        }
    }
}