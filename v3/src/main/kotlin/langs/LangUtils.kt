/*
 * File:          LangUtils.kt
 * Path:          v3\src\main\kotlin\langs\LangUtils.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-30 23:11:20 +02:00
 *
 * SHA-256: c92e1b18c38a720e54e00962ed905f4b6c56322726d15e85a07e2fadbb8f1b09
 *
 */


package langs

import langs.LangManager.getLang
import org.json.JSONObject

class LangUtils(langName: String) {

    private var lang: JSONObject? = null

    init {
        lang = getLang(langName)
    }

    fun get(key: String, shortError: Boolean = false): String {
        var search = lang!!
        val objects = key.split(".")

        return try {
        for (i in 0 until objects.size - 1) {
            search = search.getJSONObject(objects[i])
        }
            val string = search.getString(objects[objects.size - 1])

            if(lang!!.get("name") == "Guau guau" && (key != "name" && key != "flag" && key != "description")) {
                val words = string.split(" ")
                val newWords = mutableListOf<String>()
                words.map{ it.lowercase() }.forEach { word ->
                    run {
                        if (
                            word.contains("kenabot") ||
                            word.contains("%s%") ||
                            word.contains("%t%") ||
                            word.contains("%d%") ||
                            word.contains("%f%") ||
                            word.startsWith("[") ||
                            word.startsWith(":")
                        ) {
                            newWords.add(normalizeWord(word))
                        } else {
                            val possibleWords = listOf("guau", "guau", "guauu", "woof", "ruff", "wouaff", "wouf")
                            val newWord = Regex("(\\w+)").replace(normalizeWord(word), possibleWords.random())
                            newWords.add(newWord)
                        }
                    }
                }
                newWords.joinToString(" ")
            } else {
                string ?: if (shortError) "Error" else "Error getting string, please report this to the support server.\nLang: ${
                        lang?.get(
                            "name"
                        ) ?: "Unknown"
                    }\nKey: `$key`"
            }
        } catch (e: Exception) {

            if (lang!!.getString("name") != "Español") {
                return LangUtils("es-ES").get(key, shortError)
            }

            if(shortError) "Error" else "Error getting string, please report this to the support server.\nLang: ${lang?.get("name") ?: "Unknown"}\nKey: `$key`"
        }
    }

    private fun normalizeWord(word: String): String {
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