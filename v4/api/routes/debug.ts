/*
 * File:          debug.ts
 * Path:          v4\api\routes\debug.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 060ea07222e3a6ddb4002823613830b23cab40d642b6fe52efb5fa0eea0d92ec
 *
 */


import {Router} from "express";
import message_queue from "../ws/message_queue";
import {socket} from "../ws/websocket";

const router = Router()

router.get("/", async (req, res) => {

    return res.status(200).send({
        message_queue_size: message_queue.get_size(),
        connected_clients: socket.clients.size,
        your_ip: req.ip,
        uptime: process.uptime(),
    })

})

export default router;