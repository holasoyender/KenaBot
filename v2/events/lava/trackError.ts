/*
 * File:          trackError.ts
 * Path:          v2\events\lava\trackError.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 21:27:03 +02:00
 *
 * SHA-256: befcc1e67b5d6c6868c595700ba9921f60c3eb063a466b3416456764f3eb8875
 *
 */


import Zeppelin from "../../utils/Client";

exports.run = async (client: Zeppelin, player: any) => {

    client.Logger.lava(`Error when loading song! Track is error in [${player.guild}]`);
    if (player.metadata)
        player.metadata.shift();
    if (!player.voiceChannel) player.destroy();

}