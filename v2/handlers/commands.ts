/*
 * File:          commands.ts
 * Path:          v2\handlers\commands.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 16535b22dc93176d5e5896ba04d47649feffa66bb75accdbc24eb22d45fbdde1
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
        const commandsraw: Array<string> = []

        interaction.values.forEach(value => {
            commandsraw.push(value.split(":")[3])
        })

        const guild:any = await Guild.findOne({
            guildID: interaction.guild?.id
        })
        if (!guild) {
            await Guild.create({
                guildID: interaction.guild?.id,
                disabled: {
                    commands: commandsraw
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
                    commands: commandsraw,
                    voice: guild.disabled.voice,
                },
            });

            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        }

        const commands: Array<any> = []

        // @ts-ignore
        interaction.client.Commands.forEach((command: any) => {

            if (command.premium && !guildConfig.premium.enabled) return;

            commands.push({
                label: command.name,
                value: `cmd:config:disablecommand:${command.name}:${interaction.user.id}`,
                emoji: inArray(guildConfig.disabled.commands, command.name) ? "<:slashsel:941055377649319996>" : "<:slash:941024012270710874>",
                default: inArray(guildConfig.disabled.commands, command.name),
                description: command.desc
            })
        })

        const rows: Array<any> = []
        chunk(commands, 25).forEach(chunk => {
            const random = Math.floor(Math.random() * 100000)

            rows.push(
                new MessageActionRow().addComponents([
                    new MessageSelectMenu()
                        .setCustomId("cmd:config:commands:select:" + interaction.user.id + ":" + random)
                        .setPlaceholder(guildLang.commands.config.commands.select)
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
                        .setCustomId("cmd:config:commands_selectall:" + interaction.user.id),

                    new MessageButton()
                        .setStyle("SUCCESS")
                        .setLabel(guildLang.commands.config.buttons.remove_all)
                        .setCustomId("cmd:config:commands_desselectall:" + interaction.user.id)
                ])
            ]
        })

    } else {

        const args = interaction.customId.split(":")

        if (args[2] === "commands") {
            switch (args[3]) {

                case "select":
                    const guild:any = await Guild.findOne({
                        guildID: interaction.guild?.id
                    })
                    if (!guild) {
                        await Guild.create({
                            guildID: interaction.guild?.id,
                            disabled: {
                                commands: []
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
                                commands: [],
                                voice: guild.disabled.voice,
                            },
                        });
                        guildConfig = await Guild.findOne({
                            guildID: interaction.guild?.id
                        })
                    }

                    const commands: Array<any> = []

                    // @ts-ignore
                    interaction.client.Commands.forEach((command: any) => {

                        if (command.premium && !guildConfig.premium.enabled) return;

                        commands.push({
                            label: command.name,
                            value: `cmd:config:disablecommand:${command.name}:${interaction.user.id}`,
                            emoji: inArray(guildConfig.disabled.commands, command.name) ? "<:slashsel:941055377649319996>" : "<:slash:941024012270710874>",
                            default: inArray(guildConfig.disabled.commands, command.name),
                            description: command.desc
                        })
                    })

                    const rows: Array<any> = []
                    chunk(commands, 25).forEach(chunk => {
                        const random = Math.floor(Math.random() * 100000)

                        rows.push(
                            new MessageActionRow().addComponents([
                                new MessageSelectMenu()
                                    .setCustomId("cmd:config:commands:select:" + interaction.user.id + ":" + random)
                                    .setPlaceholder(guildLang.commands.config.commands.select)
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
                                    .setCustomId("cmd:config:commands_selectall:" + interaction.user.id),

                                new MessageButton()
                                    .setStyle("SUCCESS")
                                    .setLabel(guildLang.commands.config.buttons.remove_all)
                                    .setCustomId("cmd:config:commands_desselectall:" + interaction.user.id)
                            ])
                        ]
                    })

                    break;
            }
        }
    }
};

export const handleDefault = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.commands.title)
        .setDescription(guildLang.commands.config.commands.description)

    const commands: Array<any> = []

    // @ts-ignore
    interaction.client.Commands.forEach((command: any) => {

        if(command.premium && !guildConfig.premium.enabled) return;

        commands.push({
            label: command.name,
            value: `cmd:config:disablecommand:${command.name}:${interaction.user.id}`,
            emoji: inArray(guildConfig.disabled.commands, command.name) ? "<:slashsel:941055377649319996>" : "<:slash:941024012270710874>",
            default: inArray(guildConfig.disabled.commands, command.name),
            description: command.desc
        })
    })

    const rows: Array<any> = []
    chunk(commands, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:commands:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.commands.select)
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
                    .setCustomId("cmd:config:commands_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:commands_desselectall:" + interaction.user.id)
            ])
        ]
    })
}

export const handleSelectAllCommands = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const commandsraw: Array<string> = []

    // @ts-ignore
    interaction.client.Commands.forEach((command: any) => {

        if (command.premium && !guildConfig.premium.enabled) return;

        commandsraw.push(command.name)
    })

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                commands: commandsraw
            },
        });
    } else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: guild.disabled.channels,
                commands: commandsraw,
                voice: guild.disabled.voice,
            },
        });
    }

    const commands: Array<any> = []

    // @ts-ignore
    interaction.client.Commands.forEach((command: any) => {

        if (command.premium && !guildConfig.premium.enabled) return;

        commands.push({
            label: command.name,
            value: `cmd:config:disablecommand:${command.name}:${interaction.user.id}`,
            emoji: "<:slashsel:941055377649319996>",
            default: true,
            description: command.desc
        })
    })

    const rows: Array<any> = []
    chunk(commands, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:commands:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.commands.select)
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
                    .setCustomId("cmd:config:commands_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:commands_desselectall:" + interaction.user.id)
            ])
        ]
    })
}

export const handleDeselectAllCommands = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            disabled: {
                commands: []
            },
        });
    } else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            disabled: {
                channels: guild.disabled.channels,
                commands: [],
                voice: guild.disabled.voice,
            },
        });
    }

    const commands: Array<any> = []

    // @ts-ignore
    interaction.client.Commands.forEach((command: any) => {

        if (command.premium && !guildConfig.premium.enabled) return;

        commands.push({
            label: command.name,
            value: `cmd:config:disablecommand:${command.name}:${interaction.user.id}`,
            emoji: "<:slash:941024012270710874>",
            default: false,
            description: command.desc
        })
    })

    const rows: Array<any> = []
    chunk(commands, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:commands:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.commands.select)
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
                    .setCustomId("cmd:config:commands_selectall:" + interaction.user.id),

                new MessageButton()
                    .setStyle("SUCCESS")
                    .setLabel(guildLang.commands.config.buttons.remove_all)
                    .setCustomId("cmd:config:commands_desselectall:" + interaction.user.id)
            ])
        ]
    })
}
