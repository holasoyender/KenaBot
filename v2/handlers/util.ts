/*
 * File:          util.ts
 * Path:          v2\handlers\util.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 92fd232ba79ebed9b4b764dc8d9420e09d2c5af3d264c1b8140ccc8b74a69432
 *
 */


import {ButtonInteraction, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import Guild from "../database/schema/Guild";
import {inArray} from "../utils/chunk";

export const handleBack = async(interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {
    const embed = new MessageEmbed()
        // @ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.title)
        .setDescription(guildLang.commands.config.description.replace("%s%", `${interaction.client.user?.id}`))

    const langButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.lang)
        .setEmoji(guildLang.flag)
        .setCustomId("cmd:config:lang:" + interaction.user.id)

    const channelsButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.channels)
        .setEmoji("<:channels:940254447530016859>")
        .setCustomId("cmd:config:channels:" + interaction.user.id)

    const djButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.dj)
        .setEmoji("<:mention:940268589615812608>")
        .setCustomId("cmd:config:dj:" + interaction.user.id)

    const commandsButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.commands)
        .setEmoji("<:slash:941024012270710874>")
        .setCustomId("cmd:config:commands:"+interaction.user.id)

    const premiumButton = new MessageButton()
        .setStyle(guildConfig.premium.enabled ? "PRIMARY" : "DANGER")
        .setLabel(guildConfig.premium.enabled ? guildLang.commands.config.buttons.premium_on : guildLang.commands.config.buttons.premium_off)
        .setCustomId("cmd:config:premium:"+interaction.user.id)

    const moreButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.more)
        .setCustomId("cmd:config:more:" + interaction.user.id)

    await interaction.update({
        embeds: [embed],
        components: [new MessageActionRow()
            .addComponents([
                langButton,
                channelsButton,
                djButton,

                moreButton
            ]),
            new MessageActionRow()
                .addComponents([
                    commandsButton,
                    premiumButton
                ])]
    })
}

export const handlePremium = async(interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    if(guildConfig.premium.enabled) {

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(guildConfig.color)
            .setTitle(guildLang.commands.config.premium_on.title)

        // @ts-ignore
        if(guildConfig.premium.expires == "Forever")
            embed.setDescription(guildLang.commands.config.premium_on.description_forever)
        else
            embed.setDescription(guildLang.commands.config.premium_on.description.replace("%s%", Math.round(guildConfig.premium.expires/1000).toString()))

        await interaction.reply({
            embeds: [
                embed
            ],
            ephemeral: true,
        })
    }else {
        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(guildConfig.color)
            .setTitle(guildLang.commands.config.premium_off.title)
            .setDescription(guildLang.commands.config.premium_off.description)

        await interaction.reply({
            embeds: [
                embed
            ],
            ephemeral: true,
        })
    }
}

export const handleMore = async(interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const backButton = new MessageButton()
        .setLabel(guildLang.commands.config.buttons.back)
        .setStyle("SECONDARY")
        .setEmoji("<:left:940316141983764494>")
        .setCustomId("cmd:config:back:" + interaction.user.id)

    const voiceButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.voice_channels)
        .setEmoji("<:vocielock:940653745556037643>")
        .setCustomId("cmd:config:voicechannel:" + interaction.user.id)

    const betaButton = new MessageButton()
        .setStyle(guildConfig.beta ? "SUCCESS" : "DANGER")
        .setLabel(guildConfig.beta ? guildLang.commands.config.buttons.beta_on : guildLang.commands.config.buttons.beta_off)
        .setCustomId("cmd:config:beta:" + interaction.user.id)

    const colorButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.embed)
        .setCustomId("cmd:config:embed:" + interaction.user.id)

    const guildDB = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.database)
        .setCustomId("cmd:config:guilddb:" + interaction.user.id)

    await interaction.update({
        components: [
            new MessageActionRow()
                .addComponents([
                    voiceButton,
                    betaButton,
                    guildDB
                ]),
            new MessageActionRow()
                .addComponents([
                    backButton,
                    colorButton,
                ])
        ]
    });
}

