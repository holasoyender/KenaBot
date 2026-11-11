/*
 * File:          nodeError.ts
 * Path:          v2\events\lava\nodeError.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-11 19:06:56 +01:00
 *
 * SHA-256: 5e8be38b93390d6a34a459490283a755226505687da84413e6a5a00964ac786c
 *
 */


import Zeppelin from "../../utils/Client";
exports.run = async (client:Zeppelin, node:any) => {
    client.Logger.lava(`Nodo "${node.options.identifier}" error.`);
}