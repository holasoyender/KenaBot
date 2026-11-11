/*
 * File:          InternalEventHandler.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\events\InternalEventHandler.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-11-14 19:25:55 +01:00
 *
 * SHA-256: 9cf33b88971bf0833a79f42cac07e3d2e6e2c35438136fe087845930e7318f48
 *
 */


package xyz.kenabot.events

import dev.minn.jda.ktx.events.listener
import dev.minn.jda.ktx.messages.Embed
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.events.GatewayPingEvent
import net.dv8tion.jda.api.events.session.*
import xyz.kenabot.Globals
import xyz.kenabot.config.Constants
import xyz.kenabot.managers.CommandRegistry
import xyz.kenabot.managers.WebhookManager

class InternalEventHandler(
    manager: net.dv8tion.jda.api.sharding.ShardManager
) {

    private val logger by SLF4J

    init {
        manager.listener<ReadyEvent> { event ->
            logger.info("Shard ${event.jda.shardInfo.shardId} is ready")

            if (Globals.isDebug)
                logger.debug("Session ${event.jda.shardInfo.shardId} is ready (0x${event.responseNumber})")

            if (event.jda.shardInfo.shardId == 0) {

                val commands = CommandRegistry.instance!!.commands.map { it.metadata }
                event.jda.updateCommands().addCommands(commands).queue()
                logger.info("Registered ${commands.size} slash commands")

            }
            val embed = Embed {
                color = Constants.colors.purple
                author {
                    name = "Shard ${event.jda.shardInfo.shardId} is ready"
                    iconUrl = event.jda.selfUser.avatarUrl
                }
                footer {
                    name = "${event.jda.selfUser.name} - 0x${event.responseNumber}"
                }
            }

            if(event.jda.selfUser.name != "KenaBot Beta" && event.jda.selfUser.name != "KenaDev" && !Globals.isDebug)
                WebhookManager.send(embed)

            event.jda.shardManager!!.setActivity(Activity.customStatus("/help - kenabot.xyz"))

        }

        manager.listener<GatewayPingEvent> { event ->
            if (Globals.isDebug)
                logger.debug("Gateway ping heartbeat has been updated (${event.oldPing} -> ${event.newPing}) 0x${event.responseNumber}")
        }

        manager.listener<SessionDisconnectEvent> { event ->
            logger.warn(
                "Session {} disconnected with code {} ({})",
                event.jda.shardInfo.shardId,
                event.closeCode,
                if (event.isClosedByServer) "closed by server" else "closed by client"
            )

            val embed = Embed {
                color = Constants.colors.red
                author {
                    name = "Session ${event.jda.shardInfo.shardId} disconnected with code ${event.closeCode}"
                    iconUrl = event.jda.selfUser.avatarUrl
                }
                footer {
                    name = "${event.jda.selfUser.name} - 0x${event.responseNumber}"
                }
            }

            if (event.jda.selfUser.name != "KenaBot Beta" && event.jda.selfUser.name != "KenaDev" && !Globals.isDebug)
                WebhookManager.send(embed)
        }

        manager.listener<SessionInvalidateEvent> { event ->
            logger.warn(
                "Session {} invalidated ({}, 0x{})",
                event.jda.shardInfo.shardId,
                event.state.name,
                event.responseNumber,
            )

            val embed = Embed {
                color = Constants.colors.red
                author {
                    name = "Session ${event.jda.shardInfo.shardId} invalidated"
                    iconUrl = event.jda.selfUser.avatarUrl
                }
                footer {
                    name = "${event.jda.selfUser.name} - 0x${event.responseNumber}"
                }
            }

            if (event.jda.selfUser.name != "KenaBot Beta" && event.jda.selfUser.name != "KenaDev" && !Globals.isDebug)
                WebhookManager.send(embed)
        }

        manager.listener<SessionRecreateEvent> { event ->
            logger.warn(
                "Session {} recreated ({}, 0x{})",
                event.jda.shardInfo.shardId,
                event.state.name,
                event.responseNumber
            )
        }

        manager.listener<SessionResumeEvent> { event ->
            logger.warn(
                "Session {} resumed ({}, 0x{})",
                event.jda.shardInfo.shardId,
                event.state.name,
                event.responseNumber
            )
        }
    }
}