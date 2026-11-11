/*
 * File:          Clazz.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\io\Clazz.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 578840efae423021f47bbbe223007c82db694b8b09a56fd2a1f0352f12426bb5
 *
 */


package xyz.kenabot.io

import dev.minn.jda.ktx.util.SLF4J
import io.github.classgraph.ClassGraph
import net.dv8tion.jda.api.events.Event
import xyz.kenabot.interfaces.Command
import xyz.kenabot.interfaces.DevCommand

object Clazz {

    private const val COMMANDS_PACKAGE = "xyz.kenabot.commands"
    private val logger by SLF4J

    inline fun <reified T> getPropertyOfEvent(event: Event, name: String): T? {
        return try {
            val field = event::class.java.getDeclaredField(name)
            field.isAccessible = true
            field.get(event) as T
        } catch (e: Exception) {
            null
        }
    }

    fun getSlashCommandsOfClass(clazz: Class<out Command>): List<Command> {
        val list = mutableListOf<Command>()

        ClassGraph().acceptPackages(COMMANDS_PACKAGE).scan().use { sr ->
            sr.getClassesImplementing(clazz.name).loadClasses().forEach { cmd ->
                runCatching {
                    list.add(cmd.getDeclaredConstructor().newInstance() as Command)
                }.onFailure { logger.error("An error occurred while registering a command!", it) }
            }
        }

        if (list.isEmpty())
            logger.warn("No slash commands were found in package $COMMANDS_PACKAGE")

        return list
    }

    fun getDevCommandsOfClass(clazz: Class<out DevCommand>): List<DevCommand> {
        val list = mutableListOf<DevCommand>()

        ClassGraph().acceptPackages(COMMANDS_PACKAGE).scan().use { sr ->
            sr.getClassesImplementing(clazz.name).loadClasses().forEach { cmd ->
                runCatching {
                    list.add(cmd.getDeclaredConstructor().newInstance() as DevCommand)
                }.onFailure { logger.error("An error occurred while registering a dev command!", it) }
            }
        }

        if (list.isEmpty())
            logger.warn("No dev commands were found in package $COMMANDS_PACKAGE")

        return list
    }
}