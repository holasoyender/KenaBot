/*
 * File:          voice.ts
 * Path:          v2\handlers\voice.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 07d301775d7a1fcb087990732d2a7960dd854fb600568a6a466ae286e3ce4f29
 *
 */


/* eslint-disable no-case-declarations */
import {
    ButtonInteraction,
    MessageActionRow,
    MessageButton,
    MessageEmbed,
    MessageSelectMenu,
    SelectMenuInteraction
} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import chunk, {inArray} from "../utils/chunk";
import Guild from "../database/schema/Guild";


export default async (interaction: SelectMenuInteraction, guildConfig:typeof Default | any, guildLang:typeof esES) => {

    if (interaction.values[0]) {
        const IDs: Array<string> = []

        interaction.values.forEach(value => {
            IDs.push(value.split(":")[3])
        })

        const old:any = []
        interaction.message.components?.forEach(component => {
            // @ts-ignore
            if(component.components[0].customId == interaction.customId) return;
            // @ts-ignore
            component.components[0].options?.forEach((opt:any) => {
                if(opt.default) {
                    old.push({id: opt.value, default: opt.default})
                }
            })
        })
        IDs.push(...old.map((o:any) => o.id.split(":")[3]))

        const guild:any = await Guild.findOne({
            guildID: interaction.guild?.id
        })
        if (!guild) {
            await Guild.create({
                guildID: interaction.guild?.id,
                disabled: {
                    voice: IDs
                },
            });
            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        } else {
            await Guild.updateOne({
                guildID: interaction.guild?.id
            }, {
                disabled: {
                    channels: guild.disabled.channels,
                    commands: guild.disabled.commands,
                    voice: IDs,
                },
            });

            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        }

        const channels: Array<any> = []
        interaction.guild?.channels.cache.forEach(channel => {

            if (channel.type !== "GUILD_VOICE") return;
            channels.push({
                label: channel.name,
                value: `cmd:config:setvoicechannel:${channel.id}:${interaction.user.id}`,
                emoji: inArray(guildConfig.disabled.voice, channel.id) ? "<:voicesel:941355299158245466>" : "<:voice:941354953170116688>",
                default: inArray(guildConfig.disabled.voice, channel.id),
                description: "Channel ID: " + channel.id
            })
        })

        if (channels.length > 100) {
            channels.splice(100, channels.length - 100)
        }

        const rows: Array<any> = []
        chunk(channels, 25).forEach(chunk => {
            const random = Math.floor(Math.random() * 100000)

            rows.push(
                new MessageActionRow().addComponents([
                    new MessageSelectMenu()
                        .setCustomId("cmd:config:voice:select:" + interaction.user.id + ":" + random)
                        .setPlaceholder(guildLang.commands.config.voice.select)
                        .addOptions(chunk)
                        .setMaxValues(chunk.length)
                        .setMinValues(0)
                ])
            )
        })

        await interaction.update({
            components: [
                ...rows,
                new MessageActionRow().addComponents([
                    new MessageButton()
                        .setLabel(guildLang.commands.config.buttons.back)
                        .setStyle("SECONDARY")
                        .setEmoji("<:left:940316141983764494>")
                        .setCustomId("cmd:config:back:" + interaction.user.id),

                    new MessageButton()
                        .setStyle("DANGER")
                        .setLabel(guildLang.commands.config.buttons.select_all)
                        .setCustomId("cmd:config:voicechannels_selectall:" + interaction.user.id),

                    new MessageButton()
                        .setStyle("SUCCESS")
                        .setLabel(guildLang.commands.config.buttons.remove_all)
                        .setCustomId("cmd:config:voicechannels_deselectall:" + interaction.user.id)
                ])
            ]
        })

    } else {

        const args = interaction.customId.split(":")

        if (args[2] === "voice") {
            switch (args[3]) {

                case "select":


                    const IDs:any = [];
                    const old:any = []
                    interaction.message.components?.forEach(component => {
                        // @ts-ignore
                        if(component.components[0].customId == interaction.customId) return;
                        // @ts-ignore
                        component.components[0].options?.forEach((opt:any) => {
                            if(opt.default) {
                                old.push({id: opt.value, default: opt.default})
                            }
                        })
                    })
                    IDs.push(...old.map((o:any) => o.id.split(":")[3]))

                    const guild:any = await Guild.findOne({
                        guildID: interaction.guild?.id
                    })
                    if (!guild) {
                        await Guild.create({
                            guildID: interaction.guild?.id,
                            disabled: {
                                voice: IDs
                            },
                        });
                        guildConfig = await Guild.findOne({
                            guildID: interaction.guild?.id
                        })
                    } else {
                        await Guild.updateOne({
                            guildID: interaction.guild?.id
                        }, {
                            disabled: {
                                channels: guild.disabled.channels,
                                commands: guild.disabled.commands,
                                voice: IDs,
                            },
                        });
                        guildConfig = await Guild.findOne({
                            guildID: interaction.guild?.id
                        })
                    }

                    const channels: Array<any> = []
                    interaction.guild?.channels.cache.forEach(channel => {

                        if (channel.type !== "GUILD_VOICE") return;
                        channels.push({
                            label: channel.name,
                            value: `cmd:config:setvoicechannel:${channel.id}:${interaction.user.id}`,
                            emoji: inArray(guildConfig.disabled.voice, channel.id) ? "<:voicesel:941355299158245466>" : "<:voice:941354953170116688>",
                            default: inArray(guildConfig.disabled.voice, channel.id),
                            description: "Channel ID: " + channel.id
                        })
                    })

                    if (channels.length > 100) {
                        channels.splice(100, channels.length - 100)
                    }

                    const rows: Array<any> = []
                    chunk(channels, 25).forEach(chunk => {
                        const random = Math.floor(Math.random() * 100000)

                        rows.push(
                            new MessageActionRow().addComponents([
                                new MessageSelectMenu()
                                    .setCustomId("cmd:config:voice:select:" + interaction.user.id + ":" + random)
                                    .setPlaceholder(guildLang.commands.config.voice.select)
                                    .addOptions(chunk)
                                    .setMaxValues(chunk.length)
                                    .setMinValues(0)
                            ])
                        )
                    })

                    await interaction.update({
                        components: [
                            ...rows,
                            new MessageActionRow().addComponents([
                                new MessageButton()
                                    .setLabel(guildLang.commands.config.buttons.back)
                                    .setStyle("SECONDARY")
                                    .setEmoji("<:left:940316141983764494>")
                                    .setCustomId("cmd:config:back:" + interaction.user.id),

                                new MessageButton()
                                    .setStyle("DANGER")
                                    .setLabel(guildLang.commands.config.buttons.select_all)
                                    .setCustomId("cmd:config:voicechannels_selectall:" + interaction.user.id),

                                new MessageButton()
                                    .setStyle("SUCCESS")
                                    .setLabel(guildLang.commands.config.buttons.remove_all)
                                    .setCustomId("cmd:config:voicechannels_deselectall:" + interaction.user.id)
                            ])
                        ]
                    })

                    break;
            }
        }
    }
}

