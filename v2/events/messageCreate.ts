/*
 * File:          messageCreate.ts
 * Path:          v2\events\messageCreate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-18 15:19:34 +02:00
 *
 * SHA-256: 34fee6a873c158fcc31f3af39a759452f30fc15e7663ecc49248cb26557a3681
 *
 */


import Zeppelin from "../utils/Client";
import { Message, MessageActionRow, MessageButton, MessageEmbed } from "discord.js";
import esES from "../langs/es-ES";
import Default from "../utils/config/default";

exports.run = async (client: Zeppelin, message: Message) => {
    if (message.channel.type === "DM") {
        if (message.author.bot) return;
        if (!client.develop.owners.includes(message.author.id)) {

            const web = new MessageButton().setLabel(esES.events.guildCreate.buttons.web).setEmoji("🖥️").setStyle("LINK").setURL("https://kenabot.xyz/");
            const support = new MessageButton().setLabel(esES.events.guildCreate.buttons.support).setEmoji("⚙️").setStyle("LINK").setURL("https://kenaboy.xyz/support");
            const config = new MessageButton().setLabel(esES.events.guildCreate.buttons.dash).setEmoji("🔧").setStyle("LINK").setURL("https://dashboard.kenabot.xyz/");
            const row = new MessageActionRow().addComponents([web, config, support]);

            const embed = new MessageEmbed()
                //@ts-ignore
                .setColor(Default.color)
                .setTitle(esES.events.dm.embed_title.replace("%s%", client.user?.username || "KenaBot"))
                .setDescription(esES.events.dm.embed_description)
            return await message.channel.send({
                embeds: [embed],
                components: [row]
            });
        } else {
            const args = message.content.slice(1).split(/ +/g);
            const command = args?.shift()?.toLowerCase()

	    if(message.content.startsWith("$")) return message.reply({
		    content: "Ahora el prefijo de los comandos de desarrollo es `!`\n`Todos los comandos ejecutados se quedarán guardados en el log de debug del bot`",
		    allowedMentions: { repliedUser: false }
	    })
            if (!message.content.startsWith("!")) return;
            if (client.develop.Commands.find(cmd => cmd.name.toLowerCase() === command)) {
                try {
                    client.develop.Commands.find(cmd => cmd.name.toLowerCase() === command).run(client, message, args)
                } catch (e) {
                    await message.react("🚫");
                    console.log(e);
                }
            }
        }

    } else {
        if (!client.develop.owners.includes(message.author.id)) return;

        const args = message.content.slice(1).split(/ +/g);
        const command = args?.shift()?.toLowerCase()

        if (!message.content.startsWith("-")) return;
        if (client.develop.Commands.find(cmd => cmd.name.toLowerCase() === command)) {
            try {
                client.develop.Commands.find(cmd => cmd.name.toLowerCase() === command).run(client, message, args)
            } catch (e) {
                await message.react("🚫");
                console.log(e);
            }
        }
    }
}
