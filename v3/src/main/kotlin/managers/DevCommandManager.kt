/*
 * File:          DevCommandManager.kt
 * Path:          v3\src\main\kotlin\managers\DevCommandManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 3049e869eeca63f64525ff8b0847599a10a4626b54bc918a2a31b292dd9d6e54
 *
 */


package managers

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import java.util.ArrayList

class DevCommandManager {

    val commands: MutableList<DevCommand> = ArrayList<DevCommand>()
    companion object { var instance: DevCommandManager? = null }
    init {
        DevCommandInitializer(this)

        if (instance != null)
            throw Error("Ya existe una instancia de DevCommandManager!")
        else
            instance = this
    }

    fun registerCommand(cmd: DevCommand) {
        val nameFound = commands.stream().anyMatch {
            it.name.lowercase() == cmd.name
        }
        require(!nameFound) { "Ya existe un comando con ese nombre!" }
        commands.add(cmd)
    }

    private fun getCommand(search: String): DevCommand? {
        val searchLower = search.lowercase()
        for (command in commands) {
            if (command.name == searchLower) {
                return command
            }
        }
        return null
    }

    fun run(event: MessageReceivedEvent) {
        if (event.isFromGuild) return
        if (event.author.isBot) return
        if(!Constants.OWNER_IDS.contains(event.author.id)) return

        val prefix = "!"

        val message = event.message.contentRaw
        if(!message.startsWith(prefix)) return

        val args = message.substring(prefix.length).split(" ")
        val invoke = args[0].lowercase()
        val cmd: DevCommand? = getCommand(invoke)

        if (cmd != null) {

            try {
                cmd.run(event, args)
            } catch (e: Exception) {
                event.message.reply("${Constants.Emojis.error}  Ha ocurrido un error al ejecutar el comando").queue()
                e.printStackTrace()
            }
        } else {
            event.message.reply("${Constants.Emojis.error}  No he podido encontrar el comando `$invoke`").queue()
            return
        }
    }
}