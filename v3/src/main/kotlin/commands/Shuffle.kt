/*
 * File:          Shuffle.kt
 * Path:          v3\src\main\kotlin\commands\Shuffle.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: 940da41ae6918e9d128da38b71f1e40c7fdf0a2a086fcda0c7cbbbf2de764870
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
import java.util.Random

class Shuffle: Command {
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

        val rnd = Random()
        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
        if(session != null) {
            if(!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session")).setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.scheduler.shuffle(rnd)
            }
        }

        scheduler.shuffle(rnd)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(formatEmoji(Constants.Emojis.success) + lang.get("commands.shuffle"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "shuffle"
    override val description: String
        get() = "Poner la lista de reproducción en orden aleatorio"
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
                    DiscordLocale.SPANISH to "Poner la lista de reproducción en orden aleatorio",
                    DiscordLocale.ENGLISH_US to "Shuffle the queue",
                    DiscordLocale.ENGLISH_UK to "Shuffle the queue",
                    DiscordLocale.FRENCH to "Mélanger la file d'attente",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Embaralhar a fila",
                    DiscordLocale.ITALIAN to "Mischia la coda",
                    DiscordLocale.GERMAN to "Warteschlange mischen",
                )
            )
}