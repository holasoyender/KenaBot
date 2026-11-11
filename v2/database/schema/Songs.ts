/*
 * File:          Songs.ts
 * Path:          v2\database\schema\Songs.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-25 19:46:48 +01:00
 *
 * SHA-256: 55b92523c0640ed0215c2bbf4d1e652ef299d17b6cc31aec3ca57f9c9aac1eb0
 *
 */


import mongoose from "mongoose";
import {stats} from "../stats";

const songSchema = new mongoose.Schema({
    url: String,
    plays: Number,
});
export default stats.model("Song", songSchema, "Songs");