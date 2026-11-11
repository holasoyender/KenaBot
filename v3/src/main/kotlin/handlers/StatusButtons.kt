/*
 * File:          StatusButtons.kt
 * Path:          v3\src\main\kotlin\handlers\StatusButtons.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-07 01:11:56 +02:00
 *
 * SHA-256: 54b676f7e77904e9c466370d93fe4ee4d7acbcfd6e25e2f274ece708dd8fdb92
 *
 */


package handlers

import atenea.NodeManager
import database.schema.Guild
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import utils.Constants
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.round
import kotlin.time.Duration.Companion.milliseconds

object StatusButtons {

    fun handleAction(event: ButtonInteraction) {

        val args = event.componentId.split(":")

        val action = args[3]
        var page = args[2].toInt()
        val userID = args[4]

        val config = Guild.get(event.guild!!.id)
        val lang = LangUtils(config.language)

        if (event.user.id != userID) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("events.interactionCreate.cant_use_button")).setEphemeral(true).queue()
            return
        }

        val shards = event.jda.shardManager!!.shards.sortedBy { s -> s.shardInfo.shardId }
        val chunks = shards.chunked(9)

        if (action == "next") {
            page += 1
            if (page > chunks.size - 1) {
                page = chunks.size - 1
            }
        }
        if (action == "back") {
            page -= 1
            if (page < 0) {
                page = 0
            }
            if (page > chunks.size - 1)
                page = chunks.size - 1
        }

        val formatter = DecimalFormat("#,###")
        val embed = EmbedBuilder()
            .setAuthor(lang.get("commands.status.embed_title").replace("%s%", event.jda.selfUser.name), null, event.jda.selfUser.avatarUrl)
            .setDescription(lang.get("commands.status.embed_description").replace("%s%", "${event.jda.shardInfo.shardId + 1}/${event.jda.shardInfo.shardTotal}"))
            .setColor(config.embedColor)
            .setFooter("Status ${page+1}/${chunks.size}")

        if (chunks.isNotEmpty())
            chunks[page].forEach { shard ->

                val emoji = when(shard.status) {
                    JDA.Status.INITIALIZED,
                    JDA.Status.CONNECTED-> Constants.Emojis.online
                    JDA.Status.DISCONNECTED,
                    JDA.Status.SHUTTING_DOWN,
                    JDA.Status.SHUTDOWN,
                    JDA.Status.FAILED_TO_LOGIN -> Constants.Emojis.offline
                    else -> Constants.Emojis.dnd
                }
                embed.addField(
                    "$emoji Shard ${shard.shardInfo.shardId + 1}",
                    """
                    ```
                    Host: shard-${shard.shardInfo.shardId + 1}.kenabot.xyz
                    Servers: ${shard.guilds.size}
                    Users: ${formatter.format(shard.guilds.sumOf { s -> s.memberCount })}
                    Heartbeat: ${shard.gatewayPing}ms
                    Status: ${shard.status.name}
                    ```
                """.trimIndent().replace(Regex(","), "."),
                    true
                )
            }

        event.editMessageEmbeds(embed.build()).setActionRow(
            Button.primary("cmd:status:$page:back:" + event.user.id, "◀").withDisabled(page == 0),
            Button.primary("cmd:status:$page:next:" + event.user.id, "▶").withDisabled(page == chunks.size - 1),
            Button.primary(
                "cmd:status:${page +1}:back:"+event.user.id,
                Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))
            ),
        ).queue()
    }

    fun handleNodeAction(event: ButtonInteraction) {

        val args = event.componentId.split(":")

        val action = args[3]
        var page = args[2].toInt()

        val nodes = NodeManager.nodeManager.nodes
        val chunks = nodes.chunked(9)

        if (action == "next") {
            page += 1
            if (page > chunks.size - 1) {
                page = chunks.size - 1
            }
        }
        if (action == "back") {
            page -= 1
            if (page < 0) {
                page = 0
            }
            if (page > chunks.size - 1)
                page = chunks.size - 1
        }

        val embed = EmbedBuilder()
            .setAuthor("Información de los nodos de Atenea", null, event.jda.selfUser.avatarUrl)
            .setDescription("Estos nodos son los que se encargan de reproducir la música en los servidores")
            .setColor(Color.decode("#2f3136"))
            .setFooter("Status ${page+1}/${chunks.size}")

        if (chunks.isNotEmpty())
            chunks[page].forEach { node ->

                val emoji = when {
                    node.isAvailable || node.isOpen -> Constants.Emojis.online
                    node.isClosed || node.isClosing -> Constants.Emojis.offline
                    node.isConnecting -> Constants.Emojis.warning
                    else -> Constants.Emojis.dnd
                }


                val stats = node.stats ?: return@forEach

                embed.addField("$emoji ${node.name}", """
                    ```
                    Load: ${round(stats.ateneaLoad * 100)}% (${round(stats.systemLoad * 100)}% total)
                    Memory: ${round((stats.memUsed / 1024 / 1024).toDouble())}mb/${round((stats.memAllocated / 1024 / 1024).toDouble())}mb
                    Uptime: ${stats.uptime.milliseconds}
                    Players: ${stats.playingPlayers}/${stats.players}
                    Frames per minute: ${stats.avgFramesSentPerMinute} (${stats.avgFramesNulledPerMinute} nulled, ${stats.avgFramesDeficitPerMinute} deficit)
                    ```
                """.trimIndent().replace(Regex(","), "."), true)
            }

        event.editMessageEmbeds(embed.build()).setActionRow(
            Button.primary("cmd:nodes:$page:back", "◀").withDisabled(page == 0),
            Button.primary("cmd:nodes:$page:next", "▶").withDisabled(page == chunks.size - 1),
            Button.primary(
                "cmd:nodes:${page +1}:back",
                Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))
            ),
        ).queue()
    }

}