/*
 * File:          Channels.kt
 * Path:          v3\src\main\kotlin\handlers\config\Channels.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 07e0e6b6b47569dc4e0e585875740270dc687b495da64d25b9b4866b135f9317
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
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.json.JSONObject

object Channels {

    fun goto(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setTitle(lang.get("commands.config.channels.title"))
            .setDescription(lang.get("commands.config.channels.description"))

        val channels: ArrayList<JSONObject> = ArrayList()

        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.TEXT) {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("channels", 940254447530016859, false))
                if (config.disabledChannels.contains(channel.id))
                    emoji = Emoji.fromCustom(CustomEmojiImpl("channelssel", 940633186000572498, false))

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
                    .put("emoji", emoji)
                    .put("default", config.disabledChannels.contains(channel.id))
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
                    StringSelectMenu.create("cmd:config:setdchannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.channels.select"))
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
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:channels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:channels_desselectall:${event.user.id}",
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

        config.disabledChannels = ids
        config.save()

        val channels: ArrayList<JSONObject> = ArrayList()
        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.TEXT) {

                var emoji = Emoji.fromCustom(CustomEmojiImpl("channels", 940254447530016859, false))
                if (ids.contains(channel.id))
                    emoji = Emoji.fromCustom(CustomEmojiImpl("channelssel", 940633186000572498, false))

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
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
                    StringSelectMenu.create("cmd:config:setdchannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.channels.select"))
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
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:channels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:channels_desselectall:${event.user.id}",
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
            if (channel.type == ChannelType.TEXT) {

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
                    .put("emoji", Emoji.fromCustom(CustomEmojiImpl("channelssel", 940633186000572498, false)))
                    .put("default", true)
                    .put("description", "Channel ID: ${channel.id}")

                ids.add(channel.id)
                channels.add(channelData)
            }
        }

        config.disabledChannels = ids
        config.save()

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()

        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setdchannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.channels.select"))
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
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:channels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:channels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editComponents(rows).queue()
    }

    fun desSelectAll(event: ButtonInteractionEvent, config: Guild, lang: LangUtils) {

        val channels: ArrayList<JSONObject> = ArrayList()
        event.guild!!.channels.forEach { channel ->
            if (channel.type == ChannelType.TEXT) {

                val channelData = JSONObject()
                    .put("label", channel.name)
                    .put("value", "cmd:config:setdchannel:${channel.id}:${event.user.id}")
                    .put("emoji", Emoji.fromCustom(CustomEmojiImpl("channels", 940254447530016859, false)))
                    .put("default", false)
                    .put("description", "Channel ID: ${channel.id}")

                channels.add(channelData)
            }
        }

        config.disabledChannels = ArrayList()
        config.save()

        if (channels.size > 100)
            channels.subList(100, channels.size).clear()

        val rows = ArrayList<ActionRow>()
        channels.chunked(25).forEach { chunk ->
            val random = (0..100000).random()

            rows.add(
                ActionRow.of(
                    StringSelectMenu.create("cmd:config:setdchannel:${event.user.id}:${random}")
                        .setPlaceholder(lang.get("commands.config.channels.select"))
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
                Button.secondary("cmd:config:back:${event.user.id}", lang.get("commands.config.buttons.back"))
                    .withEmoji(
                        Emoji.fromCustom(CustomEmojiImpl("left", 940316141983764494, false))
                    ),
                Button.danger(
                    "cmd:config:channels_selectall:${event.user.id}",
                    lang.get("commands.config.buttons.select_all")
                ),
                Button.success(
                    "cmd:config:channels_desselectall:${event.user.id}",
                    lang.get("commands.config.buttons.remove_all")
                ),
            )
        )
        event.editComponents(rows).queue()
    }
}