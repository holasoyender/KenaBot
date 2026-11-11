/*
 * File:          DevCommand.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\interfaces\DevCommand.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: ec1d1ad766915e22d0b574ebba34f72ee1a5196bb1b2180a5921496617c96192
 *
 */


package xyz.kenabot.interfaces

import net.dv8tion.jda.api.events.message.MessageReceivedEvent

interface DevCommand {
    fun hook(event: MessageReceivedEvent, args: List<String>)
    val name: String
    val description: String
}