/*
 * File:          Backup.ts
 * Path:          v2\database\schema\Backup.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-19 19:51:42 +02:00
 *
 * SHA-256: f610a69fa92e74d1f09b8016b51e57519631613cf5a144bdad13b3136fcb40f1
 *
 */


import mongoose from "mongoose";

const backupSchema = new mongoose.Schema({
    guildID: { type: String, required: true, unique: true },
    textChannelID: { type: String, required: true },
    voiceChannelID: { type: String, required: true },
    queue: { type: Array, required: false, default: [] },
    current: { type: Object, required: false },
    currentMetadata: { type: String, required: false },
    volume: { type: Number, required: false, default: 50 },
    playing: { type: Boolean, required: false, default: false },
    paused: { type: Boolean, required: false, default: false },
    loop: { type: Boolean, required: false, default: false },
    position: { type: Number, required: false, default: 0 },
});
export default mongoose.model("Backup", backupSchema, "Backups");