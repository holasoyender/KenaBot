/*
 * File:          Top.kt
 * Path:          v3\src\main\kotlin\commands\dev\Top.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 699c6ca089f222c591dd459f5c8911c5f6a5934cb76d2f4d610772d431bab8f9
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


class Top: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        val topServers = event.jda.shardManager!!.guilds.sortedByDescending { it.memberCount }

        val howMany = if (args.isNotEmpty()) args.getOrNull(1)?.toIntOrNull() ?: 10 else 10
        if(howMany > 100) {
            event.channel.sendMessage("Solo 100 servers eeeehhhhhh que lo ropes eeeeehhhhhhh >:(").queue()
            return
        }

        val top = topServers.take(howMany)
        val servers = top.mapIndexed { idx, server -> "${idx+1}) ${server.name} [${server.id}] - ${server.memberCount} miembros" }

        servers.chunked(10).forEach { chunk ->
            if(chunk == servers.chunked(10)[0]) {
                event.message.reply("```js\n Servidores más grandes de ${event.jda.selfUser.name}\n\n${chunk.joinToString("\n")}```").queue()
            } else {
                event.channel.sendMessage("```js\n${chunk.joinToString("\n")}```").queue()
            }
        }
    }

    override val name: String
        get() = "top"
    override val description: String
        get() = "Servidores más grandes del bot."
}