/*
 * File:          nodeDisconnect.ts
 * Path:          v2\events\lava\nodeDisconnect.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-11 19:06:56 +01:00
 *
 * SHA-256: 3ad40dd78c78a7a8d3b962daea9ff8829d35a588a20a1af14020d0bbadf48d06
 *
 */


import Zeppelin from "../../utils/Client";
exports.run = async (client:Zeppelin, node:any) => {
    client.Logger.lava(`Nodo "${node.options.identifier}" desconectado.`);
}