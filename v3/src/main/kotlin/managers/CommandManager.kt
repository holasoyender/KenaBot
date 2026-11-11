/*
 * File:          CommandManager.kt
 * Path:          v3\src\main\kotlin\managers\CommandManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: ea07ee9623e3adc725009fe2a9dd5b74bb7a5b9ce549ac4aa4d2c77283770412
 *
 */


package managers

import database.schema.Guild
import interfaces.Command
import io.sentry.Sentry
import langs.LangUtils
import metrics.DailyStats
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import thirdparty.errors.Error
import utils.Constants
import utils.Constants.OWNER_IDS
import utils.Constants.formatEmoji
import java.util.ArrayList
import javax.print.attribute.standard.Severity

class CommandManager {

    val commands: MutableList<Command> = ArrayList<Command>()
    companion object { var instance: CommandManager? = null }

    init {
        CommandInitializer(this)

        if (instance != null)
            throw Error("Ya existe una instancia de CommandManager!")
        else
            instance = this

    }

    fun registerCommand(cmd: Command) {
        val nameFound = commands.stream().anyMatch {
            it.name.lowercase() == cmd.name
        }
        require(!nameFound) { "Ya existe un comando con ese nombre!" }
        commands.add(cmd)
    }

    private fun getCommand(search: String): Command? {
        val searchLower = search.lowercase()
        for (command in commands) {
            if (command.name == searchLower) {
                return command
            }
        }
        return null
    }

    fun run(event: SlashCommandInteractionEvent) {

        val guild = if(event.isFromGuild) Guild.get(event.guild!!.id) else Guild.defaultConfig()
        val invoke: String = event.name
        val cmd: Command? = getCommand(invoke)

        if(guild.blockedEnabled) {
            if(!OWNER_IDS.contains(event.user.id)) {
                event.reply(formatEmoji(Constants.Emojis.error) + LangUtils(guild.language).get("manager.blocked_server").replace("%s%", guild.blockedReason)).setEphemeral(true).queue()
                return
            }
        }

        if (cmd != null) {

            if(!cmd.enabled) {
                event.reply(formatEmoji(Constants.Emojis.error) + "The command has been globally disabled due to an internal error").setEphemeral(true).queue()
                return
            }

            if(cmd.guildOnly && event.guild == null) {
                event.reply("${Constants.Emojis.error}  No puedo ejecutar comandos en mensajes privados!").setEphemeral(true).queue()
                return
            }

            if (guild.disabledCommands.contains(cmd.name)) {
                if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                    if (!OWNER_IDS.contains(event.user.id)) {
                        val embed = EmbedBuilder()
                            .setColor(guild.embedColor)
                            .setDescription(formatEmoji(Constants.Emojis.error) + LangUtils(guild.language).get("manager.blocked_command"))
                        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                        return
                    }
                }
            }

            if (guild.disabledChannels.contains(event.channel.id)) {
                if (!event.member!!.hasPermission(Permission.MANAGE_SERVER)) {
                    if (!OWNER_IDS.contains(event.user.id)) {
                        val embed = EmbedBuilder()
                            .setColor(guild.embedColor)
                            .setDescription(formatEmoji(Constants.Emojis.error) + LangUtils(guild.language).get("manager.blocked"))
                        event.replyEmbeds(embed.build()).setEphemeral(true).queue()
                        return
                    }
                }
            }

            if (cmd.permissions.isNotEmpty() && !OWNER_IDS.contains(event.user.id)) {
                if (event.isFromGuild) {
                    val member = event.member!!
                    val missingPermissions = cmd.permissions.filter { !member.hasPermission(it) }
                    if (missingPermissions.isNotEmpty()) {
                        event.reply(LangUtils(guild.language).get("manager.no_perms")).setEphemeral(true).queue()
                        return
                    }
                } else {
                    event.reply("${formatEmoji(Constants.Emojis.error)}  No puedes usar comandos en mensajes privados!").setEphemeral(true).queue()
                    return
                }
            }

            try {
                cmd.run(event, guild, LangUtils(guild.language))
            } catch (e: Exception) {
                val error = Error(
                    "Ha ocurrido un error al ejecutar el comando `$invoke`",
                    Severity.ERROR,
                    e
                )
                error.push()
                event.reply("${Constants.Emojis.error}  Ha ocurrido un error al ejecutar el comando\nSi el problema persiste, únete a nuestro servidor de discord con el comando `/soporte` y reportanos el error\nEl código que se le ha asignado a tu error es: `${error.code}`")
                    .setEphemeral(true).queue()

                Sentry.captureException(e)
                e.printStackTrace()
            }

            database.schema.Commands.get(cmd.name).increment()
            DailyStats.incrementCommands(event.jda)
            if (System.currentTimeMillis() % 86400000 == 0L) {
                DailyStats.clear()
            }

        } else {
            event.reply(":no_entry_sign:  No he podido encontrar el comando `$invoke`").setEphemeral(true).queue()
            return
        }
    }
}