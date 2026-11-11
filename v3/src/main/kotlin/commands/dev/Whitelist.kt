/*
 * File:          Whitelist.kt
 * Path:          v3\src\main\kotlin\commands\dev\Whitelist.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: e97b31bf05615692c33a214a13916ab373c811af695088f37390dcfb5d1cc656
 *
 */


package commands.dev

import database.schema.Whitelist
import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import utils.Constants.formatEmoji

class Whitelist: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        if (args.size < 2) {
            event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes de especificar una opción (`add`/`enable`/`disable`/`remove`)")
                .queue()
            return
        }

        when (args[1]) {

            "enable",
            "habilitar" -> {
                Whitelist.enableWhitelist()
                event.message.reply(formatEmoji(Constants.Emojis.success) + "La lista blanca está habilitada")
                    .queue()
            }

            "disable",
            "deshabilitar" -> {
                Whitelist.disableWhitelist()
                event.message.reply(formatEmoji(Constants.Emojis.success) + "La lista blanca está deshabilitada")
                    .queue()
            }

            "add",
            "añadir" -> {
                if (args.size < 3) {
                    event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes de especificar una ID de servidor")
                        .queue()
                    return
                }
                val serverId = args[2]
                if (Whitelist.isWhitelisted(serverId)) {
                    event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor ya está en la lista blanca")
                        .queue()
                } else {
                    Whitelist.addServer(serverId)
                    event.message.reply(formatEmoji(Constants.Emojis.success) + "El servidor ha sido añadido a la lista blanca")
                        .queue()
                }
            }

            "rem",
            "remove",
            "quitar",
            "remover" -> {
                if (args.size < 3) {
                    event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes de especificar una ID de servidor")
                        .queue()
                    return
                }
                val serverId = args[2]
                if (!Whitelist.isWhitelisted(serverId)) {
                    event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor no está en la lista blanca")
                        .queue()
                } else {
                    Whitelist.removeServer(serverId)
                    event.message.reply(formatEmoji(Constants.Emojis.success) + "El servidor ha sido removido de la lista blanca")
                        .queue()
                }
            }
            else -> {
                event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes de especificar una opción (`add`/`enable`/`disable`/`remove`)")
                    .queue()
                return
            }
        }
    }

    override val name: String
        get() = "whitelist"
    override val description: String
        get() = "Añadir, quitar, desactivar o activar la lista blanca."
}