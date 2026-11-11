/*
 * File:          QueueButtons.kt
 * Path:          v3\src\main\kotlin\handlers\QueueButtons.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-13 22:05:15 +02:00
 *
 * SHA-256: 8324b913928b74eff59383af2923209bcb64227379b1d7f3f710a36099c4142a
 *
 */


package handlers

import database.schema.Guild
import functions.StringFormat
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import player.Player
import utils.Constants
import utils.Constants.formatEmoji
import utils.PlayerTimestamp

object QueueButtons {

    fun handleAction(event: ButtonInteraction) {

        val args = event.componentId.split(":")

        val action = args[3]
        var page = args[2].toInt()
        val userID = args[4]

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.user.id != userID) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("events.interactionCreate.cant_use_button")).setEphemeral(true).queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!.id)

        if (manager == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val player = manager.player

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true).queue()
            return
        }

        val queue = manager.scheduler.queue
        val chunk = queue.chunked(5)

        if (action == "next") {
            page += 1
            if (page > chunk.size - 1) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.queue.no_next")).setEphemeral(true).queue()
                return
            }
        }
        if (action == "back") {
            page -= 1
            if (page < 0) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.queue.no_previous")).setEphemeral(true).queue()
                return
            }
            if (page > chunk.size - 1)
                page = chunk.size - 1
        }

        val embed = EmbedBuilder()
            .setColor(config.embedColor)
            .setFooter(
                lang.get("commands.queue.embed_footer").replace("%s%", (page + 1).toString())
                    .replace("%d%", chunk.size.toString()).replace("%f%", (queue.size + 1).toString())
            )
            .setAuthor(lang.get("commands.queue.embed_author"), null, event.jda.selfUser.avatarUrl)

        if (manager.player.playingTrack != null) {
            embed.setDescription("```yml\n --- ${lang.get("commands.queue.now_playing")} ---\n${StringFormat.format(manager.player.playingTrack.info.title)}```")
        }

        if (chunk.isNotEmpty())
            for ((i, track) in chunk[page].withIndex()) {
                embed.addField(
                    "${i + 1 + (page * 5)}. ${StringFormat.format(track.info.title)}",
                    "**Autor:** ${track.info.author}\n**Duración**: ${PlayerTimestamp.get(track.duration)}",
                    false
                )
            }

        event.editMessageEmbeds(embed.build()).setActionRow(
            Button.primary("cmd:queue:$page:back:" + event.user.id, "◀").withDisabled(page == 0),
            Button.primary("cmd:queue:$page:next:" + event.user.id, "▶").withDisabled(page == chunk.size - 1)
        ).queue()
    }
}