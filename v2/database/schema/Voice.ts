/*
 * File:          Voice.ts
 * Path:          v2\database\schema\Voice.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-23 20:20:09 +01:00
 *
 * SHA-256: 61c5be64bc7a2fe23d4ebeb8d0cef2e469c6dc572e61106ae61c789a3d46d22b
 *
 */


import mongoose from "mongoose";
import {stats} from "../stats";

const joinSchema = new mongoose.Schema({
    channelID: String,
    joins: Number,
});
export default stats.model("Join", joinSchema, "Joins");