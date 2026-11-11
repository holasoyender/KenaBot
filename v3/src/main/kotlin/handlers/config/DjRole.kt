/*
 * File:          DjRole.kt
 * Path:          v3\src\main\kotlin\handlers\config\DjRole.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: cee86e63026cdacee308e24c9c7f254ff48015932643888052a44e000c0d0a68
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
import org.json.JSONObject
import utils.Constants

object DjRole {

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.djrole.title"))
            .setDescription(lang.get("commands.config.djrole.description"))

        val roles: ArrayList<JSONObject> = ArrayList()

        event.guild!!.roles.forEach { role ->
            if (role.name != "@everyone") {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("mention", 940268589615812608, false))
                if (config.djRoleID == role.id)
                    emoji = Emoji.fromCustom(CustomEmojiImpl("mentionsel", 941367034128760942, false))

                val roleData = JSONObject()
                    .put("label", role.name)
                    .put("value", "cmd:config:setdj:${role.id}:${event.user.id}")
                    .put("emoji", emoji)
                    .put("default", config.djRoleID == role.id)

                roles.add(roleData)
            }
        }

        if (roles.size > 100)
            roles.subList(100, roles.size).clear()

        val rows = ArrayList<ActionRow>()
        roles.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setdj:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.djrole.select"))
                        .setMaxValues(1)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
            )
        )
        event.editMessageEmbeds(embed.build()).setComponents(rows).queue()
    }

    fun set(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils) {

        if(event.selectedOptions.isEmpty()) {
            val ids: ArrayList<String> = ArrayList()
            val roles: ArrayList<JSONObject> = ArrayList()

            event.guild!!.roles.forEach { role ->
                if (role.name != "@everyone") {
                    val roleData = JSONObject()
                        .put("label", role.name)
                        .put("value", "cmd:config:setdj:${role.id}:${event.user.id}")
                        .put("emoji", Emoji.fromCustom(CustomEmojiImpl("mention", 940268589615812608, false)))
                        .put("default", false)

                    roles.add(roleData)
                    ids.add(role.id)
                }
            }

            config.djRoleID = ""
            config.save()

            if (roles.size > 100)
                roles.subList(100, roles.size).clear()

            val rows = ArrayList<ActionRow>()
            roles.chunked(25).forEach { chunk ->
                val random = (0..100000).random()

                rows.add(
                    ActionRow.of(
                        StringSelectMenu.create("cmd:config:setdj:${event.user.id}:${random}")
                            .setPlaceholder(lang.get("commands.config.djrole.select"))
                            .setMaxValues(1)
                            .setMinValues(0)
                            .addOptions(
                                chunk.map {
                                    SelectOption.of(
                                        it["label"] as String,
                                        it["value"] as String,
                                    ).withEmoji(it["emoji"] as Emoji)
                                        .withDefault(it["default"] as Boolean)
                                }
                            )
                            .build()
                    )
                )
            }

            rows.add(
                ActionRow.of(
                    Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                        .withEmoji(
                            Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                        ),
                )
            )
            event.editComponents(rows).queue()
            return
        }

        val args = event.selectedOptions.first().value.split(":")
        val roleID = args[3]

        val ids: ArrayList<String> = ArrayList()
        val roles: ArrayList<JSONObject> = ArrayList()

        event.guild!!.roles.forEach { role ->
            if (role.name != "@everyone") {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("mention", 940268589615812608, false))
                if (roleID == role.id)
                    emoji = Emoji.fromCustom(CustomEmojiImpl("mentionsel", 941367034128760942, false))

                val roleData = JSONObject()
                    .put("label", role.name)
                    .put("value", "cmd:config:setdj:${role.id}:${event.user.id}")
                    .put("emoji", emoji)
                    .put("default", roleID == role.id)

                roles.add(roleData)
                ids.add(role.id)
            }
        }

        if(!ids.contains(roleID)) {
            event.channel.sendMessage("${Constants.Emojis.error}  Unknown role").queue()
            return
        }

        config.djRoleID = roleID
        config.save()

        if (roles.size > 100)
            roles.subList(100, roles.size).clear()


        val rows = ArrayList<ActionRow>()
        roles.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setdj:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.djrole.select"))
                        .setMaxValues(1)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
            )
        )
        event.editComponents(rows).queue()
    }
}