export const handleBeta = async(interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    if(!guildConfig.premium.enabled) {
        return interaction.reply({
            content: guildLang.manager.no_premium,
            ephemeral: true
        });
    }

    const guild = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            beta: true,
        });
        // @ts-ignore
        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    } else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            beta: !guildConfig.beta
        });
        // @ts-ignore
        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    }

    const backButton = new MessageButton()
        .setLabel(guildLang.commands.config.buttons.back)
        .setStyle("SECONDARY")
        .setEmoji("<:left:940316141983764494>")
        .setCustomId("cmd:config:back:" + interaction.user.id)

    const voiceButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.voice_channels)
        .setEmoji("<:vocielock:940653745556037643>")
        .setCustomId("cmd:config:voicechannel:" + interaction.user.id)

    const betaButton = new MessageButton()
        .setStyle(guildConfig.beta ? "SUCCESS" : "DANGER")
        .setLabel(guildConfig.beta ? guildLang.commands.config.buttons.beta_on : guildLang.commands.config.buttons.beta_off)
        .setCustomId("cmd:config:beta:" + interaction.user.id)

    const colorButton = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.embed)
        .setCustomId("cmd:config:embed:" + interaction.user.id)

    const guildDB = new MessageButton()
        .setStyle("SECONDARY")
        .setLabel(guildLang.commands.config.buttons.database)
        .setCustomId("cmd:config:guilddb:" + interaction.user.id)

    await interaction.update({
        components: [
            new MessageActionRow()
                .addComponents([
                    voiceButton,
                    betaButton,
                    guildDB
                ]),
            new MessageActionRow()
                .addComponents([
                    backButton,
                    colorButton,
                ])
        ]
    });
}

export const handleGuildDB = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES) => {

    const guild:any = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await interaction.reply({
            embeds: [
                new MessageEmbed()
                    // @ts-ignore
                    .setColor(guildConfig.color)
                    .setDescription(guildLang.manager.no_db),
            ],
            ephemeral: true
        })
        return;
    }

    if(guild.disabled.channels.length >= 20) {
        guild.disabled.channels = guild.disabled.channels.slice(0, 25);
        // @ts-ignore
        guild.disabled.channels.push("...")
    }

    const embed = new MessageEmbed()
        // @ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.database.title)
        .addField(guildLang.commands.config.database.raw, "```" + JSON.stringify(guild).replace("_id", "cluster").replace("__v", "version") + "```")

    if(JSON.stringify(guild, null, 2).length < 1024)
        embed.addField(guildLang.commands.config.database.read, "```json\n" + JSON.stringify(guild.toJSON(), null, 2).replace("_id", "cluster").replace("__v", "version") + "```")

    embed.fields.forEach(field => {
        if(field.value.length > 1024)
            field.value = field.value.slice(0, 1015) + "...```"
    })

    await interaction.reply({
        embeds: [
            embed,
        ],
        components: [
            new MessageActionRow()
                .addComponents([
                    new MessageButton()
                        .setStyle("SECONDARY")
                        .setLabel(guildLang.commands.config.database.delete)
                        .setCustomId("cmd:config:delete:" + interaction.user.id),
                    new MessageButton()
                        .setStyle("LINK")
                        .setLabel(guildLang.commands.config.database.privacy)
                        .setURL("https://kenabot.xyz/cartas"),
                ]),
        ],
        ephemeral: true
    })
}

export const handleGuildDBDelete = async (interaction: ButtonInteraction,  guildConfig:typeof Default | any, guildLang:typeof esES, type:string) => {

    let embed;
    switch (type) {

        case "delete":

            // @ts-ignore
            if (!interaction.member.permissions.has("ADMINISTRATOR")) {
                // @ts-ignore
                if (!inArray(interaction.client.develop.owners, interaction.user.id))
                    return interaction.reply({
                        content: guildLang.manager.no_perms,
                        ephemeral: true
                    });
            }

            if(guildConfig.premium.enabled && guildConfig.premium.expires == "Forever") {
                return interaction.reply({
                    content: guildLang.manager.premium_forever,
                    ephemeral: true
                });
            }
            if(guildConfig.blocked.enabled) {
                return interaction.reply({
                    content: guildLang.manager.blocked_server.replace("%s%", guildConfig.blocked.reason),
                    ephemeral: true
                });
            }

            embed = new MessageEmbed()
                //@ts-ignore
                .setColor(guildConfig.color)
                .setDescription(guildLang.manager.delete_confirm)

            await interaction.update({
                embeds: [
                    embed,
                ],
                components: [
                    new MessageActionRow()
                        .addComponents([
                            new MessageButton()
                                .setStyle("DANGER")
                                .setLabel(guildLang.commands.config.database.confirm)
                                .setCustomId("cmd:config:delete_confirm:" + interaction.user.id),
                            new MessageButton()
                                .setStyle("SECONDARY")
                                .setLabel(guildLang.commands.config.database.cancel)
                                .setCustomId("cmd:config:delete_cancel:" + interaction.user.id),
                        ]),
                ],
            })
            break;

        case "cancel":

            embed = new MessageEmbed()
                //@ts-ignore
                .setColor(guildConfig.color)
                .setDescription(guildLang.manager.delete_cancel)

            await interaction.update({
                embeds: [
                    embed,
                ],
                components: [],
            })
            break;

        case "confirm":

            await Guild.findOneAndDelete({
                guildID: interaction.guild?.id
            })

            embed = new MessageEmbed()
                //@ts-ignore
                .setColor(guildConfig.color)
                .setDescription(guildLang.manager.deleted)

            await interaction.update({
                embeds: [
                    embed,
                ],
                components: [],
            })
            break;
    }

}