/*
 * File:          Menus.kt
 * Path:          v3\src\main\kotlin\events\Menus.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 524bdadaf15bf9af9fd9b34d0e78b6a62faa3890810e4bed5a336ae39984b676
 *
 */


package events

import database.schema.Guild
import functions.DiscordTogether
import handlers.ConfigHandler
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import utils.Constants
import utils.Constants.formatEmoji

class Menus: ListenerAdapter() {
    
    override fun onStringSelectInteraction(event: StringSelectInteractionEvent) {
        if (!event.isFromGuild) {
            event.reply("Solo puedo usar este comando en un servidor").setEphemeral(true).queue()
            return
        }

        var args = event.componentId.split(":")
        if(event.selectedOptions.isNotEmpty())
            args = event.selectedOptions.first().value.split(":")

        if (args[0] == "cmd") {
            when (args[1]) {
                "together" -> {
                    val channel = event.member!!.voiceState?.channel
                    val config = Guild.get(event.guild!!.id)
                    val lang = LangUtils(config.language)

                    if (channel == null) {
                        event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_required"))
                            .setEphemeral(true).queue()
                        return
                    }

                    if (channel.type != ChannelType.VOICE) {
                        event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_required"))
                            .setEphemeral(true).queue()
                        return
                    }


                    if (args[3] != event.user.id) {
                        val embed = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setDescription(lang.get("commands.config.cant_use"))

                        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                        return
                    }

                    val activityID: String = DiscordTogether.getAppID(args[2])
                    (channel as VoiceChannel).createInvite().setTargetApplication(activityID).queue {

                        if (it == null) {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_activity")).setEphemeral(true).queue()
                            return@queue
                        }

                        if (it.code == "50013") {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_activity")).setEphemeral(true).queue()
                            return@queue
                        }

                        val name: String = DiscordTogether.getName(args[2])

                        val embed = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setAuthor(lang.get("commands.together.embed_title").replace("%s%", name))
                            .setDescription(lang.get("commands.together.embed_description").replace("%s%", "https://discord.gg/${it.code}"))

                        event.editMessageEmbeds(embed.build()).setActionRow(
                            Button.link("https://discord.gg/${it.code}", lang.get("commands.together.button"))
                                .withEmoji(Emoji.fromUnicode("\uD83D\uDE80"))
                        ).queue()

                    }

                }
                "config" -> {

                    val config = Guild.get(event.guild!!.id)
                    val lang = LangUtils(config.language)
                    if (args.getOrNull(4) != event.user.id && args[3] != event.user.id) {
                        val embed = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setDescription(lang.get("commands.config.cant_use"))

                        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                        return
                    }

                    ConfigHandler.handleMenu(event, config, lang, args)
                }
            }
        }
    }
}