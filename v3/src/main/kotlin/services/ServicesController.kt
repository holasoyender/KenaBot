/*
 * File:          ServicesController.kt
 * Path:          v3\src\main\kotlin\services\ServicesController.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-18 19:41:50 +02:00
 *
 * SHA-256: 6f92121eff81f7ced3b811082dd92f5c081bdecf555309e1687ff400c8bdddf9
 *
 */


package services

import database.Database
import database.Redis
import langs.LangManager
import net.dv8tion.jda.api.sharding.ShardManager
import thirdparty.spotify.RotatingKeys
import ws.CentralWebSocket

object ServicesController {

    fun start(shardManager: ShardManager) {

        RotatingKeys.load()
        Redis.load()

        LangManager.load()
        Database.load(shardManager)
        //NodeManager.load(shardManager)

        CentralWebSocket.load(shardManager.getShardById(0)!!.selfUser.name.replace(Regex(" "), ""))

        ExpiredPremium.startLoop(shardManager)
        ExpiredSessions.startLoop()
        Check247.startLoop(shardManager)
        PlayerGarbageCollector.startLoop()

        metrics.start(shardManager)
    }
}