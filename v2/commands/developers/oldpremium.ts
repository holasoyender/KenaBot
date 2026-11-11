/*
 * File:          oldpremium.ts
 * Path:          v2\commands\developers\oldpremium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:34:26 +02:00
 *
 * SHA-256: 90292c6574e9ab16f589f0e94a62c7a05c8d3cc30ecd84f3e0c8931934ea3b12
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageEmbed} from "discord.js";
import Guild from "../../database/schema/Guild";
import ms from "../../utils/ms";
import cluster from "../../utils/chunk";

module.exports = {
    name: "oldpremium",
    desc: "Dar information o añadir servidores premium.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const content = message.content.split(" ").slice(1);
        let g: any;
        let time: any;
        let rawTime: any;
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

                rawTime = content[2] ? content[2] : "Forever";
                time = ms(rawTime);

                if(rawTime !== "Forever") {
                    if(!time || isNaN(time)) return message.channel.send("ErrNo: $invalidTime");
                    time = Date.now() + ms(rawTime);
                }else time = rawTime;

                g = await Guild.findOne({guildID: content[1]});
                if (!g) {
                    new Guild({
                        guildID: content[1],
                        premium: {
                            enabled: true,
                            expires: time
                        }
                    }).save();

                    const guild = client.guilds.cache.get(content[1]);
                    if (guild) {
                        const embed = new MessageEmbed()
                            .setColor(0x47EB72)
                            .setAuthor({
                                name: `Premium añadido a un servidor`,
                                iconURL: `${guild.iconURL()}`
                            })
                            .setDescription(`Se ha activado el premium en el servidor **${guild.name}** durante: \`${rawTime}\`.`)
                        await client.webhook.send({embeds: [embed]})
                    }else {
                        const embed = new MessageEmbed()
                            .setColor(0x47EB72)
                            .setAuthor({
                                name: `Premium añadido a un servidor`,
                            })
                            .setDescription(`Se ha activado el premium en el servidor **Desconocido** durante: \`${rawTime}\`.`)
                        await client.webhook.send({embeds: [embed]})
                    }

                    await message.reply({
                        content: `Se ha activado el modo **premium** para servidor con ID \`${content[1]}\` durante: **${rawTime}**.`,
                        allowedMentions: {repliedUser: false}
                    });

                    return;
                }
                if (g.premium.enabled) return message.reply({
                    content: "El servidor con ID `" + content[1] + "` ya tiene premium.",
                    allowedMentions: {repliedUser: false}
                });

                await message.reply({
                    content: `Se ha activado el modo **premium** para servidor con ID \`${content[1]}\` durante: **${rawTime}**.`,
                    allowedMentions: {repliedUser: false}
                });

                // eslint-disable-next-line no-case-declarations
                const guild = client.guilds.cache.get(content[1]);
                if (guild) {
                    const embed = new MessageEmbed()
                        .setColor(0x47EB72)
                        .setAuthor({
                            name: `Premium añadido a un servidor`,
                            iconURL: `${guild.iconURL()}`
                        })
                        .setDescription(`Se ha activado el premium en el servidor **${guild.name}** durante: \`${rawTime}\`.`)
                    await client.webhook.send({embeds: [embed]})
                }else {
                    const embed = new MessageEmbed()
                        .setColor(0x47EB72)
                        .setAuthor({
                            name: `Premium añadido a un servidor`,
                        })
                        .setDescription(`Se ha activado el premium en el servidor **Desconocido** durante: \`${rawTime}\`.`)
                    await client.webhook.send({embeds: [embed]})
                }

                g.premium.enabled = true;
                g.premium.expires = time;
                g.save();

                break;

            case "del":
            case "delete":
            case "rem":
            case "remove":
            case "borrar":
            case "quitar":
            case "disable":
                g = await Guild.findOne({guildID: content[1]});
                if (!g) return message.reply({
                    content: "El servidor con ID `" + content[1] + "` no tiene premium.",
                    allowedMentions: {repliedUser: false}
                });
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
                if (g.premium.enabled) return message.reply({
                    content: `El servidor con ID \`${content[0]}\` tiene premium (${ms(Date.now()- g.premium.expires)} restantes).`,
                    allowedMentions: {repliedUser: false}
                });
                await message.reply({
                    content: "El servidor con ID `" + content[0] + "` no tiene premium.",
                    allowedMentions: {repliedUser: false}
                });

                break;
        }
    },
}