/*
 * File:          IWebSocket.ts
 * Path:          v4\api\ws\IWebSocket.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 224bd9c501dbde2b7c90460cbbba56623f7f006eb4b30d58c38f500d03aa7e63
 *
 */


import WebSocket from "ws";

export default class IWebSocket extends WebSocket {
    id?: string;
}