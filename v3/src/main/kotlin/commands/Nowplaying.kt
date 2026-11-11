/*
 * File:          Nowplaying.kt
 * Path:          v3\src\main\kotlin\commands\Nowplaying.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 63e5a63d4a2f8211cf85fba1e07fd6df736588dddfea4ed35359a56c9bb9d409
 *
 */


package commands

import database.schema.Guild
import functions.StringFormat
import functions.progress
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji
import utils.PlayerTimestamp

class Nowplaying: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if(event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
        val player = manager.player

        if(player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val song = player.playingTrack
        val seek = player.trackPosition
        val left = song.duration - seek

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setAuthor(StringFormat.format(song.info.title), song.info.uri, event.jda.selfUser.avatarUrl)
            .setDescription("`${PlayerTimestamp.get(seek)}`  ${progress(song.duration.toInt(), seek.toInt(), 20, "▬", "🔵")} `${PlayerTimestamp.get(song.duration)}`")

        if(song.duration > 0)
            embed.setFooter(lang.get("commands.now_playing.remaining")+ PlayerTimestamp.get(left))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

    }

    override val name: String
        get() = "nowplaying"
    override val description: String
        get() = "Información de la canción sonando"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = ""
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Información de la canción sonando",
                    DiscordLocale.ENGLISH_US to "Information of the song playing",
                    DiscordLocale.ENGLISH_UK to "Information of the song playing",
                    DiscordLocale.FRENCH to "Informations sur la chanson en cours de lecture",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Informações da música tocando",
                    DiscordLocale.ITALIAN to "Informazioni sulla canzone in riproduzione",
                    DiscordLocale.GERMAN to "Informationen zum abgespielten Lied",
                )
            )
}