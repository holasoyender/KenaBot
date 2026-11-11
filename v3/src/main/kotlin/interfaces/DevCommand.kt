/*
 * File:          DevCommand.kt
 * Path:          v3\src\main\kotlin\interfaces\DevCommand.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-26 22:13:05 +01:00
 *
 * SHA-256: 68c643323ad5b9d651909e3cd7fa7575f2f30b3022534ccfb4376ab7d0ed382e
 *
 */


package interfaces

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface DevCommand {
    fun run(event: MessageReceivedEvent, args: List<String>)
    val name: String
    val description: String
}