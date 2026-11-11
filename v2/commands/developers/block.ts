/*
 * File:          block.ts
 * Path:          v2\commands\developers\block.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 8d5fa1acab86f29c83fc83a02863b3ebe09038aed5dc6f1f5ea6a2caae6b6f5a
 *
 */


import Zeppelin from "../../utils/Client";
import {Message} from "discord.js";
import Guild from "../../database/schema/Guild";

module.exports = {
    name: "block",
    desc: "Bloquear a un usuario de usar el bot.",
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
            if(guild_db.blocked.enabled)
                return message.channel.send("El servidor ya está bloqueado.");

            guild_db.blocked.enabled = true;
            guild_db.blocked.reason = args.slice(1).join(" ") || "No se especificó una razón";
            guild_db.save();
            return message.channel.send("Servidor con ID `" + guild + "` bloqueado.");
        } else {
            const new_guild = new Guild({
                guildID: guild,
                blocked: {
                    enabled: true,
                    reason: args.slice(1).join(" ") || "No se especificó una razón"
                }
            });
            new_guild.save();
            return message.channel.send("Servidor con ID `" + guild + "` bloqueado.");
        }
    }
}