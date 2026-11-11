/*
 * File:          premium.ts
 * Path:          v3\api\database\schema\premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: ca8b0b9e926537032e56db255c521d15223a16122040b3242d4191405f7ad930
 *
 */


import mongoose from "mongoose";
import {stats_database} from "../mongo";

export const premium_schema = stats_database.model("Premium",
    new mongoose.Schema({
        code: String,
        duration: String,
        createdAt: Date,
        claimed: Boolean,
        claimedAt: String,
        claimedOn: Date,
        createdBy: String,
        bots: Array,
    }),
    "Premiums");