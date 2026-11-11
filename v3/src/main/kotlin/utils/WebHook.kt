/*
 * File:          WebHook.kt
 * Path:          v3\src\main\kotlin\utils\WebHook.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 2d71c47a4149a336e820561d137c44deb81f13f03da57a94f4c1c0a33f95a73f
 *
 */


package utils
/*
import club.minnced.discord.webhook.WebhookClient
import club.minnced.discord.webhook.WebhookClientBuilder
import club.minnced.discord.webhook.send.WebhookEmbed
import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import config.Env
import net.dv8tion.jda.api.entities.MessageEmbed

object WebHook {

    private val builder: WebhookClientBuilder = WebhookClientBuilder(Env.WEBHOOK_URL!!)
    private var webhook: WebhookClient

    init {
        webhook = builder.build()
    }

    fun send(messageEmbed: MessageEmbed) {
        if(webhook.isShutdown)
            webhook = builder.build()

        val embed = WebhookEmbedBuilder.fromJDA(messageEmbed)
        send(embed.build())
    }

    fun send(embed: WebhookEmbed) {
        if(webhook.isShutdown)
            webhook = builder.build()

        webhook.send(embed)
    }

    fun send(message: String) {
        if(webhook.isShutdown)
            webhook = builder.build()

        webhook.send(message)
    }
}*/
object WebHook {

    fun send(message: String) {
        println(message)
    }
}