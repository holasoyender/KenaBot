/*
 * File:          patreon.ts
 * Path:          v3\api\database\schema\patreon.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-01 18:10:45 +02:00
 *
 * SHA-256: 6b27c2b42ff093e363cbe5823e48211114a6d5cf6c6a9ec28fb14c8fdf46548c
 *
 */


import mongoose from "mongoose";
import {stats_database} from "../mongo";

export const patreon_schema = stats_database.model("Patreon",
    new mongoose.Schema({
        reference: String,
        email: String,
        full_name: String,
        patron_status: String,
        will_pay: Number,
        discord_id: String,
        patreon_id: String,
        tier: {
            name: String,
            amount: Number
        },
        codes: [String],
        claimed: Boolean,
    }),
    "Patreons");

export const patreon_raw_schema = stats_database.model("PatreonRaw",
    new mongoose.Schema({
        data: Object
    }),
    "PatreonRaws");