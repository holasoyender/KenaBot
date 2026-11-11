/*
 * File:          Ping.kt
 * Path:          v3\src\main\kotlin\commands\dev\Ping.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 4272982513b966d45ffc31a47bf13f0285db45c9b8d2d670137d47cf067904e3
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.message.MessageReceivedEvent


class Ping: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        val jda: JDA = event.jda
        event.message.reply(":ping_pong:  Pong! `${jda.gatewayPing}ms`").queue()
    }

    override val name: String
        get() = "ping"
    override val description: String
        get() = "Pong!"
}