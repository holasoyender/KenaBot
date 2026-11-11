/*
 * File:          unblock.ts
 * Path:          v2\commands\developers\unblock.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 1ea0e3f9de2d586b39b00a1a7ffef690557685a52048f1d037c5791145a04630
 *
 */


import Zeppelin from "../../utils/Client";
import {Message} from "discord.js";
import Guild from "../../database/schema/Guild";

module.exports = {
    name: "unblock",
    desc: "Desbloquear a un usuario de usar el bot.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const args = message.content.split(" ").slice(1);
        const guild = args[0];

        if(!guild)
            return message.channel.send("Debes especificar un servidor.");
        const d_guild = client.guilds.cache.get(guild);
        if(!d_guild)
            return message.channel.send("No se pudo encontrar el servidor.");

        const guild_db:any = await Guild.findOne({guildID: guild});

        if(guild_db) {
            if(!guild_db.blocked.enabled)
                return message.channel.send("El servidor no está bloqueado.");

            guild_db.blocked.enabled = false;
            guild_db.blocked.reason = ""
            guild_db.save();
            return message.channel.send("Servidor con ID `" + guild + "` desbloqueado.");
        } else return message.channel.send("El servidor no está bloqueado.");
    }
}