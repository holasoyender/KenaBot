/*
 * File:          nodeReconnect.ts
 * Path:          v2\events\lava\nodeReconnect.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-11 19:06:56 +01:00
 *
 * SHA-256: 52ea4678ecfa6c313a6ba564c1c58ee2b2ea10f6919f02280ea573c976cc7838
 *
 */


import Zeppelin from "../../utils/Client";
exports.run = async (client:Zeppelin, node:any) => {
    client.Logger.lava(`Nodo "${node.options.identifier}" reconectado.`);
}