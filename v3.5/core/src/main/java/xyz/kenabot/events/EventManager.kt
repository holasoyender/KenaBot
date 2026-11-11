/*
 * File:          EventManager.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\events\EventManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: bfd52306bcc000c647b60cb29026d674a94a59ced8b947470eb6a62e2770aae5
 *
 */


package xyz.kenabot.events

import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.sharding.ShardManager

class EventManager(
    shardManager: ShardManager
) {

    private val logger by SLF4J

    init {

        InternalEventHandler(shardManager)

        logger.info("Registered event handlers")
    }
}