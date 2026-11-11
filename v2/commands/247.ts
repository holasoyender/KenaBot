/*
 * File:          247.ts
 * Path:          v2\commands\247.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-04 09:27:30 +02:00
 *
 * SHA-256: 4c1dd0c0b76c441ef6eb808f5a67102e578a74244285e00197bbe778c3b9fe3e
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import Guild from "../database/schema/Guild";

module.exports = {
    name: "247",
    desc: "Establecer un canal de 24/7.",
    category: "Music",
    permissions: "Gestionar servidor",
    premium: true,
    usage: "<Activar/Desactivar> <Canal>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        if (!event.member.permissions.has("MANAGE_GUILD")) {
            return event.reply({
                content: lang.manager.no_perms,
                ephemeral: true
            });
        }

        if (!config.premium.enabled) {
            return event.reply({
                content: lang.manager.no_premium,
                ephemeral: true
            });
        }

        const active = event.options.getString("activado") == "on";
        const channel = event.options.getChannel("canal");

        if (active) {
            if (!channel) {
                return event.reply({
                    content: lang.commands._247.no_channel,
                    ephemeral: true
                });
            }

            const g = await Guild.findOne({
                guildID: event.guild.id
            })

            if (channel.type !== "GUILD_VOICE") {
                if (channel.type !== "GUILD_STAGE_VOICE")
                    return event.reply({
                        content: lang.commands._247.no_channel,
                        ephemeral: true
                    });
            }

            if (g) {
                g.set({
                    _247: {
                        channelID: channel.id,
                        enabled: true
                    }
                })

                await g.save()
                await event.reply({
                    content: lang.commands._247.enabled.replace("%s%", channel.toString()),
                    ephemeral: false
                })
            } else {
                const guild = new Guild({
                    guildID: event.guild.id,
                    lang: Default.lang,
                    premium: false,
                    color: Default.color,
                    _247: {
                        channelID: channel.id,
                        enabled: true,
                    }
                })

                await guild.save()
                await event.reply({
                    content: lang.commands._247.enabled.replace("%s%", channel.toString()),
                    ephemeral: false
                })
            }

        } else {
            const g = await Guild.findOne({
                guildID: event.guild.id
            })

            if (g) {
                g.set({
                    _247: {
                        enabled: false
                    }
                })

                await g.save()
                await event.reply({
                    content: lang.commands._247.disabled,
                    ephemeral: false
                })
            } else {
                const guild = new Guild({
                    guildID: event.guild.id,
                    lang: Default.lang,
                    premium: false,
                    color: Default.color,
                    _247: {
                        channelID: null,
                        enabled: false,
                    }
                })

                await guild.save()

                const player = client.manager?.get(event.guild.id);
                if (player && !player.playing) player.destroy();

                await event.reply({
                    content: lang.commands._247.disabled,
                    ephemeral: false
                })
            }
        }

    },
    slashInfo: new SlashCommandBuilder()
        .setName("247")
        .setDescription("Establecer un canal de 24/7.")
        .addStringOption(option =>
            option.setName("activado")
                .setDescription("Activar o desactivar el modo 24/7.")
                .setRequired(true)
                .addChoices(
                    { name: "Activar", value: "on" },
                    { name: "Desactivar", value: "off" }
                ))
        .addChannelOption(option =>
            option.setName("canal")
                .setDescription("Canal de voz para el modo 24/7.")
                .setRequired(false))
}
