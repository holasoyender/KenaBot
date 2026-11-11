/*
 * File:          Status.kt
 * Path:          v3\src\main\kotlin\commands\dev\Status.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 66ac04798da1e353cee2e8d1444206c45d2a12a37ea1c51f255e6fb7eb7de204
 *
 */


package commands.dev

import functions.LastState
import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.TimeFormat


class Status: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        val embed = EmbedBuilder()
            .setColor(0x2f3136)
            .setAuthor("Estado actual del bot", null, event.jda.selfUser.avatarUrl)
            .setThumbnail(event.jda.selfUser.avatarUrl)

        val botGuilds = event.jda.shardManager!!.guilds

        val totalUsers = botGuilds.sumOf { it.memberCount }

        val string = """
            **Encendido en:** ${TimeFormat.DEFAULT.format(LastState.getReadyAt())} (${TimeFormat.RELATIVE.format(LastState.getReadyAt())})
            **Usuarios totales:** $totalUsers
            **Servidores:** ${botGuilds.size}
            **Ultimo estado:** `${LastState.getLastState()}`
            
            **Información de JVM:** ```Versión: ${System.getProperty("java.version")} (${System.getProperty("java.vendor")})
            ${System.getProperty("os.name")} ${System.getProperty("os.version")}
            
            Heap de memoria: ${Runtime.getRuntime().totalMemory() / 1024 / 1024} MB
            Uso de CPU: ${(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / Runtime.getRuntime().totalMemory() * 100}%
            Memory pool (free): ${Runtime.getRuntime().freeMemory() / 1024 / 1024} MB 
            ```
        """.trimIndent()

        embed.setDescription(string)

        event.message.replyEmbeds(embed.build()).queue()
    }

    override val name: String
        get() = "status"
    override val description: String
        get() = "Estado interno del bot"
}