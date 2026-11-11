/*
 * File:          premium.ts
 * Path:          v2\commands\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:02:10 +02:00
 *
 * SHA-256: 8aff2230e680b4c600b521f3a79502c659c0f53009e88b526aab4e14734244fd
 *
 */


import Zeppelin from "../utils/Client";
import esES from "../langs/es-ES";
import Default from "../utils/config/default";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, MessageEmbed} from "discord.js";
import Premium from "../database/schema/Premium";
import {inArray} from "../utils/chunk";
import Guild from "../database/schema/Guild";
import ms from "../utils/ms";

module.exports = {
    name: "premium",
    desc: "Activar el modo donador con un código",
    category: "Bot",
    permissions: "Gestionar Servidor",
    usage: "<CODE>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const code = event.options.getString("code");
        if (!code) return event.reply({
            content: lang.commands.premium.no_code,
            ephemeral: true
        })
        const premium = await Premium.findOne({code})

        if(!premium) return event.reply({
            content: lang.commands.premium.invalid_code,
            ephemeral: true
        })

        if(premium.claimed) return event.reply({
            content: lang.commands.premium.used_code,
            ephemeral: true
        })

        if(premium.claimedAt && premium.claimedAt !== event.guild.id) return event.reply({
            content: lang.commands.premium.invalid_code,
            ephemeral: true
        });

        if(inArray(premium.bots, `${client.user?.id}`)) return event.reply({
            content: lang.commands.premium.bot_claimed,
            ephemeral: true
        });
        premium.bots.push(`${client.user?.id}`)
        if(premium.bots.length === 4) premium.claimed = true
        premium.claimedAt = event.guild.id

        let date
        if(!premium.claimedOn) {
            date = Date.now()
            premium.claimedOn = new Date()
        }
        else
            date = new Date(premium.claimedOn).getTime();

        await premium.save()

        const g:any = await Guild.findOne({guildID: event.guild.id});

        const rawTime = premium.duration;
        let time;

        if(rawTime !== "Forever") {
            time = date + ms(rawTime as any);
        }else time = rawTime;

        if (!g) {
            new Guild({
                guildID: event.guild.id,
                premium: {
                    enabled: true,
                    expires: time
                }
            }).save();

            await event.reply({
                content: lang.commands.premium.enabled.replace("%t%", rawTime?.toString() || ""),
                ephemeral: true
            })

        } else {
            if(g.premium.enabled) return event.reply({
                content: lang.commands.premium.already_premium,
                ephemeral: true
            });

            g.premium.enabled = true;
            g.premium.expires = time;
            await g.save();

            await event.reply({
                content: lang.commands.premium.enabled.replace("%t%", rawTime?.toString() || ""),
                ephemeral: true
            })
        }
            const embed = new MessageEmbed()
                .setColor(0x47EB72)
                .setAuthor({
                    name: `Premium añadido a un servidor`,
                    iconURL: `${event.guild.iconURL()}`
                })
                .setDescription(`Se ha activado el premium en el servidor **${event.guild.name}** durante: \`${rawTime}\`.`)
            await client.webhook.send({embeds: [embed]})
    },
    slashInfo: new SlashCommandBuilder()
        .setName("premium")
        .setDescription("Activar el modo donador de KenaBot con un código")
        .addStringOption(option =>
            option.setName('code')
                .setDescription('Código de donador')
                .setRequired(true))
}