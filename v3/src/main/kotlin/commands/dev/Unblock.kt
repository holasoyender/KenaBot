/*
 * File:          Unblock.kt
 * Path:          v3\src\main\kotlin\commands\dev\Unblock.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 0548d6b26c88b5816e0c2a8a47032a552df20982de844b5384697a88b2d9aa0f
 *
 */


package commands.dev

import database.schema.Guild
import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import utils.Constants.formatEmoji

class Unblock: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {
        if (args.size < 2) {
            event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes de especificar una ID de servidor").queue()
            return
        }

        val guildID = args[1]
        try {
            val guild = event.jda.shardManager!!.getGuildById(guildID)
            if (guild == null) {
                event.message.reply(formatEmoji(Constants.Emojis.error) + "No se pudo encontrar el servidor").queue()
                return
            }

            val guildConfig = Guild.get(guild.id)
            if (!guildConfig.blockedEnabled) {
                event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor no está bloqueado").queue()
                return
            } else {
                guildConfig.blockedEnabled = false
                guildConfig.blockedReason = ""
                guildConfig.save()
            }

        } catch (e: Exception) {
            event.message.reply(formatEmoji(Constants.Emojis.error) + "No se pudo encontrar el servidor").queue()
            return
        }
    }

    override val name: String
        get() = "unblock"
    override val description: String
        get() = "Desbloquear a un servidor de usar el bot."
}