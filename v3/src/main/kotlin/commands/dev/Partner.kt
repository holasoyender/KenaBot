/*
 * File:          Partner.kt
 * Path:          v3\src\main\kotlin\commands\dev\Partner.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 61b1c96756f446eda06ded6b226c1f0c77fa96ff11ae52541b01439b823fd497
 *
 */


package commands.dev

//import club.minnced.discord.webhook.send.WebhookEmbed
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import database.schema.Guild
import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import utils.Constants
import utils.Constants.formatEmoji
//import utils.WebHook

class Partner: DevCommand {

    override fun run(event: MessageReceivedEvent, args: List<String>) {
        if (args.size < 2) {

            val initialEmbed = EmbedBuilder()
                .setColor(0x2f3136)
                .setDescription("**Recopilando configuraciones...**")

            event.message.replyEmbeds(initialEmbed.build()).queue { m ->

                val allConfigs = Guild.getAllPremium().filter { it.premiumExpires == "Forever" }

                if (allConfigs.isEmpty()) {
                    val embed = EmbedBuilder()
                        .setColor(0x2f3136)
                        .setDescription("**No he encontrado ningún servidor partner**")
                    m.editMessageEmbeds(embed.build()).queue()

                    return@queue
                }

                val formatted = allConfigs.map { guild ->

                    try {
                        "[${guild.guildID}] (${event.jda.shardManager!!.getGuildById(guild.guildID)?.name ?: "N/A"}) ${
                            event.jda.shardManager!!.getGuildById(
                                guild.guildID
                            )?.memberCount ?: 0
                        } Miembros"
                    } catch (e: Exception) {
                        "[${guild.guildID}] (N/A) 0 Miembros"
                    }
                }

                formatted.chunked(5).forEach { chunk ->
                    if(chunk == formatted.chunked(5)[0]) {
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

                    if (args.size < 3) {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes especificar una ID de servidor")
                            .queue()
                        return
                    }

                    val config = Guild.get(args[2])

                    if (config.premiumEnabled) {
                        if (config.premiumExpires == "Forever") {
                            event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor con ID `${args[2]}` ya tiene partner").queue()
                            return
                        }
                    }

                    /*val cacheGuild = event.jda.shardManager!!.getGuildById(config.guildID)
                    val embed = WebhookEmbedBuilder()
                        .setColor(0xFE5B50)
                    if (cacheGuild == null) {
                        embed.setAuthor(WebhookEmbed.EmbedAuthor("Partner añadido a un servidor", null, null))
                            .setDescription("Se ha activado el partner en el servidor **Desconocido**.")
                    } else {
                        embed.setAuthor(
                            WebhookEmbed.EmbedAuthor(
                                "Partner añadido de un servidor",
                                cacheGuild.iconUrl,
                                null
                            )
                        )
                            .setDescription("Se ha activado el partner en el servidor **${cacheGuild.name}**.")
                    }
                    WebHook.send(embed.build())*/

                    config.premiumEnabled = true
                    config.premiumExpires = "Forever"
                    config.save()

                    event.message.reply(formatEmoji(Constants.Emojis.success) + "Se ha activado el modo partner al servidor con ID `${args[2]}`")
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
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "Debes especificar una ID de servidor")
                            .queue()
                        return
                    }

                    val config = Guild.get(args[2])

                    if (!config.found) {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "No he encontrado ningún servidor con esa ID")
                            .queue()
                        return
                    }

                    if (!config.premiumEnabled) {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor con ID `${args[2]}` no tiene premium").queue()
                        return
                    }

                    if (config.premiumExpires != "Forever") {
                        event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor con ID `${args[2]}` no tiene partner").queue()
                        return
                    }

                    /*val cacheGuild = event.jda.shardManager!!.getGuildById(config.guildID)
                    val embed = WebhookEmbedBuilder()
                        .setColor(0xFE5B50)
                    if (cacheGuild == null) {
                        embed.setAuthor(WebhookEmbed.EmbedAuthor("Partner quitado de un servidor", null, null))
                            .setDescription("Se ha desactivado el partner en el servidor **Desconocido**.")
                    } else {
                        embed.setAuthor(
                            WebhookEmbed.EmbedAuthor(
                                "Partner quitado de un servidor",
                                cacheGuild.iconUrl,
                                null
                            )
                        )
                            .setDescription("Se ha desactivado el partner en el servidor **${cacheGuild.name}**.")
                    }
                    WebHook.send(embed.build())*/

                    config.premiumEnabled = false
                    config.premiumExpires = ""
                    config.save()

                    event.message.reply(formatEmoji(Constants.Emojis.success) + "Se ha desactivado el partner del servidor con ID `${args[2]}`")
                        .queue()

                }
                else -> {

                    val config = Guild.get(args[1])
                    if (config.premiumEnabled) {

                        if (config.premiumExpires == "Forever") {
                            event.message.reply(formatEmoji(Constants.Emojis.success) + "El servidor con ID `${args[1]}` es partner").queue()
                            return
                        } else {
                            event.message.reply(formatEmoji(Constants.Emojis.error) + "El servidor con ID `${args[1]}` no tiene partner")
                                .queue()
                            return
                        }

                    } else
                        event.message.reply("El servidor con ID `${args[1]}` no es partner ni premium").queue()
                }
            }

        }
    }

    override val name: String
        get() = "partner"
    override val description: String
        get() = "Dar información o añadir un servidor a la lista de partners"
}