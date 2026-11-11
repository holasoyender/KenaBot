/*
 * File:          trackStuck.ts
 * Path:          v2\events\lava\trackStuck.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 18:43:50 +01:00
 *
 * SHA-256: 2f3a4bd06d705ed5bbaa34a0cf56c73f2686fa743908b5747fc25139c8f5212b
 *
 */


import Zeppelin from "../../utils/Client";

exports.run = async (client:Zeppelin, player:any) => {

    client.Logger.lava(`Error when loading song! Track is stuck in [${player.guild}]`);

    if(player.metadata)
        player.metadata.shift();

    if (!player.voiceChannel) player.destroy();

}