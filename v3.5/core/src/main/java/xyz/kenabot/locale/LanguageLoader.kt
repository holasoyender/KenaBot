/*
 * File:          LanguageLoader.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\locale\LanguageLoader.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-09 22:32:12 +02:00
 *
 * SHA-256: 88bda905fa6eb155d202dca03143dfa2d13d579784fdfb0b33bfc46597220b9f
 *
 */


package xyz.kenabot.locale

import dev.minn.jda.ktx.util.SLF4J
import kotlinx.serialization.json.Json
import xyz.kenabot.io.ResourceManager

object LanguageLoader {

    /**
     * This list contains all the languages that are available for the bot.
     * The bot will only load the languages that are in this list.
     */
    val languages: MutableMap<String, Language> = mutableMapOf()

    /**
     * This list contains all the languages that are enabled for the bot.
     */
    private val enabledLanguages = listOf("es-ES")

    /**
     * This is the default language that will be used if the user's language is not available.
     */
    const val defaultLanguage = "es-ES"

    private val logger by SLF4J


    fun load(): Pair<Int, Int> {

        val ok = enabledLanguages.map { id ->
            try {
                val resource = ResourceManager("langs/$id.json")
                val rawLanguage = resource.content

                val language = Json.decodeFromString<Language>(rawLanguage)
                languages[id] = language

                logger.debug("Loaded language $id")
                true
            } catch (e: Exception) {
                logger.error("Error loading language $id", e)
                false
            }
        }

        return Pair(enabledLanguages.size, ok.count { it })
    }

    fun getLang(name: String): Language {
        val exists = languages.containsKey(name)
        return if (exists) {
            languages[name]!!
        } else {
            logger.error("Lang does not exist: $name", true)
            languages[defaultLanguage]!!
        }
    }

    @Suppress("unused")
    fun normalizeWord(word: String): String {
        return word
            .lowercase()
            .replace("ñ", "")
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
    }
}