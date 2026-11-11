/*
 * File:          playerCreate.ts
 * Path:          v2\events\lava\playerCreate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 15:13:53 +02:00
 *
 * SHA-256: c9874ab51446368dbf615d3e63440b56c287440803eb80f51dfe0f004318d69d
 *
 */


import Zeppelin from '../../utils/Client';
exports.run = async (client: Zeppelin, player: any) => {
    player.createdAt = new Date(); 
};

