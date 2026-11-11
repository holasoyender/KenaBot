/*
 * File:          WebSocket.d.ts
 * Path:          v2\api\types\WebSocket.d.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-27 16:13:23 +02:00
 *
 * SHA-256: 2604d1216de081de9f745f5771a54ab6c0facd017be85f4336f33d0b3df8142c
 *
 */


import WebSocket from "ws";


export default class IWebSocket extends WebSocket {
    id?: string;
} 