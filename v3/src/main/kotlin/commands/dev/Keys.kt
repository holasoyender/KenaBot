/*
 * File:          Keys.kt
 * Path:          v3\src\main\kotlin\commands\dev\Keys.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 85be49017fc57ed3cba1ad2423b950564872cdac6c1d92154dbb3c72009daaae
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import thirdparty.spotify.RotatingKeys
import thirdparty.spotify.SpotifyKey

class Keys: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        val subCommand = args.getOrNull(1) ?: return event.message.reply("Debes especificar una subcomando").queue()

        when (subCommand) {
            "add" -> {
                val clientId = args.getOrNull(2) ?: return event.message.reply("Debes especificar un ID de cliente\n`keys add <clientId> <clientSecret>`").queue()
                val clientSecret = args.getOrNull(3) ?: return event.message.reply("Debes especificar un secreto de cliente\n`keys add <clientId> <clientSecret>`").queue()

                if (RotatingKeys.keys.any { it.clientId == clientId }) {
                    return event.message.reply("Ya existe una clave con ese ID de cliente").queue()
                }

                val key = SpotifyKey(clientId, clientSecret)
                RotatingKeys.addKey(key)
                event.channel.sendMessage("Clave `$clientId:$clientSecret` añadida con éxito").queue()
            }
            "list" -> event.channel.sendMessage("```${RotatingKeys.keys.joinToString("\n") { key -> key.toString() }}```").queue()
            "remove" -> {
                val clientId = args[2]
                val removed = RotatingKeys.removeKey(clientId)
                if (removed) {
                    event.channel.sendMessage("Clave eliminada de la memoria\nPara que este cambio tenga efecto tras reiniciar, se debe de eliminar desde el archivo `keys/spotify.txt`").queue()
                } else {
                    event.channel.sendMessage("No se ha encontrado la clave").queue()
                }
            }
            "rotate" -> {

                val hasOption = args.getOrNull(2) != null
                if (!hasOption) {
                    RotatingKeys.rotateAll()
                    return event.channel.sendMessage("Claves de acceso rotadas").queue()
                }

                val isSearch = args[2] == "search"
                RotatingKeys.rotate(isSearch)
                event.channel.sendMessage("Clave de acceso ${if (isSearch) "de búsqueda" else "global"} rotada").queue()
            }
            "current" -> {
                val isSearch = args.getOrNull(2) == "search"
                val key = RotatingKeys.getWithoutRotate(isSearch)
                event.channel.sendMessage("Clave de acceso ${if (isSearch) "de búsqueda" else "global"} actual: ||`${key.clientId}:${key.clientSecret}`||").queue()
            }
            else -> event.channel.sendMessage("Subcomando `$subCommand` no reconocido").queue()
        }
    }

    override val name: String
        get() = "keys"
    override val description: String
        get() = "Añadir, listar, eliminar o rotar las claves de acceso"
}