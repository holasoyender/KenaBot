/*
 * File:          Premium.ts
 * Path:          v2\database\schema\Premium.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-09 18:02:12 +02:00
 *
 * SHA-256: 3e07fc9ee3346da2694ca2843cccfa86a544bb8369bdb68e7cba480fbb6a1c87
 *
 */


import mongoose from "mongoose";
import {stats} from "../stats";

const premiumSchema = new mongoose.Schema({
    code: String,
    duration: String,
    createdAt: Date,
    claimed: Boolean,
    claimedAt: String,
    claimedOn: Date,
    createdBy: String,
    bots: Array,
});
export default stats.model("Premium", premiumSchema, "Premiums");