/*
 * File:          Embeds.kt
 * Path:          v3\src\main\kotlin\handlers\config\Embeds.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: e70b80b001bf1178ba99b3d9d679d5da4446671f07a3a56bb8215b2b60eb5daf
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import java.awt.Color


object Embeds {

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.embed.title"))
            .setDescription(lang.get("commands.config.embed.description"))

        event.editMessageEmbeds(embed.build()).setComponents(
            ActionRow.of(
                StringSelectMenu.create("cmd:config:setcolor:${event.user.id}")
                    .setPlaceholder(lang.get("commands.config.embed.select"))
                    .setMinValues(0)
                    .setMaxValues(1)
                    .addOptions(
                        SelectOption.of("Default", "cmd:config:setcolor:#2f3136:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("default", 940708160199606292, false)))
                            .withDefault(colorString(config.embedColor) == "#2f3136"),
                        SelectOption.of("Blurple", "cmd:config:setcolor:#5865F2:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("blurple", 940708160237355048, false)))
                            .withDefault(colorString(config.embedColor) == "#5865F2"),
                        SelectOption.of("Red", "cmd:config:setcolor:#ED4245:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("red", 940708161726324766, false)))
                            .withDefault(colorString(config.embedColor) == "#ED4245"),
                        SelectOption.of("Green", "cmd:config:setcolor:#57F287:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("green", 940708160161845289, false)))
                            .withDefault(colorString(config.embedColor) == "#57F287"),
                        SelectOption.of("Yellow", "cmd:config:setcolor:#FEE75C:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("yellow", 940708160312836137, false)))
                            .withDefault(colorString(config.embedColor) == "#FEE75C"),
                        SelectOption.of("Purple", "cmd:config:setcolor:#a443f0:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("purple", 966365085444481104, false)))
                            .withDefault(colorString(config.embedColor) == "#a443f0")
                    ).build()
            ),
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    )
            )
        ).queue()
    }

    fun set(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils) {

        if(event.selectedOptions.isEmpty()) {
            event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
            return
        }

        val args = event.selectedOptions.first().value.split(":")
        val newColor = args[3]

        config.embedColor = Color.decode(newColor)
        config.save()

        val embed = EmbedBuilder()
            .setColor(Color.decode(newColor))
            .setTitle(lang.get("commands.config.embed.title"))
            .setDescription(lang.get("commands.config.embed.description"))

        event.editMessageEmbeds(embed.build()).setComponents(
            ActionRow.of(
                StringSelectMenu.create("cmd:config:setcolor:${event.user.id}")
                    .setPlaceholder(lang.get("commands.config.embed.select"))
                    .setMinValues(0)
                    .setMaxValues(1)
                    .addOptions(
                        SelectOption.of("Default", "cmd:config:setcolor:#2f3136:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("default", 940708160199606292, false)))
                            .withDefault(newColor == "#2f3136"),
                        SelectOption.of("Blurple", "cmd:config:setcolor:#5865F2:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("blurple", 940708160237355048, false)))
                            .withDefault(newColor == "#5865F2"),
                        SelectOption.of("Red", "cmd:config:setcolor:#ED4245:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("red", 940708161726324766, false)))
                            .withDefault(newColor == "#ED4245"),
                        SelectOption.of("Green", "cmd:config:setcolor:#57F287:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("green", 940708160161845289, false)))
                            .withDefault(newColor == "#57F287"),
                        SelectOption.of("Yellow", "cmd:config:setcolor:#FEE75C:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("yellow", 940708160312836137, false)))
                            .withDefault(newColor == "#FEE75C"),
                        SelectOption.of("Purple", "cmd:config:setcolor:#a443f0:${event.user.id}")
                            .withEmoji(Emoji.fromCustom(CustomEmojiImpl("purple", 966365085444481104, false)))
                            .withDefault(newColor == "#a443f0")
                    ).build()
            ),
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    )
            )
        ).queue()
    }

    private fun colorString(color: Color): String {
        val red = Integer.toHexString(color.red)
        val green = Integer.toHexString(color.green)
        val blue = Integer.toHexString(color.blue)
        return "#" +
                (if (red.length == 1) "0$red" else red) +
                (if (green.length == 1) "0$green" else green) +
                if (blue.length == 1) "0$blue" else blue
    }
}