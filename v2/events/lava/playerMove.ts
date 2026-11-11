/*
 * File:          playerMove.ts
 * Path:          v2\events\lava\playerMove.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 22:32:37 +02:00
 *
 * SHA-256: 4da5ca2eb0c9d2955999facb53b8732440b1c173bd229bee88488c3d9172d23c
 *
 */


import Zeppelin from "../../utils/Client";
import { Player } from "../../functions/manager";
import { VoiceChannel } from "discord.js";
exports.run = async (client: Zeppelin, player: Player, oldChannel: VoiceChannel, newChannel: VoiceChannel) => {
    const guild = client.guilds.cache.get(player.guild)
    if (!guild) return;
    if (oldChannel === newChannel) return;
    if (newChannel === null || !newChannel) {
        return player.destroy();
    } else {
        // @ts-ignore
        player.voiceChannel = newChannel;
        if (player.paused) player.pause(false);
    }
}