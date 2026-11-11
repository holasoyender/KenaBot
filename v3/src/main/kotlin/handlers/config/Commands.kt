/*
 * File:          Commands.kt
 * Path:          v3\src\main\kotlin\handlers\config\Commands.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: bc703f8221dff4c10e11ed9840e48baa6b44e2c400d41340ff0b43458236816a
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import managers.CommandManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.json.JSONObject

object Commands {

    private val manager: CommandManager = CommandManager.instance!!

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.commands.title"))
            .setDescription(lang.get("commands.config.commands.description"))

        val commands: ArrayList<JSONObject> = ArrayList()

        manager.commands.forEach { command ->

            if (command.premium == true && !config.premiumEnabled) return@forEach

            var emoji = Emoji.fromCustom(CustomEmojiImpl("slash", 941024012270710874, false))
            if (config.disabledCommands.contains(command.name))
                emoji = Emoji.fromCustom(CustomEmojiImpl("slashsel", 941055377649319996, false))

            val locale = when (config.language) {
                "es-ES", "ca-ES" -> DiscordLocale.SPANISH
                "en-US" -> DiscordLocale.ENGLISH_US
                "fr-FR" -> DiscordLocale.FRENCH
                "pt-PT" -> DiscordLocale.PORTUGUESE_BRAZILIAN
                "it-IT" -> DiscordLocale.ITALIAN
                "de-DE" -> DiscordLocale.GERMAN
                else -> DiscordLocale.ENGLISH_US
            }
            val description = command.metadata.descriptionLocalizations[locale] ?: command.description

            val commandData = JSONObject()
                .put("label", command.name)
                .put("value", "cmd:config:disablecommand:${command.name}:${event.user.id}")
                .put("emoji", emoji)
                .put("default", config.disabledCommands.contains(command.name))
                .put("description", description)

            commands.add(commandData)
        }

        val rows = ArrayList<ActionRow>()
        commands.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:disablecommand:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.commands.select"))
                        .setMinValues(0)
                        .setMaxValues(chunk.size)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        ).build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:commands_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:commands_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )

        event.editMessageEmbeds(embed.build()).setComponents(rows).queue()
    }

    fun set(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils) {

        val commands: ArrayList<String> = ArrayList()

        event.selectedOptions.forEach {
            commands.add(it.value.split(":")[3])
        }

        event.message.actionRows.forEach { actionRow ->
            if (!actionRow.isEmpty)
                actionRow.components.forEach { menu ->
                    if (menu is StringSelectMenu && menu.id != event.selectMenu.id)
                        menu.options.forEach { option ->
                            if (option.isDefault)
                                commands.add(option.value.split(":")[3])
                        }
                }
        }

        config.disabledCommands = commands
        config.save()

        val allCommands: ArrayList<JSONObject> = ArrayList()
        manager.commands.forEach { command ->

            if (command.premium == true && !config.premiumEnabled) return@forEach

            var emoji = Emoji.fromCustom(CustomEmojiImpl("slash", 941024012270710874, false))
            if (commands.contains(command.name))
                emoji = Emoji.fromCustom(CustomEmojiImpl("slashsel", 941055377649319996, false))

            val locale = when (config.language) {
                "es-ES", "ca-ES" -> DiscordLocale.SPANISH
                "en-US" -> DiscordLocale.ENGLISH_US
                "fr-FR" -> DiscordLocale.FRENCH
                "pt-PT" -> DiscordLocale.PORTUGUESE_BRAZILIAN
                "it-IT" -> DiscordLocale.ITALIAN
                "de-DE" -> DiscordLocale.GERMAN
                else -> DiscordLocale.ENGLISH_US
            }
            val description = command.metadata.descriptionLocalizations[locale] ?: command.description

            val commandData = JSONObject()
                .put("label", command.name)
                .put("value", "cmd:config:disablecommand:${command.name}:${event.user.id}")
                .put("emoji", emoji)
                .put("default", commands.contains(command.name))
                .put("description", description)

            allCommands.add(commandData)
        }

        val rows = ArrayList<ActionRow>()
        allCommands.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:disablecommand:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.commands.select"))
                        .setMinValues(0)
                        .setMaxValues(chunk.size)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        ).build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:commands_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:commands_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )

        event.editComponents(rows).queue()
    }

    fun selectAll(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val commands: ArrayList<String> = ArrayList()
        val allCommands: ArrayList<JSONObject> = ArrayList()

        manager.commands.forEach { command ->

            if (command.premium == true && !config.premiumEnabled) return@forEach

            val locale = when (config.language) {
                "es-ES", "ca-ES" -> DiscordLocale.SPANISH
                "en-US" -> DiscordLocale.ENGLISH_US
                "fr-FR" -> DiscordLocale.FRENCH
                "pt-PT" -> DiscordLocale.PORTUGUESE_BRAZILIAN
                "it-IT" -> DiscordLocale.ITALIAN
                "de-DE" -> DiscordLocale.GERMAN
                else -> DiscordLocale.ENGLISH_US
            }
            val description = command.metadata.descriptionLocalizations[locale] ?: command.description

            val commandData = JSONObject()
                .put("label", command.name)
                .put("value", "cmd:config:disablecommand:${command.name}:${event.user.id}")
                .put("emoji", Emoji.fromCustom(CustomEmojiImpl("slashsel", 941055377649319996, false)))
                .put("default", true)
                .put("description", description)

            allCommands.add(commandData)
            commands.add(command.name)
        }

        config.disabledCommands = commands
        config.save()

        val rows = ArrayList<ActionRow>()
        allCommands.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:disablecommand:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.commands.select"))
                        .setMinValues(0)
                        .setMaxValues(chunk.size)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        ).build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:commands_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:commands_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )

        event.editComponents(rows).queue()
    }

    fun desSelectAll(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val allCommands: ArrayList<JSONObject> = ArrayList()

        manager.commands.forEach { command ->

            if (command.premium == true && !config.premiumEnabled) return@forEach

            val locale = when (config.language) {
                "es-ES", "ca-ES" -> DiscordLocale.SPANISH
                "en-US" -> DiscordLocale.ENGLISH_US
                "fr-FR" -> DiscordLocale.FRENCH
                "pt-PT" -> DiscordLocale.PORTUGUESE_BRAZILIAN
                "it-IT" -> DiscordLocale.ITALIAN
                "de-DE" -> DiscordLocale.GERMAN
                else -> DiscordLocale.ENGLISH_US
            }
            val description = command.metadata.descriptionLocalizations[locale] ?: command.description

            val commandData = JSONObject()
                .put("label", command.name)
                .put("value", "cmd:config:disablecommand:${command.name}:${event.user.id}")
                .put("emoji", Emoji.fromCustom(CustomEmojiImpl("slash", 941024012270710874, false)))
                .put("default", false)
                .put("description", description)

            allCommands.add(commandData)
        }

        config.disabledCommands = ArrayList()
        config.save()

        val rows = ArrayList<ActionRow>()
        allCommands.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:disablecommand:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.commands.select"))
                        .setMinValues(0)
                        .setMaxValues(chunk.size)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        ).build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:commands_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:commands_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )

        event.editComponents(rows).queue()
    }
}