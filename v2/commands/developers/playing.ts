/*
 * File:          playing.ts
 * Path:          v2\commands\developers\playing.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-07 00:09:33 +02:00
 *
 * SHA-256: a90e4fd82981ab88d5b8f80dca4c229928a9b2f337245af9f28d61448bb883ae
 *
 */


import Zeppelin from "../../utils/Client";
import { Message } from "discord.js";

module.exports = {
    name: "playing",
    desc: "Reproductores activos.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const players = client.manager?.players.map(p => p.playing)
        if (!players || !players.length)
            return message.reply("No hay ningun reproductor activo.")
        const playing = players.filter(p => p);
        if (!playing || !playing.length)
            return message.reply("No hay ningun reproductor activo.")
        message.reply(`${playing.length} reproductores activos.`)
    }
}
