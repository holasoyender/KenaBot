/*
 * File:          websocket.ts
 * Path:          v4\api\ws\websocket.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: e63982b3c15a4a046bcc2a06fd7ba24b3056e574f909f2f690a5f597b15af03b
 *
 */


import ws from "ws";
import http from "http";
import { logger } from "../config/logger";
import IWebSocket from "./IWebSocket";
import message_queue from "./message_queue";

//@ts-ignore
export let socket: ws.Server<IWebSocket>

export function init(server: http.Server<typeof http.IncomingMessage, typeof http.ServerResponse>) {

    //@ts-ignore
    socket = new ws.Server({ server });
    logger.ws("Websocket server started")

    socket.on("connection", (ws, request) => {

        const handshake_authorization = request.headers.authorization;
        const ip = request.socket.remoteAddress;

        if (!handshake_authorization || handshake_authorization !== process.env.WS_AUTHORIZATION) {
            logger.error(`Invalid password "${handshake_authorization}" for client ${ip}`)
            ws.close(1003, "Unauthorized");
            return;
        }

        const id = request.headers["client-name"]?.toString()

        if (!id) {
            logger.error(`Invalid client name "${id}" for client ${ip}`)
            ws.close(1003, "Invalid client name");
            return;
        }

        const existing_client = Array.from(socket.clients).find(client => client.id === id)

        if (existing_client) {
            logger.error(`Client ${id} already connected`)
            ws.close(1003, "Client already connected");
            return;
        }

        ws.id = id;
        logger.ws(`Client ${id} connected from ${ip}`)

        ws.on("message", (data: string) => {

            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)

            message_queue.add_to_queue(object.id, object.data)
        })

        ws.on("close", () => {
            logger.ws(`Client ${id} disconnected`)
        })

        //@ts-ignore
        ws.on("error", (e) => {
            logger.error(`Client ${id} error: ${e}`)
        })

    });

}

export const get_socket_by_id = (id: string): IWebSocket | undefined => {

    let client_name: string | undefined;
    switch (id) {
        case "1":
            client_name = "KenaBot";
            break;
        case "2":
            client_name = "KenaBot2";
            break;
        case "3":
            client_name = "KenaBot3";
            break;
        case "4":
            client_name = "KenaBot4";
            break;
        case "5":
            client_name = "KenaDev";
            break;
        default:
            client_name = undefined;
            break;
    }

    if (!client_name)
        return undefined

    return Array.from(socket.clients).find(client => client.id === client_name)
}
