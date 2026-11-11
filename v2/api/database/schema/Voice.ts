/*
 * File:          Voice.ts
 * Path:          v2\api\database\schema\Voice.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-02 18:35:10 +02:00
 *
 * SHA-256: c479023bf4ce0492ecd7b3126e25b1d0afc3f5c9780801a0de4a632bb1764048
 *
 */


import mongoose from "mongoose";

const joinSchema = new mongoose.Schema({
    channelID: String,
    joins: Number,
});
export default mongoose.connection.model("Join", joinSchema, "Joins");