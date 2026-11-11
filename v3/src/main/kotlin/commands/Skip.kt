/*
 * File:          Skip.kt
 * Path:          v3\src\main\kotlin\commands\Skip.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-13 22:46:09 +02:00
 *
 * SHA-256: 4d2fcb3dd48fe98ca4f9739573e1252ef465d500e1c57a64f14ce7fa99b67fcd
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import enums.LoopMode
import functions.DjRole
import functions.SkipVote
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Skip: Command {
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

        if(player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)

        if(session == null) {
            if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
                if (!DjRole.checkRole(event.member!!, config, scheduler)) {

                    if (SkipVote.getVoteById(event.guild!!.id) != null) {
                        event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.skip_vote.already_active"))
                            .setEphemeral(true).queue()
                        return
                    }

                    val embed = EmbedBuilder()
                        .setColor(config.embedColor)
                        .setDescription(lang.get("commands.skip_vote.vote").replace("%p%", "0"))

                    event.reply(lang.get("commands.skip_vote.announce")).setEphemeral(true).queue()
                    if (event.channel.canTalk())
                        event.channel.sendMessageEmbeds(embed.build()).setActionRow(
                            Button.primary("cmd:skip_vote:vote", lang.get("commands.skip_vote.label"))
                        ).queue()
                    SkipVote.createVote(event.guild!!.id,
                        event.guild!!.selfMember.voiceState?.channel?.members?.filter { !it.user.isBot }
                            ?.map { it.user.id }?.toMutableList() ?: mutableListOf()
                    )
                    return
                }
            }
        } else {
            if (!isHost) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session"))
                    .setEphemeral(true).queue()
                return
            }

            session.listeners.forEach { listener ->
                val guildManager = Player.getMusicManager(listener.guildId, true)!!
                guildManager.scheduler.isRepeating = LoopMode.NONE
                guildManager.scheduler.skip(guildManager.scheduler.lastChannel, scheduler.queue.isEmpty())
            }
        }

        scheduler.isRepeating = LoopMode.NONE
        scheduler.skip(event.channel as GuildMessageChannel, scheduler.queue.isEmpty())

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setDescription(formatEmoji(Constants.Emojis.success) + lang.get("commands.skip"))

        event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "skip"
    override val description: String
        get() = "Salta la canción actual"
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
                    DiscordLocale.SPANISH to "Salta la canción actual",
                    DiscordLocale.ENGLISH_US to "Skips the current song",
                    DiscordLocale.ENGLISH_UK to "Skips the current song",
                    DiscordLocale.FRENCH to "Sauter la chanson actuelle",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Pula a música atual",
                    DiscordLocale.ITALIAN to "Salta la canzone corrente",
                    DiscordLocale.GERMAN to "Überspringt das aktuelle Lied",
                )
            )
}