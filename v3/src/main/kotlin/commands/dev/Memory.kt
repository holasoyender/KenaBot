/*
 * File:          Memory.kt
 * Path:          v3\src\main\kotlin\commands\dev\Memory.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 08d381c138bcf9507f33743cd4f5f8f383653ebed329522a8d1d57555fbd922c
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent

import kotlin.math.floor

class Memory: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        val rt = Runtime.getRuntime()
        val freeMem = rt.freeMemory().toDouble()
        val totalMem = rt.totalMemory().toDouble()
        val usedMem = totalMem - freeMem

        event.message.reply("```yml\nMemoria usada: ${floor(usedMem / 1000000)} MB\nMemoria total: ${floor(totalMem / 1000000)} MB\nMemoria libre: ${floor(freeMem / 1000000)} MB```").queue()
    }

    override val name: String
        get() = "memory"
    override val description: String
        get() = "Información de uso de memoria del bot"
}