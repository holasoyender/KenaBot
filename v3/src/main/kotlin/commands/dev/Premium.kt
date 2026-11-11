/*
 * File:          Premium.kt
 * Path:          v3\src\main\kotlin\commands\dev\Premium.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 694f2b8567308904c0b93f0339583d170f2eecedb962d4213a929b41faeff0d5
 *
 */


package commands.dev

//import club.minnced.discord.webhook.send.WebhookEmbed
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import database.schema.Guild
import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.utils.TimeFormat
import utils.Constants
import utils.Constants.formatEmoji
import utils.Time
//import utils.WebHook

import java.util.*

class Premium : DevCommand {

    override fun run(event: MessageReceivedEvent, args: List<String>) {

        if (args.size < 2) {

            val initialEmbed = EmbedBuilder()
                .setColor(0x2f3136)
                .setDescription("**Recopilando configuraciones...**")

            event.message.replyEmbeds(initialEmbed.build()).queue { m ->

                val allConfigs = Guild.getAllPremium().filter { it.premiumExpires != "Forever" }

                if (allConfigs.isEmpty()) {
                    val embed = EmbedBuilder()
                        .setColor(0x2f3136)
                        .setDescription("**No he encontrado ningún servidor premium**")
                    m.editMessageEmbeds(embed.build()).queue()

                    return@queue
                }

                val formatted = allConfigs.map { guild ->
                    val partner = if (guild.premiumExpires == "Forever") "(Partner)" else ""

                    "[${guild.guildID}] (${event.jda.shardManager!!.getGuildById(guild.guildID)?.name ?: "N/A"}) Durante: ${guild.premiumExpires} $partner"
                }

                formatted.chunked(10).forEach { chunk ->
                    if(chunk == formatted.chunked(10)[0]) {
                        m.editMessage("```nim\n${chunk.joinToString("\n")}```").setEmbeds().queue()
                    } else {
                        event.channel.sendMessage("```nim\n${chunk.joinToString("\n")}```").queue()
                    }
                }

            }
        } else {

            when (args[1]) {
                "add",
                "añadir",
                "set",
                "ad",
                "enable",
                "create",
                "crear" -> {

                    val time: String = if (args.size > 2) args[2] else "Forever"
                    val finalTime = Time.ms(time)

                    if (time !== "Forever") {
                        if (finalTime < 1) {
                            event.message.reply(formatEmoji(Constants.Emojis.error) + "El tiempo es inválido").queue()
                            return
                        }
                    }

                    val uuid = UUID.randomUUID().toString().replace(Regex("-"), "")

                    val premium = database.schema.Premium(
                        uuid,
                        time,
                        System.currentTimeMillis(),
                        false,
                        "",
                        null,
                        event.message.author.id,
                        listOf()
                    )
                    premium.save()

                    event.message.reply(formatEmoji(Constants.Emojis.success) + "Se ha creado un nuevo código de premium valido durante **$time**:```${uuid}```")
                        .queue()

                }
                "del",
                "delete",
                "rem",
                "remove",
                "borrar",
                "disable",
                "quitar" -> {

                    if (args.size < 3) {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes especificar un código de premium o una ID de servidor")
                            .queue()
                        return
                    }

                    val config = Guild.get(args[2])
                    if (!config.found) {

                        val premium = database.schema.Premium.get(args[2])
                        if (premium == null) {
                            event.message.reply(formatEmoji(Constants.Emojis.error) + "No he encontrado ningún código de premium/servidor con esa ID")
                                .queue()
                            return
                        } else {
                            val premiumGuild = Guild.get(premium.claimedAt)
                            if (premiumGuild.found) {
                                premiumGuild.premiumEnabled = false
                                premiumGuild.premiumExpires = ""
                                premiumGuild.save()
                            }
                            premium.delete()
                            event.message.reply(formatEmoji(Constants.Emojis.success) + "Se ha eliminado el código de premium con ID `${args[2]}` ${if (premiumGuild.found) "y se ha desactivado el premium en el servidor `${premiumGuild.guildID}`" else ""}")
                                .queue()
                            return
                        }

                    }

                    if (!config.premiumEnabled) {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor con ID `${args[2]}` no tiene premium").queue()
                        return
                    }

                    /*val cacheGuild = event.jda.shardManager!!.getGuildById(config.guildID)
                    val embed = WebhookEmbedBuilder()
                        .setColor(0xFE5B50)
                    if (cacheGuild == null) {
                        embed.setAuthor(WebhookEmbed.EmbedAuthor("Premium quitado de un servidor", null, null))
                            .setDescription("Se ha desactivado el premium en el servidor **Desconocido** manualmente.")
                    } else {
                        embed.setAuthor(
                            WebhookEmbed.EmbedAuthor(
                                "Premium quitado de un servidor",
                                cacheGuild.iconUrl,
                                null
                            )
                        )
                            .setDescription("Se ha desactivado el premium en el servidor **${cacheGuild.name}** manualmente.")
                    }
                    WebHook.send(embed.build())*/

                    config.premiumEnabled = false
                    config.premiumExpires = ""
                    config.save()

                    event.message.reply(formatEmoji(Constants.Emojis.success) + "Se ha desactivado el premium del servidor con ID `${args[2]}`")
                        .queue()

                }
                else -> {

                    val config = Guild.get(args[1])
                    if (config.premiumEnabled) {

                        val time = if (config.premiumExpires == "Forever")
                            "Forever"
                        else {
                            val date = (config.premiumExpires as Double).toLong()
                            TimeFormat.RELATIVE.format(date)
                        }

                        event.message.reply("**${event.jda.shardManager!!.getGuildById(config.guildID)?.name ?: "N/A"}** tiene una subscripción premium que acaba **$time**")
                            .queue()
                    } else
                        event.message.reply("El servidor con ID `${args[1]}` no es premium").queue()
                }
            }

        }
    }

    override val name: String
        get() = "premium"
    override val description: String
        get() = "Dar información o añadir un servidor a la lista de premium"
}