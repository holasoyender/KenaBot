/*
 * File:          config.ts
 * Path:          v2\handlers\config.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 683e981dfeca3026e23df8e628b972d6c85f9353c163d8955e1566df859055ba
 *
 */


/* eslint-disable no-case-declarations */
import {
    ButtonInteraction,
} from "discord.js";
import Guild from "../database/schema/Guild";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {handleDefault as handleDefaultLang} from "./lang";
import {handleDefault as handleDefaultConfig, handleDeselectAll, handleSelectAll} from "./channels";
import {handleDefault as handleDefaultCommands, handleDeselectAllCommands, handleSelectAllCommands} from "./commands";
import {handleBack, handleBeta, handleGuildDB, handleGuildDBDelete, handleMore, handlePremium} from "./util";
import {handleDefault as handleDefaultEmbed} from "./embed";
import {handleDefault as handleDefaultVoiceChannels, handleDeselectAllVoice, handleSelectAllVoice} from "./voice";
import {handleDefault as handleDefaultDJRole} from "./djrole";
import client from "../main";

export default async (interaction: ButtonInteraction) => {
    const type = interaction.customId.split(":")[2];
    const db = await Guild.findOne({
        guildID: interaction.guild?.id
    })

    let guildConfig: any = db
    if (!db) guildConfig = Default;

    if (!guildConfig.lang) guildConfig.lang = "es-ES";

    const guildLang = client?.langs?.[guildConfig.lang] ?? esES;

    switch (type) {
        case "lang":
            await handleDefaultLang(interaction, guildConfig, guildLang)
            break;

        case "channels":
            await handleDefaultConfig(interaction, guildConfig, guildLang)
            break;

        case "channels_selectall":
            await handleSelectAll(interaction, guildConfig, guildLang)
            break;

        case "channels_desselectall":
            await handleDeselectAll(interaction, guildConfig, guildLang)
            break;

        case "back":
            await handleBack(interaction, guildConfig, guildLang)
            break;

        case "commands":
            await handleDefaultCommands(interaction, guildConfig, guildLang)
            break;

        case "premium":
            await handlePremium(interaction, guildConfig, guildLang)
            break;

        case "more":
            await handleMore(interaction, guildConfig, guildLang)
            break;

        case "beta":
            await handleBeta(interaction, guildConfig, guildLang)
            break;
        case "guilddb":
            await handleGuildDB(interaction, guildConfig, guildLang)
            break;

        case "delete":
            await handleGuildDBDelete(interaction, guildConfig, guildLang, "delete")
            break;

        case "delete_cancel":
            await handleGuildDBDelete(interaction, guildConfig, guildLang, "cancel")
            break;

        case "delete_confirm":
            await handleGuildDBDelete(interaction, guildConfig, guildLang, "confirm")
            break;

        case "embed":
            await handleDefaultEmbed(interaction, guildConfig, guildLang)
            break;

        case "commands_selectall":
            await handleSelectAllCommands(interaction, guildConfig, guildLang)
            break;

        case "commands_desselectall":
            await handleDeselectAllCommands(interaction, guildConfig, guildLang)
            break;

        case "voicechannel":
            await handleDefaultVoiceChannels(interaction, guildConfig, guildLang)
            break;

        case "voicechannels_selectall":
            await handleSelectAllVoice(interaction, guildConfig, guildLang)
            break;

        case "voicechannels_deselectall":
            await handleDeselectAllVoice(interaction, guildConfig, guildLang)
            break;

        case "dj":
            await handleDefaultDJRole(interaction, guildConfig, guildLang)
            break;
    }
}