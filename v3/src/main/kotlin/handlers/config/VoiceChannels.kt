/*
 * File:          VoiceChannels.kt
 * Path:          v3\src\main\kotlin\handlers\config\VoiceChannels.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: f5f3b15bb5964fcc79b3ce369da8a74c303d6d1d27a77be2c205cd7b0273934b
 *
 */


package handlers.config

import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ActionRow
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.json.JSONObject

object VoiceChannels {
    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.voice.title"))
            .setDescription(lang.get("commands.config.voice.description"))

        val channels: ArrayList<JSONObject> = ArrayList()

        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.VOICE) {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("voice", 941354953170116688, false))
                if (config.disabledChannels.contains(channel.id))
                    emoji = Emoji.fromCustom(CustomEmojiImpl("voicesel", 941355299158245466, false))

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setvoicechannel:${channel.id}:${event.user.id}")
                    .put("emoji", emoji)
                    .put("default", config.disabledVoiceChannels.contains(channel.id))
                    .put("description", "Channel ID: ${channel.id}")

                channels.add(channelData)
            }
        }

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()


        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setvoicechannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.voice.select"))
                        .setMaxValues(chunk.size)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:voicechannels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:voicechannels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editMessageEmbeds(embed.build()).setComponents(rows).queue()
    }

    fun set(event: StringSelectInteractionEvent, config: Guild, lang: LangUtils) {

        val ids: ArrayList<String> = ArrayList()

        event.selectedOptions.forEach {
            ids.add(it.value.split(":")[3])
        }

        event.message.actionRows.forEach { actionRow ->
            if (!actionRow.isEmpty)
                actionRow.components.forEach { menu ->
                    if (menu is StringSelectMenu && menu.id != event.selectMenu.id)
                        menu.options.forEach { option ->
                            if (option.isDefault)
                                ids.add(option.value.split(":")[3])
                        }
                }
        }

        config.disabledVoiceChannels = ids
        config.save()

        val channels: ArrayList<JSONObject> = ArrayList()
        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.VOICE) {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("voice", 941354953170116688, false))
                if (ids.contains(channel.id))
                    emoji = Emoji.fromCustom(CustomEmojiImpl("voicesel", 941355299158245466, false))

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setvoicechannel:${channel.id}:${event.user.id}")
                    .put("emoji", emoji)
                    .put("default", ids.contains(channel.id))
                    .put("description", "Channel ID: ${channel.id}")

                channels.add(channelData)
            }
        }

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()

        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setvoicechannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.voice.select"))
                        .setMaxValues(chunk.size)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:voicechannels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:voicechannels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editComponents(rows).queue()

    }

    fun selectAll(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val ids: ArrayList<String> = ArrayList()
        val channels: ArrayList<JSONObject> = ArrayList()

        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.VOICE) {

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
                    .put("emoji", Emoji.fromCustom(CustomEmojiImpl("voicesel", 941355299158245466, false)))
                    .put("default", true)
                    .put("description", "Channel ID: ${channel.id}")

                ids.add(channel.id)
                channels.add(channelData)
            }
        }

        config.disabledVoiceChannels = ids
        config.save()

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()

        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setvoicechannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.voice.select"))
                        .setMaxValues(chunk.size)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:voicechannels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:voicechannels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editComponents(rows).queue()
    }

    fun desSelectAll(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val channels: ArrayList<JSONObject> = ArrayList()
        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.VOICE) {

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
                    .put("emoji", Emoji.fromCustom(CustomEmojiImpl("voice", 941354953170116688, false)))
                    .put("default", false)
                    .put("description", "Channel ID: ${channel.id}")

                channels.add(channelData)
            }
        }

        config.disabledVoiceChannels = ArrayList()
        config.save()

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()

        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setvoicechannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.voice.select"))
                        .setMaxValues(chunk.size)
                        .setMinValues(0)
                        .addOptions(
                            chunk.map {
                                SelectOption.of(
                                    it["label"] as String,
                                    it["value"] as String,
                                ).withDescription(it["description"] as String)
                                    .withEmoji(it["emoji"] as Emoji)
                                    .withDefault(it["default"] as Boolean)
                            }
                        )
                        .build()
                )
            )
        }

        rows.add(
            ActionRow.of(
                Button.secondary("cmd:config:more:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:voicechannels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:voicechannels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editComponents(rows).queue()
    }
}