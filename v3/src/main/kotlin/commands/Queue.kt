/*
 * File:          Queue.kt
 * Path:          v3\src\main\kotlin\commands\Queue.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 17:50:31 +02:00
 *
 * SHA-256: ba2bc96026731bb72e4ae331f43aa9444b857b6bfee06a6ae1bb13e2422c2d42
 *
 */


package commands

import database.schema.Guild
import functions.StringFormat
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import player.Player
import utils.Constants
import utils.Constants.formatEmoji
import utils.PlayerTimestamp

class Queue: Command {
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

        val queue = manager.scheduler.queue
        val chunk = queue.chunked(5)

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setFooter(lang.get("commands.queue.embed_footer").replace("%s%", "1").replace("%d%", chunk.size.toString()).replace("%f%", (queue.size+1).toString()))
            .setAuthor(lang.get("commands.queue.embed_author"), null, event.jda.selfUser.avatarUrl)

        if(manager.player.playingTrack != null) {
            embed.setDescription("```yml\n --- ${lang.get("commands.queue.now_playing")} ---\n${StringFormat.format(manager.player.playingTrack.info.title)}```")
        }

        if (chunk.isNotEmpty())
        for ((i, track) in chunk.first().withIndex()) {
            embed.addField("${i+1}. ${StringFormat.format(track.info.title)}", "**Autor:** ${track.info.author}\n**Duración**: ${PlayerTimestamp.get(track.duration)}",  false)
        }

        event.replyEmbeds(embed.build()).addActionRow(
            Button.primary("cmd:queue:0:back:"+event.user.id, "◀").asDisabled(),
            Button.primary("cmd:queue:0:next:"+event.user.id, "▶").withDisabled(chunk.size == 1)
        ).setEphemeral(config.silent).queue()
    }

    override val name: String
        get() = "queue"
    override val description: String
        get() = "Mostrar la lista de reproducción"
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
                    DiscordLocale.SPANISH to "Mostrar la lista de reproducción",
                    DiscordLocale.ENGLISH_US to "Show the queue",
                    DiscordLocale.ENGLISH_UK to "Show the queue",
                    DiscordLocale.FRENCH to "Afficher la file d'attente",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Mostrar a fila",
                    DiscordLocale.ITALIAN to "Mostra la coda",
                    DiscordLocale.GERMAN to "Warteschlange anzeigen",
                )
            )
}