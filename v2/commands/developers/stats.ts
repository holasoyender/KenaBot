/*
 * File:          stats.ts
 * Path:          v2\commands\developers\stats.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-28 19:45:59 +01:00
 *
 * SHA-256: 7268438ca64905c70f260a51f71fac4c443097224fedc236298f21aaffc8d6b7
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageEmbed} from "discord.js";
import Commands from "../../database/schema/Commands";
import Voice from "../../database/schema/Voice";

module.exports = {
    name: "stats",
    desc: "Estadísticas del bot.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const commandStats = await Commands.find();
        const commandData:any = commandStats.sort((a:any, b:any) => b.uses - a.uses)

        const channelStats = await Voice.find();
        const channelData:any = channelStats.sort((a:any, b:any) => b.joins - a.joins)

        const embed = new MessageEmbed()
            .setAuthor({ name: 'Estadísticas del bot', iconURL: client.user?.displayAvatarURL()})
            .setColor("#5865F2");

        let i = 0;
        let cString = "";

        for(const a in commandData) {
            i++
            if(i > 5) break;
            cString = cString+ `${commandData[a].command}: ${commandData[a].uses} usos\n`
        }
        cString = "```" + cString + "```";

        embed.addField("Comandos más usados", cString, true);

        let o = 0;
        let chString = "";

        for(const a in channelData) {
            o++
            if(o > 5) break;
            chString = chString+ `${channelData[a].channelID}: ${channelData[a].joins} veces\n`
        }
        chString = "```" + chString + "```";

        embed.addField("Canales más activos", chString, true);

        await message.reply({embeds: [embed], allowedMentions: { repliedUser: false}});
    },
}
