/*
 * File:          CommandRegistry.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\managers\CommandRegistry.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-11-14 19:25:55 +01:00
 *
 * SHA-256: 034e2cd8e755dd19114789a9f947601e8d61db2101b0a9d07fd25871db837354
 *
 */


package xyz.kenabot.managers

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import xyz.kenabot.Globals.OWNER_IDS
import xyz.kenabot.database.schema.Guild
import xyz.kenabot.interfaces.Command
import xyz.kenabot.io.Clazz

class CommandRegistry {

    val commands: MutableList<Command> = mutableListOf()
    companion object {
        var instance: CommandRegistry? = null
    }

    init {

        if (instance != null)
            throw Exception("CommandRegistry already initialized!")
        else
            instance = this

        commands += Clazz.getSlashCommandsOfClass(Command::class.java)
    }

    fun total() = commands.size

    private fun getCommand(name: String) = commands.firstOrNull { it.name == name }

    suspend fun exec(event: SlashCommandInteractionEvent) {

        val config = if (event.isFromGuild) Guild.get(event.guild!!.idLong) ?: Guild.default  else Guild.default
        val invoke = event.name
        val command = getCommand(invoke)

        if(config.blockedEnabled) {
            if(!OWNER_IDS.contains(event.user.id)) {
                event.reply("blocked").queue()
                return
            }
        }

        if (command != null) {

            if(config.blockedEnabled && !OWNER_IDS.contains(event.user.id)) {
                event.reply("no lol").queue()
            }

        }
    }
}