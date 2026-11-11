/*
 * File:          socket.ts
 * Path:          v2\api\utils\socket.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 12:59:35 +02:00
 *
 * SHA-256: 3f1ed800e12cee0f2e48b5bed05ac4b63454d76f378700ad1a65bbe2a97c45d3
 *
 */


import wss from "../main";
import IWebSocket from "../types/WebSocket";

export const getSocketByID = (id: string) => {

    let Kena1: typeof IWebSocket | any;
    let Kena2: typeof IWebSocket | any ;
    let Kena3: typeof IWebSocket | any ;
    let Kena4: typeof IWebSocket | any;

    let KenaBeta: typeof IWebSocket | any;
    for (const _client of wss.clients) {
        const client: IWebSocket = _client
        if (client.id == "KenaBot")
            Kena1 = client
        if (client.id == "KenaBot2")
            Kena2 = client
        if (client.id == "KenaBot3")
            Kena3 = client
        if (client.id == "KenaBot4")
            Kena4 = client
        if (client.id == "KenaBotBeta")
            KenaBeta = client
    }

    switch (id) {
        case "1":
            return Kena1
        case "2":
            return Kena2
        case "3":
            return Kena3
        case "4":
            return Kena4
        case "5":
            return KenaBeta
        default:
            return null
    }
}