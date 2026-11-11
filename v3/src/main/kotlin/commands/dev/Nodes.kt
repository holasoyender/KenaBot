/*
 * File:          Nodes.kt
 * Path:          v3\src\main\kotlin\commands\dev\Nodes.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:13:13 +02:00
 *
 * SHA-256: eeaffff3ee7bb3a9ef33eaa21ef2b025dd937ff981d0da70a4098c24c7dc02f8
 *
 */


package commands.dev

import atenea.NodeManager.nodeManager
import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import utils.Constants
import java.awt.Color
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

class Nodes: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        when (args.getOrNull(1)?.lowercase() ?: "help") {
            "add",
            "añadir",
            "set" -> {

                var host =
                    args.getOrNull(2) ?: return event.message.reply("Forma de uso: `node ${args[1]} <URL> <PASSWORD>`")
                        .queue()
                val password =
                    args.getOrNull(3) ?: return event.message.reply("Forma de uso: `node ${args[1]} <URL> <PASSWORD>`")
                        .queue()
                val name = args.getOrNull(4) ?: "Provisional #${nodeManager.nodes.size + 1}"

                if (!host.startsWith("ws://") && !host.startsWith("wss://"))
                    host = "ws://$host"

                val uri = try {
                    java.net.URL(host) // solo para comprobar que es una URL válida

                    java.net.URI(host)
                } catch (e: Exception) {
                    return event.message.reply("URL inválida").queue()
                }

                if (nodeManager.nodes.any { it.remoteUri == uri })
                    return event.message.reply("Ya existe un nodo con esa URL").queue()

                if (nodeManager.nodes.any { it.name == name })
                    return event.message.reply("Ya existe un nodo con ese nombre").queue()

                nodeManager.addNode(name, uri, password, event.jda.selfUser.name.replace(Regex(" "), ""))

                event.message.reply("Nodo añadido correctamente, este cambio no persistirá en caso de reinicio, para ello, añade el nodo a la configuración")
                    .queue()

            }

            "remove",
            "del",
            "delete",
            "quitar",
            "borrar" -> {

                val name = args.getOrNull(2) ?: return event.message.reply("Forma de uso: `node ${args[1]} <nombre>`").queue()

                val node = nodeManager.nodes.firstOrNull { it.name == name }

                if (node == null) {
                    event.message.reply("No existe ningún nodo con ese nombre").queue()
                    return
                }

                nodeManager.removeNode(nodeManager.nodes.indexOf(node))

                event.message.reply("Nodo eliminado correctamente, este cambio no persistirá en caso de reinicio, para ello, elimina el nodo de la configuración")
                    .queue()

            }

            else -> {

                event.message.reply("${Constants.Emojis.loading}  Cargando información interna...").queue {

                    val embed = EmbedBuilder()
                        .setAuthor("Información de los nodos de Atenea", null, event.jda.selfUser.avatarUrl)
                        .setDescription("Estos nodos son los que se encargan de reproducir la música en los servidores")
                        .setColor(Color.decode("#2f3136"))

                    val nodes = nodeManager.nodes
                    val nodeInfo: MutableList<Pair<String, String>> = mutableListOf()

                    for (node in nodes) {

                        val emoji = when {
                            node.isAvailable || node.isOpen -> Constants.Emojis.online
                            node.isClosed || node.isClosing -> Constants.Emojis.offline
                            node.isConnecting -> Constants.Emojis.warning
                            else -> Constants.Emojis.dnd
                        }


                        val stats = node.stats ?: continue

                        nodeInfo.add(
                            Pair(
                                "$emoji ${node.name}", """
                    ```
                    Load: ${round(stats.ateneaLoad * 100)}% (${round(stats.systemLoad * 100)}% total)
                    Memory: ${round((stats.memUsed / 1024 / 1024).toDouble())}mb/${round((stats.memAllocated / 1024 / 1024).toDouble())}mb
                    Uptime: ${stats.uptime.milliseconds}
                    Players: ${stats.playingPlayers}/${stats.players}
                    Frames per minute: ${stats.avgFramesSentPerMinute} (${stats.avgFramesNulledPerMinute} nulled, ${stats.avgFramesDeficitPerMinute} deficit)
                    ```
                """.trimIndent().replace(Regex(","), ".")
                            )
                        )
                    }

                    nodeInfo.take(9).forEach { (name, value) ->
                        embed.addField(name, value, true)
                    }

                    embed.setFooter("Status 1/${nodeInfo.chunked(9).size}")

                    it.editMessageEmbeds(embed.build()).setContent("").mentionRepliedUser(false).setActionRow(
                        Button.primary("cmd:nodes:0:back", "◀").asDisabled(),
                        Button.primary("cmd:nodes:0:next", "▶").withDisabled(nodeInfo.chunked(10).size == 1),
                        Button.primary(
                            "cmd:nodes:1:back",
                            Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))
                        ),
                    ).queue()
                }

            }
        }
    }

    override val name: String
        get() = "nodes"
    override val description: String
        get() = "Information about the Atenea nodes"
}