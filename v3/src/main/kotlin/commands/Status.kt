/*
 * File:          Status.kt
 * Path:          v3\src\main\kotlin\commands\Status.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: e1fc5dd8f285fee94c14872795ef6a431ec19ab3a555de9ffc6c7b2ed6f824c0
 *
 */


package commands

import database.schema.Guild
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import utils.Constants
import java.text.DecimalFormat

class Status: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        event.reply("${Constants.Emojis.loading}  Cargando información interna...").queue {

            val formatter = DecimalFormat("#,###")
            val embed = EmbedBuilder()
                .setAuthor(lang.get("commands.status.embed_title").replace("%s%", event.jda.selfUser.name), null, event.jda.selfUser.avatarUrl)
                .setDescription(lang.get("commands.status.embed_description").replace("%s%", "${event.jda.shardInfo.shardId + 1}/${event.jda.shardInfo.shardTotal}"))
                .setColor(config.embedColor)

            val shards = event.jda.shardManager!!.shards.sortedBy { s -> s.shardInfo.shardId }
            val shardsInfo: MutableList<Pair<String, String>> = mutableListOf()
            for (shard in shards) {
                val emoji = when(shard.status) {
                    JDA.Status.INITIALIZED,
                    JDA.Status.CONNECTED-> Constants.Emojis.online
                    JDA.Status.DISCONNECTED,
                    JDA.Status.SHUTTING_DOWN,
                    JDA.Status.SHUTDOWN,
                    JDA.Status.FAILED_TO_LOGIN -> Constants.Emojis.offline
                    else -> Constants.Emojis.dnd
                }
                shardsInfo.add(Pair("$emoji Shard ${shard.shardInfo.shardId + 1}", """
                    ```
                    Host: shard-${shard.shardInfo.shardId + 1}.kenabot.xyz
                    Servers: ${shard.guilds.size}
                    Users: ${formatter.format(shard.guilds.sumOf { s -> s.memberCount })}
                    Heartbeat: ${shard.gatewayPing}ms
                    Status: ${shard.status.name}
                    ```
                """.trimIndent().replace(Regex(","), ".")))
            }

            shardsInfo.take(9).forEach { (name, value) ->
                embed.addField(name, value, true)
            }

            embed.setFooter("Status 1/${shardsInfo.chunked(9).size}")

            it.editOriginalEmbeds(embed.build()).setContent("").mentionRepliedUser(false).setActionRow(
                Button.primary("cmd:status:0:back:"+event.user.id, "◀").asDisabled(),
                Button.primary("cmd:status:0:next:"+event.user.id, "▶").withDisabled(shardsInfo.chunked(10).size == 1),
                Button.primary(
                    "cmd:status:1:back:"+event.user.id,
                    Emoji.fromCustom(CustomEmojiImpl("loop", 952242523521294456, false))
                ),
            ).queue()
        }

    }

    override val name: String
        get() = "status"
    override val description: String
        get() = "Estado de las shards del bot"
    override val category: String
        get() = "Bot"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = false
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = ""
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Muestra el estado de las shards del bot.",
                    DiscordLocale.ENGLISH_US to "Shows the status of the bot's shards.",
                    DiscordLocale.ENGLISH_UK to "Shows the status of the bot's shards.",
                    DiscordLocale.FRENCH to "Affiche l'état des shards du bot.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Mostra o status das shards do bot.",
                    DiscordLocale.ITALIAN to "Mostra lo stato delle shards del bot.",
                    DiscordLocale.GERMAN to "Zeigt den Status der Bot-Shards an."
                )
            )
    override val premium: Boolean
        get() = false
}