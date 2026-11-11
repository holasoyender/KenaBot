/*
 * File:          stats.ts
 * Path:          v3\api\routes\stats.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 20:06:00 +02:00
 *
 * SHA-256: ad6cf1339d9a280ab0979ca836ef23cb85e59b0d995283d93970e08b969f85d0
 *
 */


import {Router} from "express";
import stats_cache from "../lib/stats_cache";
import {socket} from "../ws/websocket";
import {commands_schema} from "../database/schema/commands";
import {voice_schema} from "../database/schema/voice";
import {v4 as idv4} from "uuid"
import delay from "../lib/delay";
import message_queue from "../ws/message_queue";

const router = Router()

const allowed_clients = ["KenaBot", "KenaBot2", "KenaBot3", "KenaBot4", "KenaDev"]
router.get("/", async (req, res) => {

    const data = stats_cache.get_cache()

    if (data.renew_at < Date.now()) {

        const all_commands = await commands_schema.find({})
        const total_uses = all_commands.reduce((a: any, b: any) => a + b.uses, 0)

        const all_voice_joins = await voice_schema.find()
        const total_joins = all_voice_joins.reduce((a: any, b: any) => a + b.joins, 0)

        const id = idv4()

        for (const client of socket.clients) {

            const client_id = client.id
            if (!client_id)
                continue

            if (!allowed_clients.includes(client_id))
                continue

            await client.send(JSON.stringify({
                type: "action",
                action: "/stats",
                method: "GET",
                id
            }))

        }

        await delay(5000);

        const all_data = message_queue.get_from_queue_and_delete(id)

        const main_document = all_data.reduce((a, b) => a.data.guilds > b.data.guilds ? a : b)
        const guild_count = all_data.reduce((a, b) => a + b.data.guilds, 0)
        const all_users = all_data.reduce((a, b) => a + b.data.users, 0)

        const stats = {
            command_uses: total_uses,
            voice_joins: total_joins,
            guilds: guild_count,
            users: all_users,
            servers: main_document.data.servers || [],
            renew_at: Date.now() + 1000 * 60 * 60 * 24,
            renewed: true
        }

        stats_cache.set_cache(stats)
        res.send(stats)
    } else
        res.send(data)

})

export default router;