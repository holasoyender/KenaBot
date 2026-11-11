/*
 * File:          patreon.ts
 * Path:          v3.5\api\database\schema\patreon.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 9599c6b8c98c4cf6cfd8be9228106566ad9043283d66ccfc5ad26d3c881efb9c
 *
 */


import mongoose from "mongoose";
import {stats_database} from "../mongo";

export const patreon_schema = stats_database.model("Patreon",
    new mongoose.Schema({
        email: String,
        full_name: String,
        patron_status: String,
        will_pay: Number,
        discord_id: String,
        patreon_id: String,
        claimed: Boolean,
        tier: {
            name: String,
            amount: Number
        },
        codes: [String],
        claimed_at: Date,
    }),
    "Patreons");

export const patreon_raw_schema = stats_database.model("PatreonRaw",
    new mongoose.Schema({
        data: Object
    }),
    "PatreonRaws");