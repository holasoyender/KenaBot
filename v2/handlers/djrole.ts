/*
 * File:          djrole.ts
 * Path:          v2\handlers\djrole.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-13 14:51:33 +01:00
 *
 * SHA-256: 259a66c8709c53d55d0e352b239d131081873378d43a7b555e9814c21711b4c7
 *
 */


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
import chunk from "../utils/chunk";
import Guild from "../database/schema/Guild";


export default async (interaction: SelectMenuInteraction, guildConfig:typeof Default | any, guildLang:typeof esES) => {

    if (interaction.values[0]) {
        const args = interaction.values[0].split(":");
        const id = args[3];

        const guild = await Guild.findOne({
            guildID: interaction.guild?.id
        })
        if (!guild) {
            await Guild.create({
                guildID: interaction.guild?.id,
                dj_role: id,
            });
            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        } else {
            await Guild.updateOne({
                guildID: interaction.guild?.id
            }, {
                dj_role: id,
            });

            guildConfig = await Guild.findOne({
                guildID: interaction.guild?.id
            })
        }

        const roles: Array<any> = []
        interaction.guild?.roles.cache.forEach(role => {

            if (role.name == "@everyone") return;
            roles.push({
                label: role.name,
                value: `cmd:config:setdj:${role.id}:${interaction.user.id}`,
                emoji: guildConfig.dj_role === role.id ? "<:mentionsel:941367034128760942>" : "<:mention:940268589615812608>",
                default: guildConfig.dj_role === role.id,
            })
        })

        if (roles.length > 100) {
            roles.splice(100, roles.length - 100)
        }

        const rows: Array<any> = []
        chunk(roles, 25).forEach(chunk => {
            const random = Math.floor(Math.random() * 100000)

            rows.push(
                new MessageActionRow().addComponents([
                    new MessageSelectMenu()
                        .setCustomId("cmd:config:djrole:select:" + interaction.user.id + ":" + random)
                        .setPlaceholder(guildLang.commands.config.djrole.select)
                        .addOptions(chunk)
                        .setMaxValues(1)
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
                        .setCustomId("cmd:config:back:" + interaction.user.id)
                ])
            ]
        })
    } else {

        const args = interaction.customId.split(":")

        if (args[2] == "djrole" && args[3] == "select") {
            const guild = await Guild.findOne({
                guildID: interaction.guild?.id
            })
            if (!guild) {
                await Guild.create({
                    guildID: interaction.guild?.id,
                    dj_role: "",
                });
            } else {
                await Guild.updateOne({
                    guildID: interaction.guild?.id
                }, {
                    dj_role: "",
                });
            }

            const roles: Array<any> = []
            interaction.guild?.roles.cache.forEach(role => {

                if (role.name == "@everyone") return;
                roles.push({
                    label: role.name,
                    value: `cmd:config:setdj:${role.id}:${interaction.user.id}`,
                    emoji: "<:mention:940268589615812608>",
                    default: false,
                })
            })

            if (roles.length > 100) {
                roles.splice(100, roles.length - 100)
            }

            const rows: Array<any> = []
            chunk(roles, 25).forEach(chunk => {
                const random = Math.floor(Math.random() * 100000)

                rows.push(
                    new MessageActionRow().addComponents([
                        new MessageSelectMenu()
                            .setCustomId("cmd:config:djrole:select:" + interaction.user.id + ":" + random)
                            .setPlaceholder(guildLang.commands.config.djrole.select)
                            .addOptions(chunk)
                            .setMaxValues(1)
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
                            .setCustomId("cmd:config:back:" + interaction.user.id)
                    ])
                ]
            })
        }
    }
}

export const handleDefault = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {
    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.djrole.title)
        .setDescription(guildLang.commands.config.djrole.description)

    const roles:Array<any> = []
    interaction.guild?.roles.cache.forEach(role => {

        if(role.name == "@everyone") return;
        roles.push({
            label: role.name,
            value: `cmd:config:setdj:${role.id}:${interaction.user.id}`,
            emoji: guildConfig.dj_role === role.id ? "<:mentionsel:941367034128760942>" : "<:mention:940268589615812608>",
            default: guildConfig.dj_role === role.id,
        })
    })

    if (roles.length > 100) {
        roles.splice(100, roles.length - 100)
    }

    const rows: Array<any> = []
    chunk(roles, 25).forEach(chunk => {
        const random = Math.floor(Math.random() * 100000)

        rows.push(
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:djrole:select:" + interaction.user.id + ":" + random)
                    .setPlaceholder(guildLang.commands.config.djrole.select)
                    .addOptions(chunk)
                    .setMaxValues(1)
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
                    .setCustomId("cmd:config:back:" + interaction.user.id)
            ])
        ]
    })
}