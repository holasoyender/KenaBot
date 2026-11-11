/*
 * File:          DiscordTogether.kt
 * Path:          v3\src\main\kotlin\functions\DiscordTogether.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 7ebd12590b9d114abd80a41a70e983c2cc102d4b6f4603968229bf5a92613e34
 *
 */


package functions

object DiscordTogether {

    fun getAppID(activity: String): String {
        return when (activity) {
            "youtube" -> "880218394199220334"
            "poker" -> "755827207812677713"
            "betrayal" -> "773336526917861400"
            "fishing" -> "814288819477020702"
            "chess" -> "832012774040141894"
            "sketchy" -> "879864070101172255"
            "lettertile" -> "879863686565621790"
            "wordsnack" -> "879863976006127627"
            "doodlecrew" -> "878067389634314250"
            "awkword" -> "879863881349087252"
            "spellcast" -> "852509694341283871"
            "checkers" -> "832013003968348200"
            "puttparty" -> "945737671223947305"
            "sketchheads" -> "902271654783242291"
            "ocho" -> "832025144389533716"
            "land" -> "903769130790969345"
            "bobble" -> "947957217959759964"
            "askaway" -> "976052223358406656"
            "meme" -> "950505761862189096"
            else -> "880218394199220334"
        }
    }

    fun getName(activity: String): String {
        return when(activity) {
            "youtube" -> "YouTube Watch Together"
            "poker" -> "Poker Night"
            "betrayal" -> "Betrayal.io"
            "fishing" -> "Fishing.io"
            "chess" -> "Chess in the Park"
            "sketchy" -> "Sketchy Artist"
            "lettertile" -> "LetterTile"
            "wordsnack" -> "WordSnack"
            "doodlecrew" -> "DoodleCrew"
            "awkword" -> "AwkWord"
            "spellcast" -> "SpellCast"
            "checkers" -> "Checkers"
            "puttparty" -> "PuttParty"
            "sketchheads" -> "SketchHeads"
            "ocho" -> "Blazing 8s"
            "land" -> "Land-io"
            "bobble" -> "Bobble League"
            "askaway" -> "Ask Away"
            "meme" -> "Know What I Meme"
            else -> "YouTube Watch Together"
        }
    }

    fun getAllApps(): List<String> {
        return listOf(
            "youtube",
            "poker",
            "betrayal",
            "fishing",
            "chess",
            "sketchy",
            "lettertile",
            "wordsnack",
            "doodlecrew",
            "awkword",
            "spellcast",
            "checkers",
            "puttparty",
            "sketchheads",
            "ocho",
            "land",
            "bobble",
            "askaway",
            "meme"
        )
    }
}