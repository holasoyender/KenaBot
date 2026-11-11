/*
 * File:          Message.kt
 * Path:          v3\src\main\kotlin\events\Message.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 3f1f95ac5e5400101969fa2a3c35281d408a08b22316f36efa0b584254d0f838
 *
 */


package events

import database.schema.Guild
import langs.LangUtils
import managers.DevCommandManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.interactions.components.buttons.Button
import utils.Constants.OWNER_IDS

class Message : ListenerAdapter() {

    private val manager: DevCommandManager = DevCommandManager.instance!!

    override fun onMessageReceived(event: MessageReceivedEvent) {
        if (event.author.isBot) return
        if (event.isFromGuild) return

        if(!OWNER_IDS.contains(event.author.id)) {

            val embed = EmbedBuilder()
                .setColor(Guild.defaultConfig().embedColor)
                .setTitle(LangUtils("es-ES").get("events.dm.embed_title").replace("%s%", event.jda.selfUser.name))
                .setDescription(LangUtils("es-ES").get("events.dm.embed_description"))

            event.message.replyEmbeds(embed.build()).setActionRow(
                Button.link("https://kenabot.xyz", "\uD83D\uDDA5️"+LangUtils("es-ES").get("events.guildCreate.buttons.web")),
                Button.link("https://kenaboy.xyz/support", "⚙️"+LangUtils("es-ES").get("events.guildCreate.buttons.support") ),
                Button.link("https://kenabot.xyz#bots", "\uD83D\uDD27"+LangUtils("es-ES").get("events.guildCreate.buttons.add"))
            ).mentionRepliedUser(false).queue()

            return
        } else {
            manager.run(event)
        }
    }
}