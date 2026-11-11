/*
 * File:          socketClosed.ts
 * Path:          v2\events\lava\socketClosed.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-21 21:14:55 +01:00
 *
 * SHA-256: dbd027d7ba6fc773a6e632a340bc0d4ba620f78df277ab52c3674af4ae53131d
 *
 */


import Zeppelin from "../../utils/Client";

exports.run = async (client:Zeppelin, player:any) => {

    try {
        await player.pause(false);
    } catch (e) {
        return ;
    }

}