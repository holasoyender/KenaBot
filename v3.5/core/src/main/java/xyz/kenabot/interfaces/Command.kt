/*
 * File:          Command.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\interfaces\Command.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: b07c793bb1a047a415a222b7ad819f871188e691bc201539fc23a0e8ed0225c0
 *
 */


package xyz.kenabot.interfaces

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import xyz.kenabot.database.schema.Guild
import xyz.kenabot.enums.CommandType
import xyz.kenabot.locale.Language

interface Command {

    fun hook(event: SlashCommandInteractionEvent, config: Guild, lang: Language)

    val name: String
    val description: String
    val category: CommandType

    val enabled: Boolean
    val guildOnly: Boolean
    val premiumOnly: Boolean

    val usage: String
    val metadata: SlashCommandData

}