/*
 * File:          index.ts
 * Path:          v2\api\routes\index.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-28 18:44:31 +02:00
 *
 * SHA-256: 873a1d9b7e1314a2d68ec96a00bf21d01b35170fab911377aee3dcd75fbf0153
 *
 */


/* eslint-disable no-case-declarations */
import { Router } from "express";
import wss from "../main";
import IWebSocket from "../types/WebSocket";
import delay from "delay";
import { v4 } from 'uuid';
import commands from "../database/schema/Commands";
import Voice from "../database/schema/Voice";
import _stats from "../cache/stats";
import {getSocketByID} from "../utils/socket";
const router = Router();

router.get("/bots", (req, res) => {

    let Kena1: IWebSocket | null = null;
    let Kena2: IWebSocket | null = null;
    let Kena3: IWebSocket | null = null;
    let Kena4: IWebSocket | null = null;
    wss.clients.forEach((client: IWebSocket) => {
        if (client.id == "KenaBot")
            Kena1 = client
        if (client.id == "KenaBot2")
            Kena2 = client
        if (client.id == "KenaBot3")
            Kena3 = client
        if (client.id == "KenaBot4")
            Kena4 = client
    })
    res.status(200).send([
        Kena1 ? { status: "online", id: "KenaBot" } : { status: "offline", id: "KenaBot" },
        Kena2 ? { status: "online", id: "KenaBot2" } : { status: "offline", id: "KenaBot2" },
        Kena3 ? { status: "online", id: "KenaBot3" } : { status: "offline", id: "KenaBot3" },
        Kena4 ? { status: "online", id: "KenaBot4" } : { status: "offline", id: "KenaBot4" },
    ])
})

router.get("/stats", async (req, res) => {

    let is_new;
    let stats = _stats.get_cache();

    if (stats.renew_at < Date.now()) {
        is_new = true

        const all_commands: any = await commands.find()
        let all_uses = 0
        for (const command of all_commands) {
            all_uses += command.uses
        }
        const top_command = all_commands.sort((a: any, b: any) => b.uses - a.uses)

        const all_voice: any = await Voice.find()
        let all_voice_joins = 0
        for (const voice of all_voice) {
            all_voice_joins += voice.joins
        }

        const random = v4();

        let Kena1: IWebSocket | null = null;
        let Kena2: IWebSocket | null = null;
        let Kena3: IWebSocket | null = null;
        let Kena4: IWebSocket | null = null;

        let KenaBeta: IWebSocket | null = null;
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

        Kena1?.send(JSON.stringify({
            type: "action",
            action: "/stats",
            method: "GET",
            id: random
        }))
        Kena2?.send(JSON.stringify({
            type: "action",
            action: "/stats",
            method: "GET",
            id: random
        }))
        Kena3?.send(JSON.stringify({
            type: "action",
            action: "/stats",
            method: "GET",
            id: random
        }))
        Kena4?.send(JSON.stringify({
            type: "action",
            action: "/stats",
            method: "GET",
            id: random
        }))
        KenaBeta?.send(JSON.stringify({
            type: "action",
            action: "/stats",
            method: "GET",
            id: random
        }))

        const all_Res: any[] = [];

        Kena1?.on("message", async (data: string) => {
            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)
            if (object.id == random) {
                all_Res.push(object)
            }
        })
        Kena2?.on("message", async (data: string) => {
            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)
            if (object.id == random) {
                all_Res.push(object)
            }
        })
        Kena3?.on("message", async (data: string) => {
            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)
            if (object.id == random) {
                all_Res.push(object)
            }
        })
        Kena4?.on("message", async (data: string) => {
            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)
            if (object.id == random) {
                all_Res.push(object)
            }
        })
        KenaBeta?.on("message", async (data: string) => {
            const buffer = Buffer.from(data).toString("utf8")
            const object = JSON.parse(buffer)
            if (object.id == random) {
                all_Res.push(object)
            }
        })

        await delay(500);
        const guilds = all_Res.map((res: any) => res.data.guilds).reduce((a: number, b: number) => a + b, 0)
        const users = all_Res.map((res: any) => res.data.users).reduce((a: number, b: number) => a + b, 0)
        const channels = all_Res.map((res: any) => res.data.channels).reduce((a: number, b: number) => a + b, 0)
        _stats.set_cache({
            commands: {
                total_uses: all_uses,
                command_count: all_commands.length,
                top_commands: [top_command.map((i: any) => i.command)[0], top_command.map((i: any) => i.command)[1], top_command.map((i: any) => i.command)[2]]
            },
            voice: {
                total_joins: all_voice_joins,
                channel_count: all_voice.length + channels,
            },
            guilds,
            users,
            renew_at: Date.now() + 1000 * 60 * 60 * 24,
            renewed: true
        })
        stats = _stats.get_cache();
    }
    if (!is_new)
        stats.renewed = false

    res.send(stats)
})

router.all("/bots/:id/*", async (req, res) => {

    const random = v4();
    const id = req.params.id;
    if (!["1", "2", "3", "4", "5"].includes(id))
        return res.status(404).send({
            status: 404,
            message: "Not Found"
        })

    const socket = getSocketByID(id)
    const path = req.url.replace("/bots/"+id, "").split(("/"))[1];

    if (!socket) {
        res.status(200).send({
            status: 500,
            message: "Bot is offline"
        })
        return;
    }

    socket.send(JSON.stringify({
        type: "action",
        action: "/"+path,
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
            params: Object.values(req.params).map(i => i.replace(path, "").replace(/\//g, "")) || {},
        }
    }))

    let responded = false;
    socket?.on("message", async (data: string) => {
        responded = true;
        const buffer = Buffer.from(data).toString("utf8")
        const object = JSON.parse(buffer)
        if (object.id == random) {
            try {
                await res.status(200).send(object.data)
            } catch (e) {
                return;
            }
        }
    })
    await delay(500);
    if (!responded) {
        res.status(200).send({
            status: 404,
            message: "Page not found"
        })
        return;
    }
})

router.all("/master/*", async (req, res) => {

    const random = v4();

    let bot: typeof IWebSocket | any = null;

    for (const _client of wss.clients) {
        const client: IWebSocket = _client
        if (client.id == "KenaBot")
            bot = client
    }

    if (!bot) {
        res.status(500).send({
            status: 500,
            message: "Master bot is offline"
        })
        return;
    }

    bot.send(JSON.stringify({
        type: "action",
        action: "/" + req.url.split("/")[2],
        data: req.url.split("/").filter(x => x !== "master" && x !== ""),
        method: req.method,
        id: random
    }))

    let responded = false;
    bot.on("message", async (data: string) => {
        responded = true;
        const buffer = Buffer.from(data).toString("utf8")
        const object = JSON.parse(buffer)
        if (object.id == random) {
            try {
                await res.status(200).send(object.data)
            } catch (e) {
                return;
            }
        }
    })

    await delay(500);

    if (!responded) {
        res.status(404).send({
            status: 404,
            message: "Page not found"
        })
        return;
    }

})

export default router;
