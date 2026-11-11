/*
 * File:          Lang.kt
 * Path:          v3\src\main\kotlin\handlers\config\Lang.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: b4600a3ee33add1f2ef82842fb48ccde1ef618a0b616dfd111c97d9131082c78
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import langs.LangManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.json.JSONObject

object Lang {

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.lang.title"))
            .setDescription(lang.get("commands.config.lang.description"))

        val options: ArrayList<JSONObject> = ArrayList()

        LangManager.allLangs().keys.forEach {
            val that = LangUtils(it)
            val langData = JSONObject()
                .put("label", that.get("name"))
                .put("value", "cmd:config:setlang:${it}:${event.user.id}")
                .put("emoji", that.get("flag"))
                .put("default", that.get("name") == lang.get("name"))
                .put("description", that.get("description"))

            options.add(langData)
        }

        val guau = options.find { it.getString("label").lowercase() == "guau guau" }
        if(guau != null) {
            options.remove(guau)
            options.add(guau)
        }

        event.editMessageEmbeds(embed.build()).setComponents(
            ActionRow.of(
                StringSelectMenu.create("cmd:config:setlang:${event.user.id}")
                    .setMaxValues(1)
                    .setMinValues(1)
                    .setPlaceholder(lang.get("commands.config.lang.select"))
                    .addOptions(
                        options.map {
                            SelectOption.of(it.getString("label"), it.getString("value"))
                                .withEmoji(Emoji.fromUnicode(it.getString("emoji")))
                                .withDefault(it.getBoolean("default"))
                                .withDescription(it.getString("description"))
                        }
                ).build()
            ),
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
            )
        ).queue()
    }

    fun set(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils) {

        if(event.selectedOptions.isEmpty()) {
            event.reply(":no_entry_sign:").setEphemeral(true).queue()
            return
        }

        val args = event.selectedOptions.first().value.split(":")
        val newLang = args[3]

        val exist = LangManager.allLangs()[newLang] != null
        if(!exist) {
            event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
            return
        }

        config.language = newLang
        config.save()

        val options: ArrayList<JSONObject> = ArrayList()
        LangManager.allLangs().keys.forEach {
            val that = LangUtils(it)
            val langData = JSONObject()
                .put("label", that.get("name"))
                .put("value", "cmd:config:setlang:${it}:${event.user.id}")
                .put("emoji", that.get("flag"))
                .put("default", it == newLang)
                .put("description", that.get("description"))

            options.add(langData)
        }

        event.editComponents(
            ActionRow.of(
                StringSelectMenu.create("cmd:config:setlang:${event.user.id}")
                    .setMaxValues(1)
                    .setMinValues(1)
                    .setPlaceholder(lang.get("commands.config.lang.select"))
                    .addOptions(
                        options.map {
                            SelectOption.of(it.getString("label"), it.getString("value"))
                                .withEmoji(Emoji.fromUnicode(it.getString("emoji")))
                                .withDefault(it.getBoolean("default"))
                                .withDescription(it.getString("description"))
                        }
                    ).build()
            ),
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
            )
        ).queue()

    }
}