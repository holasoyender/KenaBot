/*
 * File:          Constants.kt
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\Constants.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-11-14 19:25:55 +01:00
 *
 * SHA-256: f0c2b83c8d04916362c085cca6701f63bd4c708a42f4d06760b357db00309ee0
 *
 */


@file:Suppress("ClassName", "unused", "ConstPropertyName")

package xyz.kenabot.config

import java.awt.Color

object Constants {

    object emojis {
        const val error = ":no_entry_sign:"
        const val pause = "<:pause:952241618755416124>"
        const val resume = "<:play:952241618843475988>"
        const val volume = "<:volume:952242136584175748>"
        const val loop = "<:loop:952242523521294456>"
        const val add = "<:add:952249506680959026>"
        const val success = "<:tick:952250501779570738>"
        const val gear = "<:gear:952252164280029274>"
        const val shuffle = "<:shuffle:952253613118459904>"
        const val beta1 = "<:beta1:1066429254834855989>"
        const val beta2 = "<:beta2:1066429257091395774>"
        const val loading = "<a:kload:1037765493668466838>"
        const val online = "<:online:983837897536987146>"
        const val warning = "<:idle:983837896287092796>"
        const val offline = "<:dnd:983837895083323452>"
        const val dnd = "<:dnd:983837895083323452>"
        const val explicit = "<:explicit:1073266738759729184>"
        const val dash = "💻"
        const val search = "🔍"
        const val popularity0 = "<:down:1080921753905266728>"
        const val popularity1 = "<:midpopular:1080921461637796060>"
        const val popularity2 = "<:up:1080921755960487958>"
        const val popularity3 = "<:popular:1080921463751717007>"
        const val popularity4 = "<:verypopular:1080921464619942051>"
    }

    val SpotifyRegex =  Regex("^https?://(open.spotify.com)/(.*)\$")
    val SoundCloudRegex = Regex("^https?://(soundcloud.com|snd.sc)/(.*)\$")
    val TwitchRegex = Regex("^(https?://)?((www.)?twitch.tv)/(.*)\$")
    val DeezerRegex = Regex("^(https?://)?((www.)?deezer.com)/(.*)\$")
    val AppleMusicRegex = Regex("^(https?://)?music.apple.com/(?:.+)?(artist|album|music-video|playlist)/([\\w\\-.]+(/)+[\\w\\-.]+|[^&]+)/([\\w\\-.]+(/)+[\\w\\-.]+|[^&]+)")
    val TikTokRegex = Regex("^(https?://)?((www.)?tiktok.com)/(.*)\$")
    val RedditRegex = Regex("^(https?://)?((www.)?reddit.com)/(.*)\$")
    val YouTubeRegex = Regex("^((?:https?:)?//)?((?:www|m)\\.)?(youtube\\.com|youtu.be)(/(?:[\\w-]+\\?v=|embed/|v/)?)([\\w-]+)(\\S+)?\$")

    object images {
        const val soundCloudIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012769193717923902/soundcloud.png"
        const val twitchIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012766939824791643/twitch.png"
        const val deezerIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012767607918694510/deezer.png"
        const val appleMusicIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012768596163821579/apple.png"
        const val tikTokIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012766231033552926/tiktok.png"
        const val redditIcon = "https://cdn.discordapp.com/attachments/755000173922615336/1012770087540903946/reddit.png"
        const val noImage = "https://cdn.discordapp.com/attachments/755000173922615336/955506132372291664/no_image.png"
    }

    object colors {
        const val purple = 0x6600F4
        const val red = 0xED4245
    }
}