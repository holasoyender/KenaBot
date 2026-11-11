/*
 * File:          filter.ts
 * Path:          v2\commands\filter.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 21:27:03 +02:00
 *
 * SHA-256: dba2e514b38914009d5044fdeae96d6bd19d8e512c98574b5622239f251aecc3
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember, MessageEmbed } from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import { Player } from "../functions/manager";
import { checkChannel, checkRole } from "../utils/djrole";

module.exports = {
    name: "filter",
    desc: "Poner filtros de audio en la canción actual.",
    category: "Music",
    permissions: "everyone",
    premium: true,
    usage: "<Filtro>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        if (!config.premium.enabled) {
            return event.reply({
                content: lang.manager.no_premium,
                ephemeral: true
            });
        }

        const player: Player | undefined = client.manager?.get(event.guild.id);
        if (!player) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (!player.queue.current) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (event.guild.me.voice.channel !== event.member.voice.channel) return event.reply({
            content: lang.manager.cant_modify_queue,
            ephemeral: true
        })

        const channel = event.guild.channels.cache.get(`${player.voiceChannel}`)
        if (channel && channel.type == "GUILD_VOICE") {
            if (checkChannel(channel, config, player)) {
                if (!checkRole(event.member, config, player)) {
                    const embed = new MessageEmbed()
                        // @ts-ignore
                        .setColor(config.color)
                        .setDescription(lang.manager.dj_role_required)
                    return event.reply({ embeds: [embed], ephemeral: true })
                }
            }
        }

        const search = event.options.getString("filter");

        if (!search) return event.reply({
            content: lang.commands.filters.no_filters,
            ephemeral: true
        })

        switch (search) {

            case "bassboost":

                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    equalizer: [
                        {
                            band: 1,
                            gain: 0.50,
                        },
                        {
                            band: 1,
                            gain: 0.50,
                        },
                        {
                            band: 1,
                            gain: 0.50,
                        },
                        {
                            band: 1,
                            gain: 0.50,
                        },
                        {
                            band: 1,
                            gain: 0.50,
                        },
                        {
                            band: 1,
                            gain: 0.50,
                        },
                    ],
                });

                await event.reply({
                    content: lang.commands.filters.bass_boost,
                    ephemeral: false
                })

                break;

            case "8d":
                await player.node.send({
                    op: "filters",
                    guildId: player.guild,
                    rotation: {
                        rotationHz: 0.2
                    }
                });

                await event.reply({
                    content: lang.commands.filters.ocho_d,
                    ephemeral: false
                })
                break;

            case "nightcore":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    timescale: {
                        speed: 1.2999999523162842,
                        pitch: 1.2999999523162842,
                        rate: 1,
                    },
                });

                await event.reply({
                    content: lang.commands.filters.nightcore,
                    ephemeral: false
                })
                break;

            case "vaporwave":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    timescale: {
                        speed: 0.8500000238418579,
                        pitch: 0.800000011920929,
                        rate: 1,
                    },
                });

                await event.reply({
                    content: lang.commands.filters.vaporwave,
                    ephemeral: false
                })

                break;

            case "treble":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    equalizer: [
                        { band: 0, gain: 0.6 },
                        { band: 1, gain: 0.67 },
                        { band: 2, gain: 0.67 },
                        { band: 3, gain: 0 },
                        { band: 4, gain: -0.5 },
                        { band: 5, gain: 0.15 },
                        { band: 6, gain: -0.45 },
                        { band: 7, gain: 0.23 },
                        { band: 8, gain: 0.35 },
                        { band: 9, gain: 0.45 },
                        { band: 10, gain: 0.55 },
                        { band: 11, gain: 0.6 },
                        { band: 12, gain: 0.55 },
                        { band: 13, gain: 0 },
                    ],
                });

                await event.reply({
                    content: lang.commands.filters.treble,
                    ephemeral: false
                })

                break;

            case "karaoke":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    karaoke: {
                        level: 1.0,
                        monoLevel: 1.0,
                        filterBand: 220.0,
                        filterWidth: 100.0
                    },
                });

                await event.reply({
                    content: lang.commands.filters.karaoke,
                    ephemeral: false
                })

                break;

            case "tremolo":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    tremolo: {
                        frequency: 10,
                        depth: 0.5
                    }
                });

                await event.reply({
                    content: lang.commands.filters.tremolo,
                    ephemeral: false
                })

                break;

            case "speed":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    timescale: {
                        speed: 1.5010,
                        pitch: 1.2450,
                        rate: 1.9210
                    }
                });

                await event.reply({
                    content: lang.commands.filters.speed,
                    ephemeral: false
                })

                break;

            case "pitch":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                    equalizer: player.bands.map((gain, index) => {
                        const Obj = {
                            "band": 0,
                            "gain": 0,
                        };
                        Obj.band = Number(index);
                        Obj.gain = Number(gain)
                        return Obj;
                    }),
                    timescale: {
                        "speed": 1.0,
                        "pitch": 1.5010,
                        "rate": 1.0
                    },
                });

                await event.reply({
                    content: lang.commands.filters.pitch,
                    ephemeral: false
                })

                break;

            case "clear":
                await player.node.send({
                    op: 'filters',
                    guildId: player.guild,
                });

                await event.reply({
                    content: lang.commands.filters.clear,
                    ephemeral: false
                })

                break;

            default:
                return event.reply({
                    content: lang.commands.filters.unknown,
                    ephemeral: true
                })
        }
    },
    slashInfo: new SlashCommandBuilder()
        .setName("filter")
        .setDescription("Poner filtros de audio en la canción actual.")
        .addStringOption(option =>
            option.setName("filter")
                .setDescription("Filtro a aplicar.")
                .setRequired(true)
                .addChoices(
                    { name: "Bass Boost", value: "bassboost" },
                    { name: "8D", value: "8d" },
                    { name: "Nightcore", value: "nightcore" },
                    { name: "Vaporwave", value: "vaporwave" },
                    { name: "Treble", value: "treble" },
                    { name: "Karaoke", value: "karaoke" },
                    { name: "Tremolo", value: "tremolo" },
                    { name: "Speed", value: "speed" },
                    { name: "Pitch", value: "pitch" },
                    { name: "Clear", value: "clear" }
                ))
}