/*
 * File:          Commands.ts
 * Path:          v2\database\schema\Commands.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-23 20:20:09 +01:00
 *
 * SHA-256: e67f4bfe33ca8fa247ecabb8f39ec49e651cb039f618b939c3c8a39274fb5c37
 *
 */


import mongoose from "mongoose";
import {stats} from "../stats";

const cmdSchema = new mongoose.Schema({
    command: String,
    uses: Number,
});
export default stats.model("Command", cmdSchema, "Commands");