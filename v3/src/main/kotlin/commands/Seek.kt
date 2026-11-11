/*
 * File:          Seek.kt
 * Path:          v3\src\main\kotlin\commands\Seek.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 21:08:05 +02:00
 *
 * SHA-256: 0ecfb0e8a3ad423c369b29ba965b07358ff3f50bb03afeb7e5ff9cdb1835152f
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import functions.DjRole
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji
import utils.PlayerTimestamp
import utils.Time

class Seek: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue"))
                    .setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
        val player = manager.player
        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val rawTime = event.getOption("tiempo")!!.asString

        val time = Time.ms(rawTime)
        if (time < 1) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.seek.invalid_time")).setEphemeral(true)
                .queue()
            return
        }

        if (!player.playingTrack.isSeekable) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.seek.not_seekable")).setEphemeral(true)
                .queue()
            return
        }

        val position = player.trackPosition
        val duration = player.playingTrack.duration

        if (time <= duration) {

            val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
            if(session != null) {
                if(!isHost) {
                    event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!").setEphemeral(true).queue()
                    return
                }

                session.listeners.forEach { listener ->
                    val guildManager = Player.getMusicManager(listener.guildId, true)
                    guildManager!!.player.seekTo(time)
                }
            }

            player.seekTo(time)

            val embed = EmbedBuilder()
                .setColor(config.embedColor)

            if (time > position) {
                embed.setDescription(
                    lang.get("commands.seek.forward_success")
                        .replace("%s%", "`${PlayerTimestamp.get(time)} / ${PlayerTimestamp.get(duration)}`")
                )
            } else {
                embed.setDescription(
                    lang.get("commands.seek.backward_success")
                        .replace("%s%", "`${PlayerTimestamp.get(time)} / ${PlayerTimestamp.get(duration)}`")
                )
            }
            event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()

        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.seek.max_duration")).setEphemeral(true)
                .queue()
            return
        }
    }

    override val name: String
        get() = "seek"
    override val description: String
        get() = "Saltar a un momento específico de la canción"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<Tiempo>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Saltar a un momento específico de la canción",
                    DiscordLocale.ENGLISH_US to "Skip to a specific moment of the song",
                    DiscordLocale.ENGLISH_UK to "Skip to a specific moment of the song",
                    DiscordLocale.FRENCH to "Passer à un moment précis de la chanson",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Pule para um momento específico da música",
                    DiscordLocale.ITALIAN to "Salta a un momento specifico della canzone",
                    DiscordLocale.GERMAN to "Springen Sie zu einem bestimmten Moment des Songs",
                )
            )
            .addOptions(
                OptionData(OptionType.STRING, "tiempo", "Momento de la canción al que saltar")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Momento de la canción al que saltar",
                            DiscordLocale.ENGLISH_US to "Song moment to skip to",
                            DiscordLocale.ENGLISH_UK to "Song moment to skip to",
                            DiscordLocale.FRENCH to "Moment de la chanson à sauter",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Momento da música para pular",
                            DiscordLocale.ITALIAN to "Momento della canzone da saltare",
                            DiscordLocale.GERMAN to "Song-Moment zum Überspringen",
                        )
                    )
                    .setRequired(true)
            )
}