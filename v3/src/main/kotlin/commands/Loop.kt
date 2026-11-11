/*
 * File:          Loop.kt
 * Path:          v3\src\main\kotlin\commands\Loop.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: afdc0c28ed0933ac6b82878c4c404c810f0c3283975871318bf8585a41846524
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import enums.LoopMode
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
import net.dv8tion.jda.api.interactions.components.buttons.Button
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Loop: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val option = event.getOption("estado")

        if (option == null) {

            val manager = Player.getMusicManager(event.guild!!) ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            val scheduler = manager.scheduler

            val state = when (scheduler.isRepeating) {
                LoopMode.TRACK -> lang.get("commands.loop.repeat_track")
                LoopMode.QUEUE -> lang.get("commands.loop.repeat_queue")
                LoopMode.NONE -> lang.get("commands.loop.no_repeat")
            }

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setDescription(lang.get("commands.loop.info").replace("%s%", state))

            if (event.member!!.voiceState?.inAudioChannel() == false) {
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
            } else {
                if (event.guild?.selfMember?.voiceState?.channel != null && event.guild?.selfMember?.voiceState?.channel == event.member?.voiceState?.channel) {
                    event.replyEmbeds(embed.build())
                        .setActionRow(
                            when (scheduler.isRepeating) {
                                LoopMode.TRACK -> Button.primary(
                                    "cmd:play:loop",
                                    lang.get("commands.loop.repeat_track").replaceFirstChar { it.uppercase() })
                                LoopMode.QUEUE -> Button.success(
                                    "cmd:play:loop",
                                    lang.get("commands.loop.repeat_queue").replaceFirstChar { it.uppercase() })
                                LoopMode.NONE -> Button.secondary(
                                    "cmd:play:loop",
                                    lang.get("commands.loop.no_repeat").replaceFirstChar { it.uppercase() })
                            }
                        ).queue()
                } else {
                    event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
                }
            }
        } else {

            val state = option.asInt

            if (event.member!!.voiceState?.inAudioChannel() == false) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue"))
                    .setEphemeral(true).queue()
                return
            }

            if (event.guild!!.selfMember.voiceState?.channel == null) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing"))
                    .setEphemeral(true).queue()
                return
            }

            if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
                if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue"))
                        .setEphemeral(true).queue()
                    return
                }
            } else {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing"))
                    .setEphemeral(true).queue()
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
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing"))
                    .setEphemeral(true).queue()
                return
            }

            if (scheduler.isRepeating == LoopMode.TRACK && state == 2) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(
                        formatEmoji(Constants.Emojis.error) + lang.get("commands.loop.already")
                            .replace("%s%", lang.get("commands.loop.repeat_track"))
                    )

                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }

            if (scheduler.isRepeating == LoopMode.QUEUE && state == 3) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(
                        formatEmoji(Constants.Emojis.error) + lang.get("commands.loop.already")
                            .replace("%s%", lang.get("commands.loop.repeat_queue"))
                    )

                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }

            if (scheduler.isRepeating == LoopMode.NONE && state == 4) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(
                        formatEmoji(Constants.Emojis.error) + lang.get("commands.loop.already")
                            .replace("%s%", lang.get("commands.loop.no_repeat"))
                    )

                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }

            val (isHost, session) = Session.get(event.guild!!.id, event.jda.selfUser.name)
            if(session != null) {
                if(!isHost) {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.session.cant_modify_session")).setEphemeral(true).queue()
                    return
                }

                session.listeners.forEach { listener ->
                    val guildManager = Player.getMusicManager(listener.guildId, true)
                    guildManager!!.scheduler.isRepeating = when (state) {
                        2 -> LoopMode.TRACK
                        3 -> LoopMode.QUEUE
                        4 -> LoopMode.NONE
                        else -> LoopMode.NONE
                    }
                }
            }

            scheduler.isRepeating = when (state) {
                2 -> LoopMode.TRACK
                3 -> LoopMode.QUEUE
                4 -> LoopMode.NONE
                else -> LoopMode.NONE
            }

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setDescription(
                    formatEmoji(Constants.Emojis.success) + lang.get("commands.loop.action").replace(
                        "%s%",
                        when (state) {
                            2 -> lang.get("commands.loop.repeat_track")
                            3 -> lang.get("commands.loop.repeat_queue")
                            4 -> lang.get("commands.loop.no_repeat")
                            else -> lang.get("commands.loop.no_repeat")
                        }
                    )
                )

            event.replyEmbeds(embed.build()).setEphemeral(config.silent).queue()
        }
    }

    override val name: String
        get() = "loop"
    override val description: String
        get() = "Activar o desactivar el bucle de la canción actual."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<Estado>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Activar o desactivar el bucle de la canción actual.",
                    DiscordLocale.ENGLISH_US to "Enable or disable the loop of the current song.",
                    DiscordLocale.ENGLISH_UK to "Enable or disable the loop of the current song.",
                    DiscordLocale.FRENCH to "Activer ou désactiver la boucle de la chanson actuelle.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Ativar ou desativar o loop da música atual.",
                    DiscordLocale.ITALIAN to "Abilita o disabilita il loop della canzone corrente.",
                    DiscordLocale.GERMAN to "Aktivieren oder Deaktivieren Sie die Schleife des aktuellen Songs.",
                )
            )
            .addOptions(
                OptionData(OptionType.INTEGER, "estado", "Estado del bucle")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Estado del bucle",
                            DiscordLocale.ENGLISH_US to "Loop state",
                            DiscordLocale.ENGLISH_UK to "Loop state",
                            DiscordLocale.FRENCH to "État de la boucle",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Estado do loop",
                            DiscordLocale.ITALIAN to "Stato del loop",
                            DiscordLocale.GERMAN to "Schleifenstatus",
                        )
                    )
                    .addChoice("Track loop", 2)
                    .addChoice("Queue loop", 3)
                    .addChoice("Sin bucle", 4)
                    .setRequired(false)
            )
}