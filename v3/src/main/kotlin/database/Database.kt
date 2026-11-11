/*
 * File:          Database.kt
 * Path:          v3\src\main\kotlin\database\Database.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-08 19:37:19 +01:00
 *
 * SHA-256: 4630460969fc46173ec92c9bfa046aebabdcdbe1b9916ea7931d9aa2d996646c
 *
 */


package database

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoDatabase
import config.Env
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory

object Database {

    var mainDatabase: MongoDatabase? = null
    var syncDatabase: MongoDatabase? = null
    var statsDatabase: MongoDatabase? = null

    private val logger = LoggerFactory.getLogger("Database")

    fun load(shardManager: ShardManager) {

        val mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(Env.MONGODB_URL!!))
            .build()
        val mongoClient = MongoClients.create(mongoClientSettings)
        logger.info("Conectado a MongoDB (${shardManager.getShardById(0)!!.selfUser.name.replace(" ", "_")}, Stats)")
        val database = mongoClient.getDatabase(shardManager.getShardById(0)!!.selfUser.name.replace(" ", "_"))
        val statDatabase = mongoClient.getDatabase("Stats")

        if (shardManager.getShardById(0)!!.selfUser.name.replace(" ", "_") != "KenaBot")
            syncDatabase = mongoClient.getDatabase("KenaBot")

        mainDatabase = database
        statsDatabase = statDatabase

    }
}