export const handleDefault = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.voice.title)
        .setDescription(guildLang.commands.config.voice.description)

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_VOICE") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setvoicechannel:${channel.id}:${interaction.user.id}`,
            emoji: inArray(guildConfig.disabled.voice, channel.id) ? "<:voicesel:941355299158245466>" : "<:voice:941354953170116688>",
            default: inArray(guildConfig.disabled.voice, channel.id),
            description: "Channel ID: " + channel.id
        })
    })

    if (channels.length > 100) {
        channels.splice(100, channels.length - 100)
    }

    const rows: Array<any> = []
    chunk(channels, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:voicechannels:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.voice.select)
                    .addOptions(chunk)
                    .setMaxValues(chunk.length)
                    .setMinValues(0)
            ])
        )
    })

    await interaction.update({
        embeds: [embed],
        components: [
            ...rows,
            new MessageActionRow().addComponents([
                new MessageButton()
                    .setLabel(guildLang.commands.config.buttons.back)
                    .setStyle("SECONDARY")
                    .setEmoji("<:left:940316141983764494>")
                    .setCustomId("cmd:config:back:" + interaction.user.id),

                new MessageButton()
                    .setStyle("DANGER")
                    .setLabel(guildLang.commands.config.buttons.select_all)
                    .setCustomId("cmd:config:voicechannels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:voicechannels_deselectall:" + interaction.user.id)
            ])
        ]
    })
}

export const handleSelectAllVoice = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const ids: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {
        if (channel.type !== "GUILD_VOICE") return;
        ids.push(channel.id)
    })

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if(!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                voice: ids
            },
        });
    }else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: guild.disabled.channels,
                commands: guild.disabled.commands,
                voice: ids,
            },
        });
    }

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_VOICE") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setvoicechannel:${channel.id}:${interaction.user.id}`,
            emoji: "<:voicesel:941355299158245466>",
            default: true,
            description: "Channel ID: " + channel.id
        })
    })

    if (channels.length > 100) {
        channels.splice(100, channels.length - 100)
    }

    const rows: Array<any> = []
    chunk(channels, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:voice:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.voice.select)
                    .addOptions(chunk)
                    .setMaxValues(chunk.length)
                    .setMinValues(0)
            ])
        )
    })

    await interaction.update({
        components: [
            ...rows,
            new MessageActionRow().addComponents([
                new MessageButton()
                    .setLabel(guildLang.commands.config.buttons.back)
                    .setStyle("SECONDARY")
                    .setEmoji("<:left:940316141983764494>")
                    .setCustomId("cmd:config:back:" + interaction.user.id),

                new MessageButton()
                    .setStyle("DANGER")
                    .setLabel(guildLang.commands.config.buttons.select_all)
                    .setCustomId("cmd:config:voicechannels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:voicechannels_deselectall:" + interaction.user.id)
            ])
        ]
    })
}

export const handleDeselectAllVoice = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if(!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                voice: []
            },
        });
    }else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: guild.disabled.channels,
                commands: guild.disabled.commands,
                voice: [],
            },
        });
    }

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_VOICE") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setvoicechannel:${channel.id}:${interaction.user.id}`,
            emoji: "<:voice:941354953170116688>",
            default: false,
            description: "Channel ID: " + channel.id
        })
    })

    if (channels.length > 100) {
        channels.splice(100, channels.length - 100)
    }

    const rows: Array<any> = []
    chunk(channels, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:voice:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.voice.select)
                    .addOptions(chunk)
                    .setMaxValues(chunk.length)
                    .setMinValues(0)
            ])
        )
    })

    await interaction.update({
        components: [
            ...rows,
            new MessageActionRow().addComponents([
                new MessageButton()
                    .setLabel(guildLang.commands.config.buttons.back)
                    .setStyle("SECONDARY")
                    .setEmoji("<:left:940316141983764494>")
                    .setCustomId("cmd:config:back:" + interaction.user.id),

                new MessageButton()
                    .setStyle("DANGER")
                    .setLabel(guildLang.commands.config.buttons.select_all)
                    .setCustomId("cmd:config:voicechannels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:voicechannels_deselectall:" + interaction.user.id)
            ])
        ]
    })
}