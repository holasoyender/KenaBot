/*
 * File:          Interactions.kt
 * Path:          v3\src\main\kotlin\events\Interactions.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 050a6a45bdbd2ffa51e6464225524c51c339082f1cd8d729693d555e8489416d
 *
 */


package events

import database.schema.Guild
import database.schema.Whitelist
import handlers.ConfigHandler
import langs.LangUtils
import managers.CommandManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import thirdparty.spotify.Autocomplete
import utils.Constants
import utils.Constants.formatEmoji
import utils.PingCalc
import java.awt.Color
import java.net.MalformedURLException
import java.net.URL

class Interactions: ListenerAdapter() {

    private val manager: CommandManager = CommandManager.instance!!

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        PingCalc.start()
        val user: User = event.user
        if (user.isBot) return

        if (event.guild != null)
            if (Whitelist.isWhitelistEnabled()) {
                if (!Whitelist.isWhitelisted(event.guild!!.id)) {
                    val embed = EmbedBuilder()
                        .setColor(Color.decode("#2f3136"))
                        .setDescription(formatEmoji(Constants.Emojis.error) + "Este servidor no está en la whitelist")
                    event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                    return
                }
            }

        manager.run(event)
    }

    override fun onCommandAutoCompleteInteraction(event: CommandAutoCompleteInteractionEvent) {

        if(event.fullCommandName == "play" && event.focusedOption.name == "canción") {

            val search = event.options[0].asString

            if(search.length < 3 || event.channelType == ChannelType.PRIVATE || event.member?.voiceState?.channel == null || isUrl(search)) {
                event.replyChoiceStrings().queue()
                return
            }

            val results = Autocomplete.search(search).map {if(it.length > 100) it.substring(0, 95) + "..." else it }

            if(results.isEmpty())
                event.replyChoiceStrings().queue()
            else
                event.replyChoiceStrings(results).queue()
        }
    }

    override fun onModalInteraction(event: ModalInteractionEvent) {

        val args = event.modalId.split(":")
        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        when (args[0]) {
            "config" -> ConfigHandler.handleModal(event, config, lang, args)
            else -> event.reply("No se ha encontrado el modal").setEphemeral(true).queue()
        }
    }

    private fun isUrl(query: String): Boolean {
        return try {
            URL(query)
            true
        } catch (e: MalformedURLException) {
            false
        }
    }
}