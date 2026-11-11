/*
 * File:          Guilds.kt
 * Path:          v3\src\main\kotlin\events\Guilds.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-22 19:31:59 +02:00
 *
 * SHA-256: 824d19928d7624c2cc85009bff7466821954845a8c3b55e8870e42cda7dd7370
 *
 */


package events

//import club.minnced.discord.webhook.send.WebhookEmbed.EmbedAuthor
//import club.minnced.discord.webhook.send.WebhookEmbed.EmbedField
//import club.minnced.discord.webhook.send.WebhookEmbedBuilder
import database.schema.Guild
import database.schema.Whitelist
import langs.LangUtils
import metrics.DailyStats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.audit.ActionType
import net.dv8tion.jda.api.events.guild.GuildJoinEvent
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.components.buttons.Button
//import utils.WebHook
import java.awt.Color

class Guilds: ListenerAdapter() {

    override fun onGuildJoin(event: GuildJoinEvent) {

        DailyStats.incrementGuilds(event.jda)

        if(Whitelist.isWhitelistEnabled()) {
            if(!Whitelist.isWhitelisted(event.guild.id)) {

                /*val embed = WebhookEmbedBuilder()
                    .setColor(0x47EB72)
                    .setAuthor(
                        EmbedAuthor(
                            "Servidor sin whitelist",
                            if (event.guild.iconUrl != null) event.guild.iconUrl else event.jda.selfUser.avatarUrl,
                            null
                        )
                    )
                    .setThumbnailUrl(if (event.guild.iconUrl != null) event.guild.iconUrl else event.jda.selfUser.avatarUrl)
                    .addField(EmbedField(true, "Nombre del servidor", event.guild.name + "(`" + event.guild.id + "`)"))
                    .addField(EmbedField(true, "Miembros", event.guild.memberCount.toString()))
                    .addField(EmbedField(true, "Propietario", "`" + event.guild.ownerId + "`"))
                WebHook.send(embed.build())*/

                event.guild.leave().queue()
                return
            }
        }

        //val botGuilds = event.jda.shardManager!!.guilds

        /*val embed = WebhookEmbedBuilder()
            .setColor(0x47EB72)
            .setAuthor(
                EmbedAuthor(
                    "Me han añadido a un servidor",
                    if (event.guild.iconUrl != null) event.guild.iconUrl else event.jda.selfUser.avatarUrl,
                    null
                )
            )
            .addField(EmbedField(true, "Nombre del servidor", event.guild.name + "(`" + event.guild.id + "`)"))
            .addField(EmbedField(true, "Miembros", event.guild.memberCount.toString()))
            .addField(EmbedField(true, "Propietario", "`" + event.guild.ownerId + "`"))
            .addField(
                EmbedField(
                    true,
                    "Servidores totales",
                    "${botGuilds.size}(`${botGuilds.sumOf { it.memberCount }} usuarios totales`)"
                )
            )
            .addField(EmbedField(true, "Bot", event.jda.selfUser.asTag + "(`" + event.jda.selfUser.id + "`)"))
        WebHook.send(embed.build())*/

        //if(event.guild.memberCount > 30000)
        //    WebHook.send("<@&964912146641195068>")

        if(event.guild.selfMember.hasPermission(Permission.VIEW_AUDIT_LOGS)) {
            try {
                event.guild.retrieveAuditLogs().type(ActionType.BOT_ADD).limit(1).queue({ logs ->
                    val log = logs.firstOrNull()
                    if(log != null) {

                        val lang = when(event.guild.locale) {
                            DiscordLocale.ITALIAN -> Pair("it-IT", LangUtils("it-IT"))
                            DiscordLocale.FRENCH -> Pair("fr-FR", LangUtils("fr-FR"))
                            DiscordLocale.ENGLISH_US -> Pair("en-US", LangUtils("en-US"))
                            DiscordLocale.ENGLISH_UK -> Pair("en-GB", LangUtils("en-GB"))
                            DiscordLocale.PORTUGUESE_BRAZILIAN -> Pair("pt-PT", LangUtils("pt-PT"))
                            DiscordLocale.SPANISH -> Pair("es-ES", LangUtils("es-ES"))
                            DiscordLocale.GERMAN -> Pair("de-DE", LangUtils("de-DE"))
                            DiscordLocale.UKRAINIAN -> Pair("uk-UA", LangUtils("uk-UA"))
                            else -> Pair("es-ES", LangUtils("es-ES"))
                        }

                        if(lang.first != "es-ES") {
                            val config = Guild.defaultConfig(event.guild.id)
                            config.language = lang.first
                            config.save()
                        }

                        log.user?.openPrivateChannel()?.queue({
                            val mdEmbed = EmbedBuilder()
                                .setColor(Color.decode("#2f3136"))
                                .setAuthor(lang.second.get("events.guildCreate.embed_author"), null, event.jda.selfUser.avatarUrl)
                                .setDescription(lang.second.get("events.guildCreate.embed_description").replace("%s%", event.guild.name).replace("%t%", lang.first).replace("%u%", "`${lang.second.get("name")}` ${lang.second.get("flag")}"))
                                .setThumbnail("https://cdn.discordapp.com/attachments/912371493944586320/1062464402500104242/kena_music.png")
                            it.sendMessageEmbeds(mdEmbed.build()).setActionRow(
                                Button.link("https://kenabot.xyz", lang.second.get("events.guildCreate.buttons.web", true)),
                                Button.link("https://kenabot.xyz/support", lang.second.get("events.guildCreate.buttons.support", true)),
                                Button.link("https://kenabot.xyz#bots", lang.second.get("events.guildCreate.buttons.add", true)),
                            ).queue({}, {})
                        }, {})
                    }
                }, {})
            } catch (_: Exception) {}
        }
    }

    override fun onGuildLeave(event: GuildLeaveEvent) {

        DailyStats.decrementGuilds(event.jda)

        /*val botGuilds = event.jda.shardManager!!.guilds

        val embed = WebhookEmbedBuilder()
            .setColor(0xFA753B)
            .setAuthor(
                EmbedAuthor(
                    "Me han echado de un servidor",
                    if (event.guild.iconUrl != null) event.guild.iconUrl else event.jda.selfUser.avatarUrl,
                    null
                )
            )
            .addField(EmbedField(true, "Nombre del servidor", event.guild.name + "(`" + event.guild.id + "`)"))
            .addField(EmbedField(true, "Miembros", event.guild.memberCount.toString()))
            .addField(EmbedField(true, "Propietario", "`" + event.guild.ownerId + "`"))
            .addField(
                EmbedField(
                    true,
                    "Servidores totales",
                    "${botGuilds.size}(`${botGuilds.sumOf { it.memberCount }} usuarios totales`)"
                )
            )
            .addField(EmbedField(true, "Bot", event.jda.selfUser.asTag + "(`" + event.jda.selfUser.id + "`)"))
        WebHook.send(embed.build())
        if(event.guild.memberCount > 30000)
            WebHook.send("<@&964912146641195068>")*/
    }
}