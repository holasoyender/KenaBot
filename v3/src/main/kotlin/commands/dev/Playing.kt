/*
 * File:          Playing.kt
 * Path:          v3\src\main\kotlin\commands\dev\Playing.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-29 12:08:20 +02:00
 *
 * SHA-256: b44f4dc30bdd92885ca10d27ef1ad8ac55c445867d05aeef10f1bdfe49a5dd23
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import player.Player


class Playing: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        val totalPlayers = Player.musicManagers
        if(totalPlayers.isEmpty()) {
            event.message.reply("No players found").queue()
            return
        }
        val playingPlayers = totalPlayers.filter { it.value.player.playingTrack != null }
        if(playingPlayers.isEmpty()) {
            event.message.reply("No players playing").queue()
            return
        }
        event.message.reply("Players playing: ${playingPlayers.size}").queue()
    }

    override val name: String
        get() = "playing"
    override val description: String
        get() = "Reproductores activos del cliente"
}