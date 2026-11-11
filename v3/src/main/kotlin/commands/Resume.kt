/*
 * File:          Resume.kt
 * Path:          v3\src\main\kotlin\commands\Resume.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: ebc6b56531751de7c5a9b7d5b7fb29e59183c73cea766bfc1658b766fbbda8d6
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

class Resume: Command {
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

        if (!player.isPaused) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.pause.already_resumed"))
                .setEphemeral(true).queue()
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
                guildManager.player.isPaused = false
            }
        }

        player.isPaused = false
        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(formatEmoji(Constants.Emojis.success) + lang.get("commands.pause.resumed"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "resume"
    override val description: String
        get() = "Resumir la canción actual"
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
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Resumir la canción actual",
                    DiscordLocale.ENGLISH_US to "Resume the current song",
                    DiscordLocale.ENGLISH_UK to "Resume the current song",
                    DiscordLocale.FRENCH to "Reprendre la chanson actuelle",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Retome a música atual",
                    DiscordLocale.ITALIAN to "Riprendi la canzone corrente",
                    DiscordLocale.GERMAN to "Setze das aktuelle Lied fort",
                )
            )
    override val premium: Boolean
        get() = false
}