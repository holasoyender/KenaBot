/*
 * File:          premium.ts
 * Path:          v2\utils\config\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-02 18:35:10 +02:00
 *
 * SHA-256: 571d28bd5cdeef098fe173fc6314f559e2455278ec53adffaa73f0f51aefab27
 *
 */


export default {
    guildID: "",
    lang: "es-ES",
    color: "#2f3136",
    _247: {
        channelID: null,
        enabled: false,
    },
    premium: {
        enabled: true,
        expires: "Forever",
    },
    blocked: {
        enabled: false,
        reason: ""
    },
    disabled: {
        channels: [],
        commands: [],
        voice: []
    },
    beta: false,
    dj_role: ""
}