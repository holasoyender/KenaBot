/*
 * File:          Stats.kt
 * Path:          v3\src\main\kotlin\commands\dev\Stats.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 34460e6da13607b765665ee7c597f038ae3cf71bcf2e297822d41d89ca65c8d8
 *
 */


package commands.dev

import database.schema.Commands
import database.schema.Joins
import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


class Stats: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        val initialEmbed = EmbedBuilder()
            .setColor(0x2f3136)
            .setDescription("**Recopilando estadísticas...**")

        event.message.replyEmbeds(initialEmbed.build()).queue {

            val commandStats = Commands.getAll()
            val commandData = commandStats.sortedByDescending { cmd -> cmd.uses }

            val top5Commands = commandData.take(5).joinToString("\n") { cmd ->
                "${cmd.command}: ${cmd.uses} usos"
            }

            val joinData = Joins.getTop10()

            val top5Joins = joinData.take(5).joinToString("\n") { cmd ->
                "${cmd.channelID}: ${cmd.joins} joins"
            }

            val embed = EmbedBuilder()
                .setColor(0x2f3136)
                .setAuthor("Estadísticas del bot", null, event.jda.selfUser.avatarUrl)
                .addField("Comandos más usados", "```$top5Commands```", true)
                .addField("Canales más activos", "```${top5Joins}```", true)

            it.editMessageEmbeds(embed.build()).queue()
        }
    }

    override val name: String
        get() = "stats"
    override val description: String
        get() = "Estadísticas del bot"
}