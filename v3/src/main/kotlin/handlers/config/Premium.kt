/*
 * File:          Premium.kt
 * Path:          v3\src\main\kotlin\handlers\config\Premium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 0c5f840dcea2d16192ed9910cc6e09a23b998e21b4e7c67b8d810a10ef3ff1dd
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent

object Premium {

    fun click(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        if(config.premiumEnabled) {
            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setTitle(lang.get("commands.config.premium_on.title"))

            if(config.premiumExpires == "Forever")
                embed.setDescription(lang.get("commands.config.premium_on.description_forever"))
            else
                embed.setDescription(lang.get("commands.config.premium_on.description").replace("%s%", (config.premiumExpires as Long / 1000).toString()))

            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        } else {
            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setTitle(lang.get("commands.config.premium_off.title"))
                .setDescription(lang.get("commands.config.premium_off.description"))

            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
        }
    }
}