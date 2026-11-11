/*
 * File:          Block.kt
 * Path:          v3\src\main\kotlin\commands\dev\Block.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 1c4c2454c6e53672ff84455b6d65450b458be6cd90d9efaf0d12cd604e81120f
 *
 */


package commands.dev

import database.schema.Guild
import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import utils.Constants.formatEmoji

class Block: DevCommand {
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

            var reason = "Sin razón"
            if (args.size >= 3)
                reason = args.subList(2, args.size).joinToString(" ")


            val guildConfig = Guild.get(guild.id)
            if (guildConfig.blockedEnabled) {
                event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor ya está bloqueado").queue()
                return
            } else {
                guildConfig.blockedEnabled = true
                guildConfig.blockedReason = reason
                event.message.reply(formatEmoji(Constants.Emojis.success) + "El servidor ha sido bloqueado con razón `$reason`").queue()
            }

        } catch (e: Exception) {
            event.message.reply(formatEmoji(Constants.Emojis.error) + "No se pudo encontrar el servidor").queue()
            return
        }
    }

    override val name: String
        get() = "block"
    override val description: String
        get() = "Bloquear a un servidor de usar el bot."
}