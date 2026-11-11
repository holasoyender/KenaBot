/*
 * File:          nodeConnect.ts
 * Path:          v2\events\lava\nodeConnect.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-05 17:15:02 +01:00
 *
 * SHA-256: a9d5734ada084346b53125172cbdb6f9016c963d1f07e0d49f475ebcf0a4b9df
 *
 */


import Zeppelin from "../../utils/Client";
import {getLastState} from "../../utils/State";
import check247 from "../../utils/checks/check247";
exports.run = async (client:Zeppelin, node:any) => {
    client.Logger.lava(`Nodo "${node.options.identifier}" conectado.`);
    if(getLastState() == "ready")
        await check247(client);
}