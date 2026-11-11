/*
 * File:          Command.kt
 * Path:          v3\src\main\kotlin\interfaces\Command.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 7acc5f13183f49b6c2ed7fe5440b8c5b9c088a2d4556273a635b85f5dc3025c2
 *
 */


package interfaces

import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData

interface Command {

    fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils)

    val name: String
    val description: String
    val category: String

    val enabled: Boolean
    val guildOnly: Boolean

    val permissions: List<Permission>

    val usage: String
    val metadata: SlashCommandData

    val premium: Boolean?

}