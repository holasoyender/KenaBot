/*
 * File:          partner.ts
 * Path:          v2\commands\developers\partner.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:34:26 +02:00
 *
 * SHA-256: ada7f7c7dc2ca57bf24db1f63b98592ce24d86117c676b694dc1cb38bf5a0e43
 *
 */


import Zeppelin from "../../utils/Client";
import {Message, MessageEmbed} from "discord.js";
import Guild from "../../database/schema/Guild";
import cluster from "../../utils/chunk";

module.exports = {
    name: "partner",
    desc: "Dar information o añadir servidores partner.",
    category: "Developers",
    permissions: "Developers",
    run: async (client: Zeppelin, message: Message) => {

        const content = message.content.split(" ").slice(1);
        let g: any;
        if (!content[0]) {
            let all_premium = await Guild.find(/*{premium: {enabled: true}}*/);
            if(!all_premium || !all_premium.length) return message.channel.send("No hay ningún servidor partner.");

            all_premium = all_premium.filter((g:any) => g.premium.enabled && g.premium.expires == "Forever");
            if(!all_premium || !all_premium.length) return message.channel.send("No hay ningún servidor partner.");

            const description = all_premium.map((guild: any) => {
                const guild_cache = client.guilds.cache.get(guild.guildID);
                return `[${guild.guildID}] (${guild_cache?.name || "Desconocido"}) ${guild_cache?.memberCount || 0} Miembros `}
            );

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

                g = await Guild.findOne({guildID: content[1]});
                if (!g) {
                    new Guild({
                        guildID: content[1],
                        premium: {
                            enabled: true,
                            expires: "Forever"
                        }
                    }).save();

                    const guild = client.guilds.cache.get(content[1]);
                    if (guild) {
                        const embed = new MessageEmbed()
                            .setColor(0x47EB72)
                            .setAuthor({
                                name: `Partner añadido a un servidor`,
                                iconURL: `${guild.iconURL()}`
                            })
                            .setDescription(`Se ha activado el modo partner en el servidor **${guild.name}**.`)
                        await client.webhook.send({embeds: [embed]})
                    }else {
                        const embed = new MessageEmbed()
                            .setColor(0x47EB72)
                            .setAuthor({
                                name: `Partner añadido a un servidor`,
                            })
                            .setDescription(`Se ha activado el modo partner en el servidor **Desconocido**.`)
                        await client.webhook.send({embeds: [embed]})
                    }

                    await message.reply({
                        content: `Se ha activado el modo **partner** para servidor con ID \`${content[1]}\`.`,
                        allowedMentions: {repliedUser: false}
                    });

                    return;
                }
                if (g.premium.enabled && g.premium.expires == "Forever") return message.reply({
                    content: "El servidor con ID `" + content[1] + "` ya es partner.",
                    allowedMentions: {repliedUser: false}
                });

                await message.reply({
                    content: `Se ha activado el modo **partner** para servidor con ID \`${content[1]}\`.`,
                    allowedMentions: {repliedUser: false}
                });

                // eslint-disable-next-line no-case-declarations
                const guild = client.guilds.cache.get(content[1]);
                if (guild) {
                    const embed = new MessageEmbed()
                        .setColor(0x47EB72)
                        .setAuthor({
                            name: `Partner añadido a un servidor`,
                            iconURL: `${guild.iconURL()}`
                        })
                        .setDescription(`Se ha activado el modo partner en el servidor **${guild.name}**.`)
                    await client.webhook.send({embeds: [embed]})
                }else {
                    const embed = new MessageEmbed()
                        .setColor(0x47EB72)
                        .setAuthor({
                            name: `Partner añadido a un servidor`,
                        })
                        .setDescription(`Se ha activado el modo partner en el servidor **Desconocido**.`)
                    await client.webhook.send({embeds: [embed]})
                }

                g.premium.enabled = true;
                g.premium.expires = "Forever";
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
                    content: "El servidor con ID `" + content[1] + "` no tiene partner.",
                    allowedMentions: {repliedUser: false}
                });
                if (!g.premium.enabled) return message.reply({
                    content: "El servidor con ID `" + content[1] + "` no tiene partner.",
                    allowedMentions: {repliedUser: false}
                });

                await message.reply({
                    content: "Se ha desactivado el modo **partner** para servidor con ID `" + content[1] + "`.",
                    allowedMentions: {repliedUser: false}
                });

                // eslint-disable-next-line no-case-declarations
                const guild2 = client.guilds.cache.get(content[1]);
                if (guild2) {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Partner quitado de un servidor`,
                            iconURL: `${guild2.iconURL()}`
                        })
                        .setDescription(`Se ha desactivado el modo partner en el servidor **${guild2.name}** manualmente.`)
                    await client.webhook.send({embeds: [embed]})
                }else {
                    const embed = new MessageEmbed()
                        .setColor(0xFE5B50)
                        .setAuthor({
                            name: `Partner quitado de un servidor`,
                        })
                        .setDescription(`Se ha desactivado el modo partner en el servidor **Desconocido** manualmente.`)
                    await client.webhook.send({embeds: [embed]})
                }

                g.premium.enabled = false;
                g.premium.expires = "";
                g.save();

                break;

            default:

                g = await Guild.findOne({guildID: content[0]});
                if (!g) return message.reply({
                    content: "El servidor con ID `" + content[0] + "` no tiene partner.",
                    allowedMentions: {repliedUser: false}
                });
                if (g.premium.enabled && g.premium.expires == "Forever") return message.reply({
                    content: `El servidor con ID \`${content[0]}\` tiene partner.`,
                    allowedMentions: {repliedUser: false}
                });
                await message.reply({
                    content: "El servidor con ID `" + content[0] + "` no tiene partner.",
                    allowedMentions: {repliedUser: false}
                });

                break;
        }
    },
}