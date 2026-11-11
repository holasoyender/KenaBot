/*
 * File:          Error.kt
 * Path:          v3\src\main\kotlin\thirdparty\errors\Error.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 8a0df1d5897c6b6c11eea13fd3894610fe4eec1d95babf26cfc3e4dea7b3af09
 *
 */


package thirdparty.errors

//import club.minnced.discord.webhook.send.WebhookEmbed
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
//import utils.WebHook
//import java.time.Instant
import javax.print.attribute.standard.Severity

class Error(
    message: String,
    severity: Severity,
    exception: Exception
) {

    val message: String
    val code: String
    val severity: Severity
    val exception: Exception

    init {
        this.message = message
        this.code = generateCode()
        this.severity = severity
        this.exception = exception
    }

    constructor(message: String, severity: Severity) : this(message, severity, Exception())

    fun push() {
        /*val embed = WebhookEmbedBuilder()
            .setAuthor(WebhookEmbed.EmbedAuthor("Error reportado por el cliente", "https://cdn.discordapp.com/attachments/1026084700189638738/1069703631399559168/emoji..png", null))
            .setDescription("Se ha encontrado un error en el cliente y se le ha dado la siguiente ID ```$code```")
            .addField(WebhookEmbed.EmbedField(true, "Mensaje", message))
            .addField(WebhookEmbed.EmbedField(true, "Severidad", severity.toString()))
            .addField(WebhookEmbed.EmbedField(false, "Excepción", "```$exception```"))
            .setColor(0xED4245)
            .setTimestamp(Instant.now())
            .build()

        WebHook.send(embed)*/
    }

    companion object {
        private fun generateCode(): String {
            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
            val code = StringBuilder()
            for (i in 0..7) {
                val index = (chars.length * Math.random()).toInt()
                code.append(chars[index])
            }
            return "ERR-$code"
        }
    }
}