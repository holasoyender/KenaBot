/*
 * File:          LangManager.kt
 * Path:          v3\src\main\kotlin\langs\LangManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-29 21:43:39 +02:00
 *
 * SHA-256: cc2f6d15b05c848b2cd3e47d85c8465909043a45eeefb43a21dc8c0801feccfb
 *
 */


package langs

import io.sentry.Sentry
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader


object LangManager {

    private val langs: MutableMap<String, JSONObject> = mutableMapOf()
    private val logger = LoggerFactory.getLogger(LangManager::class.java)
    val langNames = listOf("es-ES", "en-US", "en-GB", "fr-FR", "pt-PT", "pt-BR", "it-IT", "de-DE", "uk-UA", "ca-ES")

    fun load() {


        langNames.forEach { lang ->
            run {
                try {
                    val file = getFileAsIOStream("langs/$lang.json")

                    val obj = JSONObject(fileContent(file))
                    addLang(lang, obj)

                    if(lang == "es-ES") {

                        val newObj = JSONObject()
                        newObj.put("flag", "\uD83D\uDC36")
                        newObj.put("name", "Guau guau")
                        newObj.put("description", "Guau guau guau from KoiHouse")

                        newObj.put("events", obj.getJSONObject("events"))
                        newObj.put("commands", obj.getJSONObject("commands"))
                        newObj.put("manager", obj.getJSONObject("manager"))
                        addLang("gu-AU", newObj)
                    }

                } catch (e: IOException) {
                    logger.error("Error loading lang file $lang")
                    Sentry.captureException(e)
                }
            }
        }
    }

    private fun addLang(name: String, lang: JSONObject) {
        val exists = langs.containsKey(name)
        if (exists) {
            logger.error("Lang already exists: $name", true)
        } else {
            langs[name] = lang
        }
    }

    fun getLang(name: String): JSONObject {
        val exists = langs.containsKey(name)
        return if (exists) {
            langs[name]!!
        } else {
            logger.error("Lang does not exist: $name", true)
            langs["es-ES"]!!
        }
    }

    fun allLangs(): Map<String, JSONObject> {
        return langs
    }

    private fun getFileAsIOStream(fileName: String): InputStream {
        return javaClass
            .classLoader
            .getResourceAsStream(fileName) ?: throw IllegalArgumentException("$fileName is not found")
    }

    @Throws(IOException::class)
    private fun fileContent(`is`: InputStream): String {
        var lines = ""
        InputStreamReader(`is`).use { isr ->
            BufferedReader(isr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    lines += line
                }
                `is`.close()
            }
        }
        return lines
    }
}