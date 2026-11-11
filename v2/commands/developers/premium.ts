/*
 * File:          premium.ts
 * Path:          v2\commands\developers\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:34:26 +02:00
 *
 * SHA-256: 147db3808214f2da723ff87eb7fd925496a547d96ee09692d6a8c8e8b4987b91
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageEmbed} from "discord.js";
import Guild from "../../database/schema/Guild";
import ms from "../../utils/ms";
import cluster from "../../utils/chunk";
import { v4 } from "uuid"
import Premium from "../../database/schema/Premium";

module.exports = {
    name: "premium",
    desc: "Dar information o añadir servidores premium.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const content = message.content.split(" ").slice(1);
        let g: any;
        let time: any;
        let rawTime: any;
        let uid: any;
        if (!content[0]) {
            let all_premium = await Guild.find(/*{premium: {enabled: true}}*/);
            if(!all_premium || !all_premium.length) return message.channel.send("No hay ningún servidor premium.");

            all_premium = all_premium.filter((g:any) => g.premium.enabled);
            if(!all_premium || !all_premium.length) return message.channel.send("No hay ningún servidor premium.");
            const description = all_premium.map((guild: any) => `[${guild.guildID}] (${client.guilds.cache.get(guild.guildID)?.name || "Desconocido"}) Durante: ${guild.premium.expires == "Forever" ? "Siempre (Partner)" : ms(guild.premium.expires - Date.now())}`);

            let _msg = description.join("\n");

            if (_msg.length > 1600) {
                const clustered = cluster(description, 30);
                for(const chunk of clustered) {
                    _msg = chunk.join("\n");
                    message.channel.send("```nim\n"+_msg+"```");
                }

            } else {
                message.channel.send("```nim\n"+_msg+"```");
            }
            return;
        }


        switch (content[0]) {
            case "add":
            case "añadir":
            case "set":
            case "ad":
            case "enable":
            case "create":
            case "crear":

                rawTime = content[1] ? content[1] : "Forever";
                time = ms(rawTime);

                if(rawTime !== "Forever") {
                    if(!time || isNaN(time)) return message.channel.send("Debes de especificar un tiempo válido!\n`Ejemplo: premium add <tiempo>`");
                    time = Date.now() + ms(rawTime);
                }else time = rawTime;

                uid = v4().replace(/-/g, "")

                g = await new Premium({
                    code: uid,
                    duration: rawTime.toString(),
                    createdAt: new Date(),
                    claimed: false,
                    claimedAt: "",
                    claimedOn: null,
                    createdBy: message.author.id,
                    bots: [],
                })

                g.save();
                await message.reply(`Se ha creado un nuevo código premium valido durante **${rawTime}**: \`\`\`${uid}\`\`\``)

                break;

            case "del":
            case "delete":
            case "rem":
            case "remove":
            case "borrar":
            case "quitar":
            case "disable":
                g = await Guild.findOne({guildID: content[1]});
                if (!g) {

                    g = await Premium.findOne({code: content[1]})
                    if (!g) {
                        return message.reply({
                            content: "El servidor con ID `" + content[1] + "` no tiene premium y no existe ningún codigo premium que coincida con `" + content[1] + "`.",
                            allowedMentions: {repliedUser: false}
                        });
                    } else {
                        g.delete();
                        return await message.reply({
                            content: "Se ha desactivado el código **premium** con ID `" + content[1] + "`.",
                            allowedMentions: {repliedUser: false}
                        });
                    }
                }
                if (!g.premium.enabled) return message.reply({
                    content: "El servidor con ID `" + content[1] + "` no tiene premium.",
                    allowedMentions: {repliedUser: false}
                });

                await message.reply({
                    content: "Se ha desactivado el modo **premium** para servidor con ID `" + content[1] + "`.",
                    allowedMentions: {repliedUser: false}
                });

                // eslint-disable-next-line no-case-declarations
                const guild2 = client.guilds.cache.get(content[1]);
                if (guild2) {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Premium quitado de un servidor`,
                            iconURL: `${guild2.iconURL()}`
                        })
                        .setDescription(`Se ha desactivado el premium en el servidor **${guild2.name}** manualmente.`)
                    await client.webhook.send({embeds: [embed]})
                }else {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Premium quitado de un servidor`,
                        })
                        .setDescription(`Se ha desactivado el premium en el servidor **Desconocido** manualmente.`)
                    await client.webhook.send({embeds: [embed]})
                }

                g.premium.enabled = false;
                g.premium.expires = "";
                g.save();

                break;

            default:

                g = await Guild.findOne({guildID: content[0]});
                if (!g) return message.reply({
                    content: "El servidor con ID `" + content[0] + "` no tiene premium.",
                    allowedMentions: {repliedUser: false}
                });
                if (g.premium.enabled) {
                    let time
                    if(g.premium.expires == "Forever") time = "Forever"
                    else time = ms(Date.now()- g.premium.expires)
                    return message.reply({
                        content: `El servidor con ID \`${content[0]}\` tiene premium (${time} restantes).`,
                        allowedMentions: {repliedUser: false}
                    });
                }
                await message.reply({
                    content: "El servidor con ID `" + content[0] + "` no tiene premium.",
                    allowedMentions: {repliedUser: false}
                });

                break;
        }
    },
}