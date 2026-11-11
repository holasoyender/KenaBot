/*
 * File:          embed.ts
 * Path:          v2\handlers\embed.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-04-20 22:11:17 +02:00
 *
 * SHA-256: ab568b480a9aed930418307d7a99325206faa43038e291d13ce695ce27a4247f
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

export default async (interaction: SelectMenuInteraction, guildConfig: typeof Default | any, guildLang: typeof esES) => {

    const args = interaction.values[0].split(":");
    const color = args[3];

    const guild = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            color,
        });
        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    } else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            color
        });

        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    }


    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setDescription(guildLang.commands.config.embed.description)
        .setTitle(guildLang.commands.config.embed.title)

    await interaction.update({
        embeds: [
            embed,
        ],
        components: [
            new MessageActionRow()
                .addComponents([
                    new MessageSelectMenu()
                        .setPlaceholder(guildLang.commands.config.embed.select)
                        .setCustomId("cmd:config:embed:select:" + interaction.user.id)
                        .setMinValues(1)
                        .setMaxValues(1)
                        .addOptions([
                            {
                                label: "Default",
                                value: `cmd:config:setcolor:#2f3136:${interaction.user.id}`,
                                emoji: "<:default:940708160199606292>",
                                default: guildConfig.color === "#2f3136",
                            },
                            {
                                label: "Blurple",
                                value: `cmd:config:setcolor:#5865F2:${interaction.user.id}`,
                                emoji: "<:blurple:940708160237355048>",
                                default: guildConfig.color === "#5865F2",
                            },
                            {
                                label: "Red",
                                value: `cmd:config:setcolor:#ED4245:${interaction.user.id}`,
                                emoji: "<:red:940708161726324766>",
                                default: guildConfig.color === "#ED4245",
                            },
                            {
                                label: "Green",
                                value: `cmd:config:setcolor:#57F287:${interaction.user.id}`,
                                emoji: "<:green:940708160161845289>",
                                default: guildConfig.color === "#57F287",
                            },
                            {
                                label: "Yellow",
                                value: `cmd:config:setcolor:#FEE75C:${interaction.user.id}`,
                                emoji: "<:yellow:940708160312836137>",
                                default: guildConfig.color === "#FEE75C",
                            },
                            {
                                label: "Purple",
                                value: `cmd:config:setcolor:#a443f0:${interaction.user.id}`,
                                emoji: "<:purple:966365085444481104>",
                                default: guildConfig.color === "#a443f0",
                            }
                        ])
                ]),
            new MessageActionRow().addComponents([
                new MessageButton()
                    .setLabel(guildLang.commands.config.buttons.back)
                    .setStyle("SECONDARY")
                    .setEmoji("<:left:940316141983764494>")
                    .setCustomId("cmd:config:back:" + interaction.user.id)
            ])
        ],
    })
}

export const handleDefault = async (interaction: ButtonInteraction, guildConfig: typeof Default | any, guildLang: typeof esES) => {


    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setDescription(guildLang.commands.config.embed.description)
        .setTitle(guildLang.commands.config.embed.title)

    await interaction.update({
        embeds: [
            embed,
        ],
        components: [
            new MessageActionRow()
                .addComponents([
                    new MessageSelectMenu()
                        .setPlaceholder(guildLang.commands.config.embed.select)
                        .setCustomId("cmd:config:embed:select:" + interaction.user.id)
                        .setMinValues(1)
                        .setMaxValues(1)
                        .addOptions([
                            {
                                label: "Default",
                                value: `cmd:config:setcolor:#2f3136:${interaction.user.id}`,
                                emoji: "<:default:940708160199606292>",
                                default: guildConfig.color === "#2f3136",
                            },
                            {
                                label: "Blurple",
                                value: `cmd:config:setcolor:#5865F2:${interaction.user.id}`,
                                emoji: "<:blurple:940708160237355048>",
                                default: guildConfig.color === "#5865F2",
                            },
                            {
                                label: "Red",
                                value: `cmd:config:setcolor:#ED4245:${interaction.user.id}`,
                                emoji: "<:red:940708161726324766>",
                                default: guildConfig.color === "#ED4245",
                            },
                            {
                                label: "Green",
                                value: `cmd:config:setcolor:#57F287:${interaction.user.id}`,
                                emoji: "<:green:940708160161845289>",
                                default: guildConfig.color === "#57F287",
                            },
                            {
                                label: "Yellow",
                                value: `cmd:config:setcolor:#FEE75C:${interaction.user.id}`,
                                emoji: "<:yellow:940708160312836137>",
                                default: guildConfig.color === "#FEE75C",
                            },
                            {
                                label: "Purple",
                                value: `cmd:config:setcolor:#a443f0:${interaction.user.id}`,
                                emoji: "<:purple:966365085444481104>",
                                default: guildConfig.color === "#a443f0",
                            }
                        ])
                ]),
            new MessageActionRow().addComponents([
                new MessageButton()
                    .setLabel(guildLang.commands.config.buttons.back)
                    .setStyle("SECONDARY")
                    .setEmoji("<:left:940316141983764494>")
                    .setCustomId("cmd:config:back:" + interaction.user.id)
            ])
        ],
    })
}