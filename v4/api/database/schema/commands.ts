/*
 * File:          commands.ts
 * Path:          v4\api\database\schema\commands.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:38:13 +02:00
 *
 * SHA-256: 9ca4396417b4b37972f80dab0e56da7bb86b5c0968d577e5c7f800ced4484c17
 *
 */


import mongoose from "mongoose";
import {stats_database} from "../mongo";

export const commands_schema = stats_database.model("Command",
    new mongoose.Schema({
        command: String,
        uses: Number,
    }),
    "Commands");
