/*
 * File:          WebhookManager.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\managers\WebhookManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-11-14 19:25:55 +01:00
 *
 * SHA-256: e3164de0f115dfddf6500d03e077513899b44b655306ebd8ca46ec9717bfc5ca
 *
 */


package xyz.kenabot.managers

import net.dv8tion.jda.api.entities.MessageEmbed
import xyz.kenabot.Globals
import xyz.kenabot.config.Env
import xyz.kenabot.webhook.WebhookClient
import xyz.kenabot.webhook.WebhookClientBuilder
import xyz.kenabot.webhook.send.WebhookEmbed
import xyz.kenabot.webhook.send.WebhookEmbedBuilder

object WebhookManager {

    private val builder: WebhookClientBuilder = WebhookClientBuilder(Env.WEBHOOK_URL)
    private var webhook: WebhookClient?

    init {
        webhook = if (Globals.webhookEnabled)
            builder.build()
        else
            null
    }

    fun send(messageEmbed: MessageEmbed) {
        if (!Globals.webhookEnabled)
            return

        val embed = WebhookEmbedBuilder.fromJDA(messageEmbed)
        send(embed.build())
    }

    private fun send(embed: WebhookEmbed) {
        if (!Globals.webhookEnabled)
            return

        if (webhook == null || webhook!!.isShutdown)
            webhook = builder.build()

        webhook!!.send(embed)
    }

    fun send(message: String) {
        if (!Globals.webhookEnabled)
            return

        if (webhook == null || webhook!!.isShutdown)
            webhook = builder.build()

        webhook!!.send(message)
    }

}