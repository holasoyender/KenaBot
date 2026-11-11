/*
 * File:          trackEnd.ts
 * Path:          v2\events\lava\trackEnd.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:46:00 +01:00
 *
 * SHA-256: 8950223bf0deaa334107aa4be1d48480f8cc1faebda20f298ef508f6ed7bcf97
 *
 */


import Zeppelin from "../../utils/Client";

exports.run = async (client:Zeppelin, player:any) => {

    if(player.metadata) {
        if(!player.trackRepeat)
            player.metadata.shift();
    }
}