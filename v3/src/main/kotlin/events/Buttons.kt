/*
 * File:          Buttons.kt
 * Path:          v3\src\main\kotlin\events\Buttons.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 63824a056cc4acf7d1042775c5272059b8fb36dc35416df026c78861ef32c019
 *
 */


package events

import database.schema.Guild
import functions.SkipVote
import handlers.*
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import player.Player
import restarter.RestartServiceW
import utils.Constants
import utils.Constants.formatEmoji
import java.awt.Color
import kotlin.math.round

class Buttons: ListenerAdapter() {

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        if (event.isFromGuild) {

            val args = event.componentId.split(":")

            if (args[0] == "cmd") {
                when (args[1]) {
                    "play" -> {
                        when (args[2]) {
                            "pause" -> PlayButtons.handlePause(event)
                            "skip" -> PlayButtons.handleSkip(event)
                            "stop" -> PlayButtons.handleStop(event)
                            "showQueue" -> PlayButtons.handleQueue(event)
                            "loop" -> PlayButtons.handleLoop(event)
                            "play" -> PlayButtons.handlePlay(event)
                            else -> event.reply("No se reconoce el comando").setEphemeral(true).queue()
                        }
                    }

                    "queue" -> QueueButtons.handleAction(event)
                    "status" -> StatusButtons.handleAction(event)
                    "config" -> {

                        val config = Guild.get(event.guild!!.id)
                        val lang = LangUtils(config.language)

                        if (args[3] != event.user.id) {
                            val embed = EmbedBuilder()
                                .setColor(config.embedColor)
                                .setDescription(lang.get("commands.config.cant_use"))

                            event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                            return
                        }

                        ConfigHandler.handleButton(event, config, lang, args)
                    }

                    "skip_vote" -> {

                        val config = Guild.get(event.guild!!.id)
                        val lang = LangUtils(config.language)

                        if (SkipVote.getVoteById(event.guild!!.id) == null) {
                            event.reply(lang.get("commands.skip_vote.no_active_vote")).setEphemeral(true).queue()
                            return
                        }

                        if (SkipVote.userVoted(event.guild!!.id, event.user.id)) {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.skip_vote.already_voted")).setEphemeral(true)
                                .queue()
                            return
                        }
                        if (!SkipVote.canVote(event.guild!!.id, event.user.id)) {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.skip_vote.cant_vote")).setEphemeral(true)
                                .queue()
                            return
                        }

                        SkipVote.addVote(event.guild!!.id, event.user.id)
                        val percentage = SkipVote.getPercentage(event.guild!!.id)

                        if (percentage >= 50) {
                            val embed = EmbedBuilder()
                                .setColor(config.embedColor)
                                .setDescription(
                                    lang.get("commands.skip_vote.completed")
                                        .replace("%s%", round(percentage.toDouble()).toString().split(".")[0] + "%")
                                )

                            event.deferEdit().setEmbeds(embed.build()).setActionRow(
                                Button.primary("cmd:skip_vote:vote", lang.get("commands.skip_vote.label")).asDisabled()
                            ).queue()
                            SkipVote.endVote(event.guild!!.id)

                            val manager = Player.getMusicManager(event.guild!!.id)
                            if (manager != null) {

                                val scheduler = manager.scheduler

                                if (scheduler.queue.isEmpty()) {
                                    manager.link.destroy()
                                    Player.destroyMusicManager(event.guild!!)

                                    if (config.premiumEnabled) {
                                        if (!config.radioEnabled) {
                                            event.guild!!.audioManager.closeAudioConnection()
                                        }
                                    } else
                                        event.guild!!.audioManager.closeAudioConnection()

                                } else
                                    scheduler.skip(event.channel as TextChannel)
                            }
                            return
                        }

                        event.reply(formatEmoji(Constants.Emojis.success) + lang.get("commands.skip_vote.voted")).setEphemeral(true).queue()
                        val embed = EmbedBuilder()
                            .setColor(config.embedColor)
                            .setDescription(
                                lang.get("commands.skip_vote.vote")
                                    .replace("%p%", round(percentage.toDouble()).toString().split(".")[0] + "%")
                            )

                        event.message.editMessageEmbeds(embed.build()).queue()

                    }
                }
            }
        } else {
            val args = event.componentId.split(":")

            if (args[0] == "cmd") {
                when (args[1]) {
                    "nodes" -> StatusButtons.handleNodeAction(event)
                    "restart" -> {

                        event.deferEdit().queue { that ->

                            val totalPlayers = Player.musicManagers
                            val playingPlayers = totalPlayers.filter { it.value.player.playingTrack != null }

                            val embed = EmbedBuilder()
                                .setColor(Color.decode("#5865F2"))
                                .setAuthor("Reinicio del bot", null, event.jda.selfUser.avatarUrl)

                            if (playingPlayers.isEmpty()) {
                                embed.setDescription("No hay ningún reproductor activo!")

                                that.editOriginalEmbeds(embed.build()).setActionRow(
                                    Button.success("cmd:restart", "Reiniciar").asDisabled(),
                                    Button.danger("cmd:restart-cancel", "Cancelar").asDisabled()
                                ).queue()
                                return@queue
                            }

                            embed.setDescription("El bot ha mandado un mensaje a todos los nodos para el reinicio\nSe han desconectado `${totalPlayers.size}` reproductores, de los cuales `${playingPlayers.size}` estaban activos")

                            RestartServiceW(event.jda.shardManager!!).savePlayingPlayers()
                            val unavailableServers = ArrayList<String>()

                            playingPlayers.forEach {

                                val guild = event.jda.getGuildById(it.key)

                                if (it.value.player.playingTrack == null) {
                                    unavailableServers.add(it.key)
                                    guild?.audioManager?.closeAudioConnection()
                                    return@forEach
                                }

                                try {

                                    val scheduler = it.value.scheduler
                                    val player = it.value.player

                                    scheduler.queue.clear()
                                    player.stopTrack()
                                    player.isPaused = false

                                    it.value.link.destroy()

                                    guild?.audioManager?.closeAudioConnection()

                                    val config = Guild.get(it.key)
                                    val lang = LangUtils(config.language)

                                    val sendEmbed = EmbedBuilder()
                                        .setColor(config.embedColor)
                                        .setDescription(lang.get("manager.restart.embed_description"))
                                        .setAuthor(
                                            lang.get("manager.restart.embed_author"),
                                            null,
                                            event.jda.selfUser.avatarUrl
                                        )

                                    if (it.value.channel?.canTalk() == true)
                                        it.value.channel?.sendMessageEmbeds(sendEmbed.build())?.queue()
                                            ?: unavailableServers.add(it.key)

                                } catch (e: Exception) {
                                    unavailableServers.add(it.key)
                                }
                            }

                            if (unavailableServers.isNotEmpty())
                                embed.addField(
                                    "Servidores no disponibles", "```${
                                        unavailableServers.map {
                                            "• ${event.jda.getGuildById(it)?.name ?: "Desconocido"} ($it)"
                                        }.joinToString { "\n" }
                                    }```", false
                                )

                            that.editOriginalEmbeds(embed.build()).setActionRow(
                                Button.success("cmd:restart", "Reiniciar").asDisabled(),
                                Button.danger("cmd:restart-cancel", "Cancelar").asDisabled()
                            ).queue()
                        }
                    }

                    "restart-cancel" -> {
                        val embed = EmbedBuilder()
                            .setColor(Color.decode("#5865F2"))
                            .setDescription("Se ha cancelado el reinicio del bot")
                            .setAuthor("Reinicio del bot", null, event.jda.selfUser.avatarUrl)

                        event.editMessageEmbeds(embed.build()).setActionRow(
                            Button.success("cmd:restart", "Reiniciar").asDisabled(),
                            Button.danger("cmd:restart-cancel", "Cancelar").asDisabled()
                        ).queue()
                    }
                    else -> event.reply("No se reconoce el comando").setEphemeral(true).queue()
                }
            }
        }
    }
}
