/*
 * File:          ExpiredPremium.kt
 * Path:          v3\src\main\kotlin\services\ExpiredPremium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: ad3293609a6e2f1d2a65adaae066e06afc94cb92edc112e838aca30819ef91ce
 *
 */


package services

//import club.minnced.discord.webhook.WebhookClient
//import club.minnced.discord.webhook.WebhookClientBuilder
//import club.minnced.discord.webhook.send.WebhookEmbed
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
//import config.Env
import database.schema.Guild
import net.dv8tion.jda.api.sharding.ShardManager
import org.slf4j.LoggerFactory
import kotlin.concurrent.thread

object ExpiredPremium {

    //private val webhookClient: WebhookClient = WebhookClientBuilder(Env.WEBHOOK_URL!!).build()
    private val logger = LoggerFactory.getLogger(ExpiredPremium::class.java)

    fun startLoop(shardManager: ShardManager) {

        thread(isDaemon = true, name = "ExpiredPremiumService") {
            while (true) {
                val allGuilds = Guild.getAllPremium()
                allGuilds.forEach { guild ->
                    if (guild.premiumExpires == "Forever") return@forEach

                    val expires = guild.premiumExpires as Number
                    val currentTime = System.currentTimeMillis()
                    val expiresTime = expires.toLong()

                    if (currentTime >= expiresTime) {
                        guild.premiumEnabled = false
                        guild.premiumExpires = ""
                        guild.save()

                        /*val cacheGuild = shardManager.getGuildById(guild.guildID)
                        if (cacheGuild == null) {
                            val embed = WebhookEmbedBuilder()
                                .setColor(0xFE5B50)
                                .setAuthor(
                                    WebhookEmbed.EmbedAuthor(
                                        "Premium expirado en un servidor",
                                        shardManager.getShardById(0)!!.selfUser.avatarUrl,
                                        null
                                    )
                                )
                                .setDescription("Se ha desactivado el premium en el servidor **Desconocido** porque ha expirado.")
                            webhookClient.send(embed.build())
                        } else {
                            val embed = WebhookEmbedBuilder()
                                .setColor(0xFE5B50)
                                .setAuthor(
                                    WebhookEmbed.EmbedAuthor(
                                        "Premium expirado en un servidor",
                                        shardManager.getShardById(0)!!.selfUser.avatarUrl,
                                        null
                                    )
                                )
                                .setDescription("Se ha desactivado el premium en el servidor **${cacheGuild.name}** porque ha expirado.")
                                .setThumbnailUrl(
                                    cacheGuild.iconUrl ?: shardManager.getShardById(0)!!.selfUser.avatarUrl
                                )
                            webhookClient.send(embed.build())
                        }*/
                        logger.info("Premium expirado en el servidor ${guild.guildID}")
                    }
                }
                Thread.sleep(120000)
            }
        }
    }
}