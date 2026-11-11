/*
 * File:          Guild.ts
 * Path:          v2\database\schema\Guild.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-02 18:35:10 +02:00
 *
 * SHA-256: d67aaab82f60851ae96b3db62fb87bcc47554a1f2e5da422245e6765d3776327
 *
 */


import mongoose, {Schema} from "mongoose";
import Default from "../../utils/config/default";


const guildSchema = new mongoose.Schema({
    guildID: { type: String, required: true, unique: true },
    lang: { type: String, required: false, default: Default.lang },
    premium: {
        enabled: { type: Boolean, required: false, default: Default.premium.enabled },
        expires: { type: Schema.Types.Mixed, required: false, default: "" },
    },
    color: { type: String, required: false, default: Default.color },
    _247: {
        channelID: { type: String, required: false, default: Default._247.channelID, },
        enabled: { type: Boolean, required: false, default: Default._247.enabled },
    },
    disabled: {
        channels: { type: Array, required: false, default: Default.disabled.channels },
        commands: { type: Array, required: false, default: Default.disabled.commands },
        voice: { type: Array, required: false, default: Default.disabled.voice },
    },
    beta: { type: Boolean, required: false, default: Default.beta },
    dj_role: { type: String, required: false, default: Default.dj_role },
    blocked: {
        enabled: { type: Boolean, required: false, default: false },
        reason: { type: String, required: false, default: "" },
    }
});
export default mongoose.model("Guild", guildSchema, "Guilds");