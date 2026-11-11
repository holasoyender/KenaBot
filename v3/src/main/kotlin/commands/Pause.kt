/*
 * File:          Pause.kt
 * Path:          v3\src\main\kotlin\commands\Pause.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 58d22cf736c78e118e2cb308cd02908ed81eebf7adbad70d0f1f87f26bbd906e
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
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Pause: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
            return
        }

        if(event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
        val player = manager.player
        val scheduler = manager.scheduler

        if(DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if(!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if(player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if(player.isPaused) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.pause.already_paused")).setEphemeral(true).queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + "No puedes modificar la cola de reproducción mientras estás en una sesión!").setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.player.isPaused = true
            }
        }

        player.isPaused = true
        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(formatEmoji(Constants.Emojis.success) + lang.get("commands.pause.paused"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "pause"
    override val description: String
        get() = "Pausar la canción actual"
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
                    DiscordLocale.SPANISH to "Pausar la canción actual",
                    DiscordLocale.ENGLISH_US to "Pause the current song",
                    DiscordLocale.ENGLISH_UK to "Pause the current song",
                    DiscordLocale.FRENCH to "Mettre en pause la chanson actuelle",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Pausar a música atual",
                    DiscordLocale.ITALIAN to "Metti in pausa la canzone corrente",
                    DiscordLocale.GERMAN to "Pausiere den aktuellen Song",
                )
            )
}