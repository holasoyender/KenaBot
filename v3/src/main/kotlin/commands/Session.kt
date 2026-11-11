/*
 * File:          Session.kt
 * Path:          v3\src\main\kotlin\commands\Session.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 21:08:05 +02:00
 *
 * SHA-256: 8153710d74d00081723f2db952070d5e04dda125d8137a0d48afc4a4f15737e7
 *
 */


package commands

import database.schema.Guild
import database.schema.Session
import interfaces.Command
import langs.LangUtils
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.*
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.utils.TimeFormat
import player.Player
import utils.Constants

class Session: Command {
    override fun run(event: SlashCommandInteractionEvent, config: Guild, lang: LangUtils) {

        val option = event.subcommandName
        if(option == null) {
            event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
            return
        }

        if (!event.isFromGuild || event.member == null) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_do")).setEphemeral(true).queue()
            return
        }

        val channel = event.member!!.voiceState?.channel

        if (channel == null) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc")).setEphemeral(true)
                .queue()
            return
        }

        if (!channel.guild.selfMember.hasPermission(
                channel,
                Permission.VOICE_CONNECT
            ) || !channel.guild.selfMember.hasPermission(channel, Permission.VOICE_SPEAK)
        ) {
            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.play.no_vc_perms")).setEphemeral(true)
                .queue()
            return
        }

        if (config.disabledVoiceChannels.contains(channel.id)) {
            if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.play.vc_disabled"))
                    .setEphemeral(true).queue()
                return
            }
        }

        if (event.guild!!.selfMember.voiceState?.channel !== null) {
            if (event.guild!!.selfMember.voiceState!!.channel !== channel) {
                event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("commands.play.different_vc"))
                    .setEphemeral(true).queue()
                return
            }
        }

        when (option) {
            "create" -> {

                val existingSession = Session.get(event.guild!!.id, event.jda.selfUser.name, false)
                if(existingSession != null) {
                    event.reply(lang.get("commands.session.already_active").replace("%s%", existingSession.inviteCode)).setEphemeral(true).queue()
                    return
                }

                val existingListeningSession = Session.get(event.guild!!.id, event.jda.selfUser.name, true)
                if(existingListeningSession != null) {
                    event.reply(lang.get("commands.session.cant_create_already_listener")).setEphemeral(true).queue()
                    return
                }

                val manager = Player.getMusicManager(event.guild!!, event.channel as GuildMessageChannel)
                if(manager?.player?.playingTrack == null) {
                    event.reply(lang.get("commands.session.cant_create_nothing_playing")).setEphemeral(true).queue()
                    return
                }

                val session = Session(
                    event.guild!!.id,
                    event.jda.selfUser.name,
                    Session.generateCode(),
                    System.currentTimeMillis(),
                    listOf()
                )
                session.save()

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setAuthor(lang.get("commands.session.embed_author"), null, event.jda.selfUser.avatarUrl)
                    .setTitle("${lang.get("commands.session.embed_title")} ${Constants.Emojis.beta1}${Constants.Emojis.beta2}")
                    .setDescription(lang.get("commands.session.embed_description").replace("%s%", session.inviteCode))

                event.replyEmbeds(embed.build()).setEphemeral(false).queue()

            }
            "join" -> {

                val code = event.getOption("código")?.asString
                if(code == null) {
                    event.reply(lang.get("commands.session.unknown_code")).setEphemeral(true).queue()
                    return
                }

                var session = Session.get(code)
                if(session == null) {
                    event.reply(lang.get("commands.session.unknown_code")).setEphemeral(true).queue()
                    return
                }

                if(session.sourceGuildId == event.guild!!.id && session.sourceBotName == event.jda.selfUser.name) {
                    event.reply(lang.get("commands.session.join_self")).setEphemeral(true).queue()
                    return
                }

                val currentSession = Session.get(event.guild!!.id, event.jda.selfUser.name, true)
                if(currentSession != null) {
                    event.reply(lang.get("commands.session.already_in_session")).setEphemeral(true).queue()
                    return
                }

                val existingSession = Session.get(event.guild!!.id, event.jda.selfUser.name, false)
                if(existingSession != null) {
                    event.reply(lang.get("commands.session.already_active").replace("%s%", existingSession.inviteCode)).setEphemeral(true).queue()
                    return
                }

                session = session.join(event.guild!!.id, event.jda.selfUser.name, event.channel.id)

                val baseManager = Player.getMusicManager(session.sourceGuildId) ?: return event.reply(lang.get("commands.session.cant_join_nothing_playing")).setEphemeral(true).queue()

                if(baseManager.player.playingTrack == null) {
                    event.reply(lang.get("commands.session.cant_join_nothing_playing")).setEphemeral(true).queue()
                    return
                }

                if(session.sourceBotName != event.jda.selfUser.name) {
                    event.reply(lang.get("commands.session.other_bot")).setEphemeral(true).queue()
                    return
                }

                val manager = Player.getMusicManager(event.guild!!, event.channel as GuildMessageChannel, true)!!

                if (manager.player.playingTrack != null) {
                    if (event.member!!.voiceState?.inAudioChannel() == false) {
                        event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                        return
                    }

                    if (event.guild!!.selfMember.voiceState?.inAudioChannel() == true) {
                        if (event.guild!!.selfMember.voiceState!!.channel != event.member!!.voiceState!!.channel) {
                            event.reply(Constants.formatEmoji(Constants.Emojis.error) + lang.get("manager.cant_modify_queue")).setEphemeral(true).queue()
                            return
                        }
                    }

                    manager.player.stopTrack()
                    manager.scheduler.queue.clear()
                }

                if(!manager.link.player.isConnected)
                    manager.link.connect(channel)

                manager.scheduler.isRepeating = baseManager.scheduler.isRepeating
                manager.scheduler.playlistSizeCache = if(baseManager.scheduler.playlistSizeCache == 0) baseManager.scheduler.queue.size else baseManager.scheduler.playlistSizeCache

                manager.channel = event.channel as GuildMessageChannel
                manager.scheduler.lastChannel = event.channel as GuildMessageChannel

                val queue = baseManager.scheduler.queue.map { it.info.uri }
                Player.loadAndPlayBackup(manager, baseManager.player.playingTrack.info.uri, baseManager.player.trackPosition + 28, queue, event.guild!!.selfMember, event.channel as GuildMessageChannel)

                event.reply(Constants.formatEmoji(Constants.Emojis.success) + " " + lang.get("commands.session.join").replace("%s%", session.inviteCode)).setEphemeral(false).queue()
            }

            "leave" -> {

                val existingSession = Session.get(event.guild!!.id, event.jda.selfUser.name, false)
                if(existingSession != null) {
                    event.reply(lang.get("commands.session.cant_leave_self")).setEphemeral(true).queue()
                    return
                }

                val currentSession = Session.get(event.guild!!.id, event.jda.selfUser.name, true)
                if(currentSession == null) {
                    event.reply(lang.get("commands.session.cant_leave_not_in_session")).setEphemeral(true).queue()
                    return
                }

                val session = currentSession.leave(event.guild!!.id, event.jda.selfUser.name)
                val manager = Player.getMusicManager(event.guild!!, event.channel as GuildMessageChannel) ?: return event.reply(lang.get("commands.session.cant_leave_not_in_session")).setEphemeral(true).queue()

                manager.player.stopTrack()
                manager.player.isPaused = false

                manager.scheduler.queue.clear()

                manager.link.destroy()
                Player.destroyMusicManager(event.guild!!)

                event.reply(Constants.formatEmoji(Constants.Emojis.success) + " " + lang.get("commands.session.leave").replace("%s%", session.inviteCode)).setEphemeral(false).queue()

            }

            "delete" -> {

                val existingSession = Session.get(event.guild!!.id, event.jda.selfUser.name, false)
                if (existingSession == null) {
                    event.reply(lang.get("commands.session.not_hosting")).setEphemeral(true).queue()
                    return
                }

                existingSession.listeners.forEach { listener ->
                    val guild = event.jda.getGuildById(listener.guildId)

                    if(guild != null) {
                        val textChannel = guild.getTextChannelById(listener.channelId)
                        textChannel?.sendMessage(Constants.formatEmoji(Constants.Emojis.gear) + lang.get("commands.session.session_end").replace("%s%", existingSession.inviteCode))
                            ?.queue({}, {})

                        val guildManager = Player.getMusicManager(guild.id)
                        guildManager?.player?.stopTrack()
                        guildManager?.player?.isPaused = false

                        guildManager?.scheduler?.queue?.clear()

                        guildManager?.link?.destroy()

                        Player.destroyMusicManager(guild)
                    }
                }

                event.reply(Constants.formatEmoji(Constants.Emojis.success) + lang.get("commands.session.delete").replace("%s%", existingSession.inviteCode).replace("%t%", existingSession.listeners.size.toString()))
                    .setEphemeral(false).queue()
            }

            "info" -> {

                val existingSession = Session.get(event.guild!!.id, event.jda.selfUser.name, false)
                if(existingSession == null) {
                    event.reply(lang.get("commands.session.not_hosting")).setEphemeral(true).queue()
                    return
                }

                val embed = EmbedBuilder()
                    .setColor(config.embedColor)
                    .setAuthor(lang.get("commands.session.embed_author"), null, event.jda.selfUser.avatarUrl)
                    .setDescription(lang.get("commands.session.code").replace("%s%", existingSession.inviteCode))
                    .addField(lang.get("commands.session.created_at"), "${TimeFormat.DEFAULT.format(existingSession.createdAt)} (${TimeFormat.RELATIVE.format(existingSession.createdAt)})", true)
                    .addField(lang.get("commands.session.listeners"), "${existingSession.listeners.size} oyentes", true)
                    .setThumbnail("https://cdn.discordapp.com/attachments/912371493944586320/1062464402500104242/kena_music.png")

                event.replyEmbeds(embed.build()).setEphemeral(false).queue()
            }

            else -> {
                event.reply(lang.get("events.interactionCreate.unknown")).setEphemeral(true).queue()
                return
            }
        }
    }

    override val name: String
        get() = "session"
    override val description: String
        get() = "Crear o unirse a una sesión de Kena Together"
    override val category: String
        get() = "Music"
    override val enabled: Boolean
        get() = true
    override val guildOnly: Boolean
        get() = true
    override val permissions: List<Permission>
        get() = listOf()
    override val usage: String
        get() = "join/create [código]"
    override val metadata: SlashCommandData
        get() = Commands.slash(this.name, this.description)
            .setDescriptionLocalizations(
                mapOf(
                    DiscordLocale.SPANISH to "Crear o unirse a una sesión de Kena Together",
                    DiscordLocale.ENGLISH_US to "Create or join a Kena Together session",
                    DiscordLocale.ENGLISH_UK to "Create or join a Kena Together session",
                    DiscordLocale.FRENCH to "Créer ou rejoindre une session Kena Together",
                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Criar ou se juntar a uma sessão Kena Together",
                    DiscordLocale.ITALIAN to "Crea o unisciti a una sessione Kena Together",
                    DiscordLocale.GERMAN to "Erstellen oder beitreten Sie einer Kena Together-Sitzung"
                )
            )
            .addSubcommands(
                SubcommandData("create", "Crear una sesión")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Crear una sesión",
                            DiscordLocale.ENGLISH_US to "Create a session",
                            DiscordLocale.ENGLISH_UK to "Create a session",
                            DiscordLocale.FRENCH to "Créer une session",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Criar uma sessão",
                            DiscordLocale.ITALIAN to "Crea una sessione",
                            DiscordLocale.GERMAN to "Erstellen Sie eine Sitzung"
                        )
                    ),
                SubcommandData("join", "Unirse a una sesión")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Unirse a una sesión",
                            DiscordLocale.ENGLISH_US to "Join a session",
                            DiscordLocale.ENGLISH_UK to "Join a session",
                            DiscordLocale.FRENCH to "Rejoindre une session",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Junte-se a uma sessão",
                            DiscordLocale.ITALIAN to "Unisciti a una sessione",
                            DiscordLocale.GERMAN to "Treten Sie einer Sitzung bei"
                        )
                    ).addOptions(
                        OptionData(OptionType.STRING, "código", "Código de la sesión", true)
                            .setDescriptionLocalizations(
                                mapOf(
                                    DiscordLocale.SPANISH to "Código de la sesión",
                                    DiscordLocale.ENGLISH_US to "Session code",
                                    DiscordLocale.ENGLISH_UK to "Session code",
                                    DiscordLocale.FRENCH to "Code de session",
                                    DiscordLocale.PORTUGUESE_BRAZILIAN to "Código da sessão",
                                    DiscordLocale.ITALIAN to "Codice di sessione",
                                    DiscordLocale.GERMAN to "Sitzungscode"
                                )
                            )
                    ),
                SubcommandData("info", "Obtener información sobre la sesión")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Obtener información sobre la sesión",
                            DiscordLocale.ENGLISH_US to "Get information about the session",
                            DiscordLocale.ENGLISH_UK to "Get information about the session",
                            DiscordLocale.FRENCH to "Obtenir des informations sur la session",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Obter informações sobre a sessão",
                            DiscordLocale.ITALIAN to "Ottieni informazioni sulla sessione",
                            DiscordLocale.GERMAN to "Informationen zur Sitzung erhalten"
                        )
                    ),
                SubcommandData("leave", "Dejar la sesión")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Dejar la sesión",
                            DiscordLocale.ENGLISH_US to "Leave the session",
                            DiscordLocale.ENGLISH_UK to "Leave the session",
                            DiscordLocale.FRENCH to "Quitter la session",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Deixe a sessão",
                            DiscordLocale.ITALIAN to "Lascia la sessione",
                            DiscordLocale.GERMAN to "Verlassen Sie die Sitzung"
                        )
                    ),
                SubcommandData("delete", "Eliminar la sesión")
                    .setDescriptionLocalizations(
                        mapOf(
                            DiscordLocale.SPANISH to "Eliminar la sesión",
                            DiscordLocale.ENGLISH_US to "Delete the session",
                            DiscordLocale.ENGLISH_UK to "Delete the session",
                            DiscordLocale.FRENCH to "Supprimer la session",
                            DiscordLocale.PORTUGUESE_BRAZILIAN to "Excluir a sessão",
                            DiscordLocale.ITALIAN to "Elimina la sessione",
                            DiscordLocale.GERMAN to "Sitzung löschen"
                        )
                    )
            )
    override val premium: Boolean
        get() = false
}