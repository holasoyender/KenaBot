/*
 * File:          Mongo.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\database\Mongo.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 3a3b897452d4a07d1c484c0b675232cfdb10c19824899f05328435adfa1e7fd8
 *
 */


package xyz.kenabot.database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import dev.minn.jda.ktx.util.SLF4J
import net.dv8tion.jda.api.sharding.ShardManager
import xyz.kenabot.config.Env

object Mongo {

    private val logger by SLF4J

    lateinit var main: MongoDatabase
    lateinit var stats: MongoDatabase
    lateinit var sync: MongoDatabase

    fun connect(shardManager: ShardManager) {

        val uri = ConnectionString(Env.DATABASE_URL)
        val dbName = shardManager.getShardById(0)!!.selfUser.name.replace(" ", "_")

        val config = MongoClientSettings.builder()
            .applyConnectionString(uri)
            .build()

        val client = MongoClient.create(config)
        logger.info("Connected to MongoDB database (${dbName}, Stats) successfully.")

        main =  client.getDatabase(dbName)
        stats = client.getDatabase("Stats")

        if (dbName != "KenaBot")
            sync = client.getDatabase("KenaBot")

    }

}