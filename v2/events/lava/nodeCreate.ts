/*
 * File:          nodeCreate.ts
 * Path:          v2\events\lava\nodeCreate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-11 19:06:56 +01:00
 *
 * SHA-256: e7f271c0fd019dd30844608cc4da5679559520737485454129bc71fcd83f26e1
 *
 */


import Zeppelin from "../../utils/Client";
exports.run = async (client:Zeppelin, node:any) => {
    client.Logger.lava(`Nodo "${node.options.identifier}" creado.`);
}