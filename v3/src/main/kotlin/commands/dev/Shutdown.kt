/*
 * File:          Shutdown.kt
 * Path:          v3\src\main\kotlin\commands\dev\Shutdown.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 90e38579ab2d5e90ae0b144289a18dfa389276b78af12202aa63055f33e1eed4
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import utils.Constants.formatEmoji

import kotlin.system.exitProcess

class Shutdown: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        event.message.reply(formatEmoji(Constants.Emojis.success) + "El bot se ha apagado.").queue()
        event.jda.shutdownNow()
        exitProcess(0)
    }

    override val name: String
        get() = "shutdown"
    override val description: String
        get() = "Apagar todo el bot"
}