/*
 * File:          bots.ts
 * Path:          v3.5\api\routes\bots.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-07-04 02:00:55 +02:00
 *
 * SHA-256: 2f1a1def12f1fb0e5e734c8fc49a24a908a5e21d72d27b8b3ac7a69653b1653a
 *
 */


import { Router } from "express";
import { get_socket_by_id, socket } from "../ws/websocket";
import { v4 as idv4 } from "uuid"
import delay from "../lib/delay";
import message_queue from "../ws/message_queue";

const router = Router()

router.get("/", async (req, res) => {

    let one = false
    let two = false
    let three = false
    let four = false

    for (const client of socket.clients) {
        if (client.id === "KenaBot")
            one = true
        if (client.id === "KenaBot2")
            two = true
        if (client.id === "KenaBot3")
            three = true
        if (client.id === "KenaBot4")
            four = true
    }
    return res.status(200).send([
        {
            id: 1,
            name: "KenaBot",
            online: one
        },
        {
            id: 2,
            name: "KenaBot 2",
            online: two
        },
        {
            id: 3,
            name: "KenaBot 3",
            online: three
        },
        {
            id: 4,
            name: "KenaBot 4",
            online: four
        }
    ]);
})

router.all("/:id/*", async (req, res) => {

    const random = idv4();
    const id = req.params.id;

    if (!["1", "2", "3", "4", "5"].includes(id))
        return res.status(404).send({
            status: 404,
            message: "Not Found"
        })

    const raw_path = req.url.replace("/" + id + "/", "")
    const path = raw_path.split("/")[0]
    const params = raw_path.split("/").slice(1)
    const body = req.body || ""

    if (!path)
        return res.status(404).send({
            status: 404,
            message: "Not Found"
        })

    const client = get_socket_by_id(id)
    if (!client)
        return res.status(404).send({
            status: 500,
            message: "Bot is offline"
        })

    client.send(JSON.stringify({
        type: "action",
        action: "/" + path,
        method: req.method,
        id: random,
        data: {
            user: req.user ? {
                id: (req.user as any).id,
                username: (req.user as any).username,
                avatar: (req.user as any).avatar,
                guilds: (req.user as any).guilds.map((i: any) => {
                    return {
                        id: i.id,
                        name: i.name,
                        icon: i.icon,
                        owner: i.owner,
                        permissions: i.permissions
                    }
                }) || []
            } : null,
            params,
            body: JSON.stringify(body)
        }
    }))

    for (let i = 0; i < 10; i++) {

        const response = message_queue.get_from_queue_and_delete(random)
        if (response && response.length > 0) {
            if (response[0].data) {
                if (response[0].data.status) {
                    return res.status(response[0].data.status).send(response[0].data)
                } else {
                    return res.status(200).send({
                        status: 200,
                        data: response[0].data
                    })
                }
            } else
                return res.status(200).send({
                    status: 404,
                    message: "Page not found"
                })
        }
        else
            await delay(500);

    }

    return res.status(404).send({
        status: 404,
        message: "Page not found"
    })

})

export default router;