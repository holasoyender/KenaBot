/*
 * File:          Restart.kt
 * Path:          v3\src\main\kotlin\commands\dev\Restart.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 16:20:00 +02:00
 *
 * SHA-256: 8952521366e379409bda0d906036e8c2c65667d44d0205b8697156caa73bdfd9
 *
 */


package commands.dev

import interfaces.DevCommand
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button

import java.awt.Color

class Restart: DevCommand {
    override fun run(event: MessageReceivedEvent, args: List<String>) {

        val embed = EmbedBuilder()
            .setColor(Color.decode("#5865F2"))
            .setDescription("Haciendo click al botón el bot mandará un mensaje a todos los servidores que están reproduciendo una canción diciendo que se reiniciará el bot")
            .setAuthor("Reinicio del bot", null, event.jda.selfUser.avatarUrl)

        event.message.replyEmbeds(embed.build()).setActionRow(
            Button.success("cmd:restart", "Reiniciar"),
            Button.danger("cmd:restart-cancel", "Cancelar")
        ).queue()
    }

    override val name: String
        get() = "restart"
    override val description: String
        get() = "Avisar sobre un reinicio del bot"
}