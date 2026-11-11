/*
 * File:          Together.kt
 * Path:          v3\src\main\kotlin\commands\Together.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: bcecb367e4287fa3b5e6d280cb8b3fc0fd0d23cc5d688937e94967b535ca3816
 *
 */


package commands

import database.schema.Guild
import functions.DiscordTogether
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel
import net.dv8tion.jda.api.entities.emoji.Emoji
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu
import net.dv8tion.jda.api.interactions.components.selections.SelectOption
import net.dv8tion.jda.internal.entities.emoji.CustomEmojiImpl
import org.json.JSONObject
import utils.Constants
import utils.Constants.formatEmoji

class Together: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {
        val channel = event.member!!.voiceState?.channel

        if (channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_required")).setEphemeral(true).queue()
            return
        }

        if(channel.type != ChannelType.VOICE) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_required")).setEphemeral(true).queue()
            return
        }

        val activity = event.getOption("actividad")?.asString

        if(activity != null) {

            if(!event.guild!!.selfMember.hasPermission(channel, Permission.CREATE_INSTANT_INVITE)) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.together.no_permission")).setEphemeral(true).queue()
                return
            }

            val activityID: String = DiscordTogether.getAppID(activity)
            (channel as VoiceChannel).createInvite().setTargetApplication(activityID).queue {

                if(it == null) {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_activity")).setEphemeral(true).queue()
                    return@queue
                }

                if(it.code == "50013") {
                    event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_activity")).setEphemeral(true).queue()
                    return@queue
                }

                val name: String =  DiscordTogether.getName(activity)

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setAuthor(lang.get("commands.together.embed_title").replace("%s%", name))
                    .setDescription(lang.get("commands.together.embed_description").replace("%s%", "https://discord.gg/${it.code}"))

                event.replyEmbeds(embed.build()).addActionRow(
                    Button.link("https://discord.gg/${it.code}", lang.get("commands.together.button")).withEmoji(Emoji.fromUnicode("\uD83D\uDE80"))
                ).queue()

            }
        } else {

            val apps = DiscordTogether.getAllApps()
            val options: ArrayList<JSONObject> = ArrayList()

            for(app in apps) {
                val option = JSONObject()
                    .put("value", "cmd:together:${app}:${event.user.id}")
                    .put("label", DiscordTogether.getName(app))
                    .put("description", "Lanzar una actividad de ${DiscordTogether.getName(app)}")
                options.add(option)
            }

            val embed = EmbedBuilder()
                .setColor(config.embedColor)
                .setAuthor(lang.get("commands.together.choose_embed_title"), null, event.user.avatarUrl)
                .setDescription(lang.get("commands.together.choose_embed_description"))

            event.replyEmbeds(embed.build()).addActionRow(
                StringSelectMenu.create("cmd:together")
                    .setMaxValues(1)
                    .setMinValues(0)
                    .setPlaceholder(lang.get("commands.together.choose_embed_placeholder"))
                    .addOptions(
                        options.map {
                            SelectOption.of(it.getString("label"), it.getString("value"))
                                .withEmoji(Emoji.fromCustom(CustomEmojiImpl("pin", 997598523828736071, false)))
                                .withDescription(it.getString("description"))
                        }
                    ).build()
            ).queue()
        }
    }

    override val name: String
        get() = "together"
    override val description: String
        get() = "Empezar una actividad de Discord Together."
    override val category: String
        get() = "Misc"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<actividad>"
    override val premium: Boolean
        get() = false
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Empezar una actividad de Discord Together.",
                    DiscordLocale.ENGLISH_US to "Start a Discord Together activity.",
                    DiscordLocale.ENGLISH_UK to "Start a Discord Together activity.",
                    DiscordLocale.FRENCH to "Commencer une activité Discord Together.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Iniciar uma atividade do Discord Together.",
                    DiscordLocale.ITALIAN to "Avvia un'attività di Discord Together.",
                    DiscordLocale.GERMAN to "Starten Sie eine Discord Together-Aktivität.",
                )
            )
            .addOptions(
                OptionData(OptionType.STRING, "actividad", "Actividad a lanzar")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Actividad a lanzar",
                            DiscordLocale.ENGLISH_US to "Activity to launch",
                            DiscordLocale.ENGLISH_UK to "Activity to launch",
                            DiscordLocale.FRENCH to "Activité à lancer",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Atividade para lançar",
                            DiscordLocale.ITALIAN to "Attività da lanciare",
                            DiscordLocale.GERMAN to "Aktivität zu starten",
                        )
                    )
                    .setRequired(false)
                    .addChoice("YouTube", "youtube")
                    .addChoice("Poker", "poker")
                    .addChoice("Betrayal", "betrayal")
                    .addChoice("Fishing", "fishing")
                    .addChoice("LetterTile", "lettertile")
                    .addChoice("WordSnack", "wordsnack")
                    .addChoice("DoodleCrew", "doodlecrew")
                    .addChoice("AwkWord", "awkword")
                    .addChoice("SpellCast", "spellcast")
                    .addChoice("Checkers", "checkers")
                    .addChoice("PuttParty", "puttparty")
                    .addChoice("SketchHeads", "sketchheads")
                    .addChoice("Blazing 8s", "ocho")
                    .addChoice("Land-io", "land")
                    .addChoice("Bobble League", "bobble")
                    .addChoice("Ask Away", "askaway")
                    .addChoice("Know What I Meme", "meme")
            )
}