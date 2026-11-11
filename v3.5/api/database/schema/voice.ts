/*
 * File:          voice.ts
 * Path:          v3.5\api\database\schema\voice.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:38:04 +02:00
 *
 * SHA-256: 28c5ca904dc984d751981f02bd2474a12f52623ed7ffb6e3624cb2531d49439c
 *
 */


import mongoose from "mongoose";
import {stats_database} from "../mongo";

export const voice_schema = stats_database.model("Join",
    new mongoose.Schema({
        channelID: String,
        joins: Number,
    }),
    "Joins");
