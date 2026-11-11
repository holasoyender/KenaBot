/*
 * File:          default.ts
 * Path:          v2\utils\config\default.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-02 18:35:10 +02:00
 *
 * SHA-256: 0625952d58bbf8e5bf52c208dd0150bd583850d115af14d365b68f7bef58d599
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
        enabled: false,
        expires: "" || 0,
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