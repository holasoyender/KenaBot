/*
 * File:          Filter.kt
 * Path:          v3\src\main\kotlin\commands\Filter.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-06 23:59:04 +02:00
 *
 * SHA-256: 8e7f3813fe5050ca462b9369fdeebf11837b02c6c41b97ef946200fa045cc73d
 *
 */


package commands

import database.schema.Guild
import enums.AudioFilters
import functions.DjRole
import interfaces.Command
import langs.LangUtils
import managers.FilterManager
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData
import player.Player
import utils.Constants
import utils.Constants.formatEmoji

class Filter: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        if (!config.premiumEnabled) {
            event.reply(lang.get("manager.no_premium")).setEphemeral(true).queue()
            return
        }

        if (event.member!!.voiceState?.inAudioChannel() == false) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.channel == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
            if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue"))
                    .setEphemeral(true).queue()
                return
            }
        } else {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        val manager = Player.getMusicManager(event.guild!!)
            ?: return event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing"))
                .setEphemeral(true).queue()
        val player = manager.player
        val scheduler = manager.scheduler

        if (DjRole.checkChannel(event.guild!!.selfMember.voiceState?.channel, config, scheduler)) {
            if (!DjRole.checkRole(event.member!!, config, scheduler)) {
                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setDescription(lang.get("manager.dj_role_required"))
                event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                return
            }
        }

        if (player.playingTrack == null) {
            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("manager.nothing_playing")).setEphemeral(true)
                .queue()
            return
        }

        when (event.subcommandName) {
            "add" -> {
                var filter = event.getOption("filter")!!.asString
                if (filter == "null")
                    filter = "clear"

                try {
                    when (filter) {
                        "bassboost" -> {
                            FilterManager.setBassBoost(player)
                            if (scheduler.filters.contains(AudioFilters.BASS_BOOST)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.BASS_BOOST)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Bass Boost") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "8d" -> {
                            FilterManager.set8D(player)
                            if (scheduler.filters.contains(AudioFilters.EIGHT_D)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.EIGHT_D)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "8D") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "nightcore" -> {
                            FilterManager.setNightCore(player)
                            if (scheduler.filters.contains(AudioFilters.NIGHT_CORE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.NIGHT_CORE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Night Core") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "vaporwave" -> {
                            FilterManager.setVaporWave(player)
                            if (scheduler.filters.contains(AudioFilters.VAPOR_WAVE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.VAPOR_WAVE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Vapor Wave") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "treble" -> {
                            FilterManager.setTreble(player)
                            if (scheduler.filters.contains(AudioFilters.TREBLE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.TREBLE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Treble") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "karaoke" -> {
                            FilterManager.setKaraoke(player)
                            if (scheduler.filters.contains(AudioFilters.KARAOKE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.KARAOKE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Karaoke") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "tremolo" -> {
                            FilterManager.setTremolo(player)
                            if (scheduler.filters.contains(AudioFilters.TREMOLO)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.TREMOLO)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Tremolo") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "speed" -> {
                            FilterManager.setSpeed(player)
                            if (scheduler.filters.contains(AudioFilters.SPEED)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.SPEED)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Speed") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "pitch" -> {
                            FilterManager.setPitch(player)
                            if (scheduler.filters.contains(AudioFilters.PITCH)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_enabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.add(AudioFilters.PITCH)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Pitch") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        else -> {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.unknown"))
                                .setEphemeral(true).queue()
                            return
                        }
                    }
                } catch (e: Exception) {
                    event.reply(formatEmoji(Constants.Emojis.error) + "An error occurred while trying to apply the filter: ${e.message}")
                        .setEphemeral(true).queue()
                    return
                }
            }

            "remove" -> {

                var filter = event.getOption("filter")!!.asString
                if (filter == "null")
                    filter = "clear"

                try {
                    when (filter) {
                        "bassboost" -> {
                            FilterManager.removeBassBoost(player)
                            if (!scheduler.filters.contains(AudioFilters.BASS_BOOST)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.BASS_BOOST)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Bass Boost") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "8d" -> {
                            FilterManager.remove8D(player)
                            if (!scheduler.filters.contains(AudioFilters.EIGHT_D)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.EIGHT_D)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) + lang.get("commands.filters.enabled")
                                    .replace("%s%", "8D") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "nightcore" -> {
                            FilterManager.removeNightCore(player)
                            if (!scheduler.filters.contains(AudioFilters.NIGHT_CORE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.NIGHT_CORE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Night Core") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "vaporwave" -> {
                            FilterManager.removeVaporWave(player)
                            if (!scheduler.filters.contains(AudioFilters.VAPOR_WAVE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.VAPOR_WAVE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Vapor Wave") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "treble" -> {
                            FilterManager.removeTreble(player)
                            if (!scheduler.filters.contains(AudioFilters.TREBLE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.TREBLE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Treble") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "karaoke" -> {
                            FilterManager.removeKaraoke(player)
                            if (!scheduler.filters.contains(AudioFilters.KARAOKE)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.KARAOKE)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Karaoke") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "tremolo" -> {
                            FilterManager.removeTremolo(player)
                            if (!scheduler.filters.contains(AudioFilters.TREMOLO)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.TREMOLO)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Tremolo") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "speed" -> {
                            FilterManager.removeSpeed(player)
                            if (!scheduler.filters.contains(AudioFilters.SPEED)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.SPEED)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Speed") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        "pitch" -> {
                            FilterManager.removePitch(player)
                            if (!scheduler.filters.contains(AudioFilters.PITCH)) {
                                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.already_disabled"))
                                    .queue()
                                return
                            }
                            manager.scheduler.filters.remove(AudioFilters.PITCH)
                            event.reply(
                                formatEmoji(Constants.Emojis.success) +
                                        lang.get("commands.filters.enabled").replace("%s%", "Pitch") +
                                        if (scheduler.filters.size > 1) "\n" + lang.get("commands.filters.enabled_filters")
                                            .replace(
                                                "%s%",
                                                scheduler.filters.joinToString { formatFilterNames(it) }) else ""
                            ).queue()
                        }

                        else -> {
                            event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.unknown"))
                                .setEphemeral(true).queue()
                            return
                        }
                    }
                } catch (e: Exception) {
                    event.reply(formatEmoji(Constants.Emojis.error) + "An error occurred while trying to apply the filter: ${e.message}")
                        .setEphemeral(true).queue()
                    return
                }
            }

            "clear" -> {
                player.filters.clear()
                player.filters.commit()
                manager.scheduler.filters.clear()
                event.reply(formatEmoji(Constants.Emojis.success) + lang.get("commands.filters.clear")).queue()
            }

            else -> {
                event.reply(formatEmoji(Constants.Emojis.error) + lang.get("commands.filters.unknown"))
                    .setEphemeral(true).queue()
                return
            }
        }

    }

    override val name: String
        get() = "filter"
    override val description: String
        get() = "Poner filtros de audio en la canción actual."
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "<Filtro>"
    override val premium: Boolean
        get() = true
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Poner filtros de audio en la canción actual.",
                    DiscordLocale.ENGLISH_US to "Set audio filters on the current song.",
                    DiscordLocale.ENGLISH_UK to "Set audio filters on the current song.",
                    DiscordLocale.FRENCH to "Définir des filtres audio sur la chanson actuelle.",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Defina filtros de áudio na música atual.",
                    DiscordLocale.ITALIAN to "Imposta filtri audio sulla canzone corrente.",
                    DiscordLocale.GERMAN to "Setzen Sie Audiofilter auf den aktuellen Song.",
                )
            )
            .addSubcommands(
                SubcommandData("clear", "Quitar todos los filtros"),
                SubcommandData("add", "Añadir un filtro")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Añadir un filtro",
                            DiscordLocale.ENGLISH_US to "Add a filter",
                            DiscordLocale.ENGLISH_UK to "Add a filter",
                            DiscordLocale.FRENCH to "Ajouter un filtre",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Adicionar um filtro",
                            DiscordLocale.ITALIAN to "Aggiungi un filtro",
                            DiscordLocale.GERMAN to "Fügen Sie einen Filter hinzu",
                        )
                    ).addOptions(options),
                SubcommandData("remove", "Quitar un filtro")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Quitar un filtro",
                            DiscordLocale.ENGLISH_US to "Remove a filter",
                            DiscordLocale.ENGLISH_UK to "Remove a filter",
                            DiscordLocale.FRENCH to "Supprimer un filtre",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Remover um filtro",
                            DiscordLocale.ITALIAN to "Rimuovi un filtro",
                            DiscordLocale.GERMAN to "Entfernen Sie einen Filter",
                        )
                    ).addOptions(options),
            )

    private val options = OptionData(OptionType.STRING, "filter", "Filtro a aplicar.")
        .setDescriptionLocalizations(
            mapOf(
                DiscordLocale.SPANISH to "Filtro a aplicar.",
                DiscordLocale.ENGLISH_US to "Filter to apply.",
                DiscordLocale.ENGLISH_UK to "Filter to apply.",
                DiscordLocale.FRENCH to "Filtre à appliquer.",
                DiscordLocale.PORTUGUESE_BRAZILIAN to "Filtro a ser aplicado.",
                DiscordLocale.ITALIAN to "Filtro da applicare.",
                DiscordLocale.GERMAN to "Zu setzender Filter.",
            )
        )
        .setRequired(true)
        .addChoice("Bass Boost", "bassboost")
        .addChoice("8D", "8d")
        .addChoice("Nightcore", "nightcore")
        .addChoice("Vaporwave", "vaporwave")
        .addChoice("Treble", "treble")
        .addChoice("Karaoke", "karaoke")
        .addChoice("Tremolo", "tremolo")
        .addChoice("Speed", "speed")
        .addChoice("Pitch", "pitch")

    private fun formatFilterNames(filter: AudioFilters): String {
        return when (filter) {
            AudioFilters.BASS_BOOST -> "Bass Boost"
            AudioFilters.EIGHT_D -> "8D"
            AudioFilters.NIGHT_CORE -> "Nightcore"
            AudioFilters.VAPOR_WAVE -> "Vaporwave"
            AudioFilters.TREBLE -> "Treble"
            AudioFilters.KARAOKE -> "Karaoke"
            AudioFilters.TREMOLO -> "Tremolo"
            AudioFilters.SPEED -> "Speed"
            AudioFilters.PITCH -> "Pitch"
        }
    }
}
