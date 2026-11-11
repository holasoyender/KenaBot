/*
 * File:          TogetherListener.kt
 * Path:          v3\src\main\kotlin\enums\TogetherListener.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: ae7c8ab723168815338983bde911f7276c4d97e5c106f1f943fd54cdb0288d82
 *
 */


package enums

class TogetherListener(
    guildId: String,
    botName: String,
    channelId: String
) {

    val guildId: String
    val botName: String
    var channelId: String

    init {
        this.guildId = guildId
        this.botName = botName
        this.channelId = channelId
    }

}