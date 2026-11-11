/*
 * File:          playerDestroy.ts
 * Path:          v2\events\lava\playerDestroy.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-14 15:29:21 +01:00
 *
 * SHA-256: 44f03e85c6ea768f0ae233dc8394fafc93594c28b1397f0b5f213b0e973e143e
 *
 */


import Zeppelin from "../../utils/Client";
import {Message} from "discord.js";
exports.run = async (client:Zeppelin, player:any) => {

    if(player.msg) {
        try {
            const message: Message = player.msg
            await message.edit({embeds: message.embeds, components: []})
        } catch (e) {
            return;
        }
    }
}