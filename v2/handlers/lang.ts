/*
 * File:          lang.ts
 * Path:          v2\handlers\lang.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-09 20:11:49 +01:00
 *
 * SHA-256: a846e6eae328967a21d8aa522902550bf7ba5a30732d16e499d47ac0be09a2d6
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
import fs from "fs";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import main from "../main";

export default async (interaction: SelectMenuInteraction, guildConfig:typeof Default | any, guildLang:typeof esES) => {
    const args = interaction.values[0].split(":");
    const lang = args[3];

    const guild = await Guild.findOne({
        guildID: interaction.guild?.id
    })
    if (!guild) {
        await Guild.create({
            guildID: interaction.guild?.id,
            lang: lang,
        });
        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    } else {
        await Guild.updateOne({
            guildID: interaction.guild?.id
        }, {
            lang: lang
        });

        guildConfig = await Guild.findOne({
            guildID: interaction.guild?.id
        })
    }

    let str = "./";
    if(main?.env == "prod") {
        str ="./dist/";
    }

    const langs: Array<any> = []
    const files = fs.readdirSync(str + "langs");

    files.forEach(file => {
        if (file.endsWith(".js") || file.endsWith(".ts")) {
            // eslint-disable-next-line @typescript-eslint/no-var-requires
            const lang = main?.langs[file.split(".")[0]]

            langs.push({
                label: lang?.name,
                value: `cmd:config:setlang:${file.split(".")[0]}:${interaction.user.id}`,
                emoji: lang?.flag,
                default: guildConfig.lang === file.split(".")[0],
                description: lang?.description
            })
        }
    });

    await interaction.update({
        components: [
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:lang:select:" + interaction.user.id)
                    .setPlaceholder(guildLang.commands.config.lang.select)
                    .addOptions(langs)
                    .setMaxValues(1)
                    .setMinValues(1)
            ]),
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

export const handleDefault = async (interaction:ButtonInteraction, guildConfig:typeof Default, guildLang:typeof esES) => {
    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guildConfig.color)
        .setTitle(guildLang.commands.config.lang.title)
        .setDescription(guildLang.commands.config.lang.description)

    let str = "./";
    if (main?.env == "prod") {
        str = "./dist/";
    }

    const langs: Array<any> = []
    const files = fs.readdirSync(str + "langs");

    files.forEach(file => {
        if (file.endsWith(".js") || file.endsWith(".ts")) {
            // eslint-disable-next-line @typescript-eslint/no-var-requires
            const lang = main?.langs[file.split(".")[0]]

            langs.push({
                label: lang?.name,
                value: `cmd:config:setlang:${file.split(".")[0]}:${interaction.user.id}`,
                emoji: lang?.flag,
                default: guildConfig.lang === file.split(".")[0],
                description: lang?.description
            })
        }
    });

    await interaction.update({
        embeds: [embed],
        components: [
            new MessageActionRow().addComponents([
                new MessageSelectMenu()
                    .setCustomId("cmd:config:lang:select:" + interaction.user.id)
                    .setPlaceholder(guildLang.commands.config.lang.select)
                    .addOptions(langs)
                    .setMaxValues(1)
                    .setMinValues(1)
            ]),
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