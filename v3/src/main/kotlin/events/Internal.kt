/*
 * File:          Internal.kt
 * Path:          v3\src\main\kotlin\events\Internal.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 5a06ec9adbc48edc58a0d3cfd1f974fa7f1f380bc3348cac779eaf9ff1fba939
 *
 */


package events

import atenea.NodeManager
import atenea.NodeManager.nodeManager
//import club.minnced.discord.webhook.send.WebhookEmbed
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import database.Redis
import enums.States
import functions.LastState
import managers.CommandManager
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.session.ReadyEvent
import net.dv8tion.jda.api.events.session.SessionRecreateEvent
import net.dv8tion.jda.api.events.session.ShutdownEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import org.slf4j.LoggerFactory
import restarter.BootServiceW
//import utils.WebHook

class Internal : ListenerAdapter() {

    private val logger = LoggerFactory.getLogger("Shards")

    override fun onReady(event: ReadyEvent) {
        LastState.setLastState(States.SHARD_READY)
        logger.info("Shard " + event.jda.shardInfo.shardId + " lanzada")
        /*val embed: WebhookEmbed = WebhookEmbedBuilder()
            .setColor(0x6600F4)
            .setAuthor(
                WebhookEmbed.EmbedAuthor(
                    "Shard " + event.jda.shardInfo.shardId + " lanzada",
                    event.jda.selfUser.avatarUrl,
                    null
                )
            )
            .setFooter(WebhookEmbed.EmbedFooter("Bot: " + event.jda.selfUser.name, ""))
            .build()
        if(event.jda.selfUser.name != "KenaBot Beta" && event.jda.selfUser.name != "KenaDev")
            WebHook.send(embed)*/

        val allCommands: MutableList<CommandData> = mutableListOf()
        CommandManager.instance!!.commands.filter { it.enabled }.forEach {
            allCommands.add(it.metadata)
        }

        if(event.jda.shardInfo.shardId == 0) {
            event.jda.updateCommands().addCommands(allCommands).queue()
            logger.info("Slash commands updated successfully")
            Redis.userId = event.jda.selfUser.id

            nodeManager.setUserId(event.jda.selfUser.id)
            nodeManager.numShards = event.jda.shardInfo.shardTotal
            NodeManager.loadNodes(nodeManager, event.jda.selfUser.name.replace(Regex(" "), ""))
        }
        event.jda.presence.setPresence(Activity.watching("kenabot.xyz"), false)

        BootServiceW(event.jda.shardInfo.shardId, event.jda.guilds.map { it.id }.toTypedArray(), event.jda).resumePlayers()

    }
    override fun onSessionRecreate(event: SessionRecreateEvent) {
        LastState.setLastState(States.SHARD_CONNECTED)
        /*val embed: WebhookEmbed = WebhookEmbedBuilder()
            .setColor(0x6600F4)
            .setAuthor(
                WebhookEmbed.EmbedAuthor(
                    "Shard " + event.jda.shardInfo.shardId + " conectada",
                    event.jda.selfUser.avatarUrl,
                    null
                )
            )
            .setFooter(WebhookEmbed.EmbedFooter("Bot: " + event.jda.selfUser.name, ""))
            .build()
        if(event.jda.selfUser.name != "KenaBot Beta" && event.jda.selfUser.name != "KenaDev")
            WebHook.send(embed)*/
        logger.info("Shard ${event.jda.shardInfo.shardId} conectada")
        event.jda.presence.setPresence(Activity.watching("kenabot.xyz"), false)

    }

    override fun onShutdown(event: ShutdownEvent) {
        LastState.setLastState(States.SHARD_DISCONNECTED)
        /*val embed: WebhookEmbed = WebhookEmbedBuilder()
            .setColor(0xEA4F4C)
            .setAuthor(WebhookEmbed.EmbedAuthor("Bot apagado", event.jda.selfUser.avatarUrl, null))
            .setFooter(WebhookEmbed.EmbedFooter("Bot: " + event.jda.selfUser.name, ""))
            .build()
        WebHook.send(embed)*/
    }
}