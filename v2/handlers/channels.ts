/*
 * File:          channels.ts
 * Path:          v2\handlers\channels.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: b9fa5c41e3dd184f5e8d84ae205e720e22744f0de646f080b2fc3029e5e460e0
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
import Guild from "../database/schema/Guild";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import chunk, {inArray} from "../utils/chunk";

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
                    channels: IDs
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
                    channels: IDs,
                    commands: guild.disabled.commands,
                    voice: guild.disabled.voice,
                },
            });

            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        }

        const channels: Array<any> = []
        interaction.guild?.channels.cache.forEach(channel => {

            if (channel.type !== "GUILD_TEXT") return;
            channels.push({
                label: channel.name,
                value: `cmd:config:setdchannel:${channel.id}:${interaction.user.id}`,
                emoji: inArray(guildConfig.disabled.channels, channel.id) ? "<:channelssel:940633186000572498>" : "<:channels:940254447530016859>",
                default: inArray(guildConfig.disabled.channels, channel.id),
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
                        .setCustomId("cmd:config:channels:select:" + interaction.user.id + ":" + random)
                        .setPlaceholder(guildLang.commands.config.channels.select)
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
                        .setCustomId("cmd:config:channels_selectall:" + interaction.user.id),

                    new MessageButton()
                        .setStyle("SUCCESS")
                        .setLabel(guildLang.commands.config.buttons.remove_all)
                        .setCustomId("cmd:config:channels_desselectall:" + interaction.user.id)
                ])
            ]
        })

    } else {

        const args = interaction.customId.split(":")

        if (args[2] === "channels") {
            switch (args[3]) {

                case "select":

                    const IDs:any = []
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
                                channels: IDs
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
                                channels: IDs,
                                commands: guild.disabled.commands,
                                voice: guild.disabled.voice,
                            },
                        });
                        guildConfig = await Guild.findOne({
                            guildID: interaction.guild?.id
                        })
                    }

                    const channels: Array<any> = []
                    interaction.guild?.channels.cache.forEach(channel => {

                        if (channel.type !== "GUILD_TEXT") return;
                        channels.push({
                            label: channel.name,
                            value: `cmd:config:setdchannel:${channel.id}:${interaction.user.id}`,
                            emoji: inArray(guildConfig.disabled.channels, channel.id) ? "<:channelssel:940633186000572498>" : "<:channels:940254447530016859>",
                            default: inArray(guildConfig.disabled.channels, channel.id),
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
                                    .setCustomId("cmd:config:channels:select:" + interaction.user.id + ":" + random)
                                    .setPlaceholder(guildLang.commands.config.channels.select)
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
                                    .setCustomId("cmd:config:channels_selectall:" + interaction.user.id),

                                new MessageButton()
                                    .setStyle("SUCCESS")
                                    .setLabel(guildLang.commands.config.buttons.remove_all)
                                    .setCustomId("cmd:config:channels_desselectall:" + interaction.user.id)
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
        .setTitle(guildLang.commands.config.channels.title)
        .setDescription(guildLang.commands.config.channels.description)

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_TEXT") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setdchannel:${channel.id}:${interaction.user.id}`,
            emoji: inArray(guildConfig.disabled.channels, channel.id) ? "<:channelssel:940633186000572498>" : "<:channels:940254447530016859>",
            default: inArray(guildConfig.disabled.channels, channel.id),
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
                    .setCustomId("cmd:config:channels:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.channels.select)
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
                    .setCustomId("cmd:config:channels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:channels_desselectall:" + interaction.user.id)
            ])
        ]
    })
}
export const handleSelectAll = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const ids: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {
        if (channel.type !== "GUILD_TEXT") return;
        ids.push(channel.id)
    })

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if(!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                channels: ids
            },
        });
    }else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: ids,
                commands: guild.disabled.commands,
                voice: guild.disabled.voice,
            },
        });
    }

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_TEXT") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setdchannel:${channel.id}:${interaction.user.id}`,
            emoji: "<:channelssel:940633186000572498>",
            default: true,
            description: "ID del canal: " + channel.id
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
                    .setCustomId("cmd:config:channels:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.channels.select)
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
                    .setCustomId("cmd:config:channels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:channels_desselectall:" + interaction.user.id)
            ])
        ]
    })
}

export const handleDeselectAll = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if(!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                channels: []
            },
        });
    }else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: [],
                commands: guild.disabled.commands,
                voice: guild.disabled.voice,
            },
        });
    }

    const channels: Array<any> = []
    interaction.guild?.channels.cache.forEach(channel => {

        if (channel.type !== "GUILD_TEXT") return;
        channels.push({
            label: channel.name,
            value: `cmd:config:setdchannel:${channel.id}:${interaction.user.id}`,
            emoji: "<:channels:940254447530016859>",
            default: false,
            description: "ID del canal: " + channel.id
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
                    .setCustomId("cmd:config:channels:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.channels.select)
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
                    .setCustomId("cmd:config:channels_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:channels_desselectall:" + interaction.user.id)
            ])
        ]
    })
}

