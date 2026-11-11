/*
 * File:          user.ts
 * Path:          v3\api\database\schema\user.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:37:53 +02:00
 *
 * SHA-256: 15b8bc7e7a308b8079e97767b55d3d4a8a6955acf1f045edb64bdb23958a17f8
 *
 */


import mongoose from "mongoose";
import {api_database} from "../mongo";

export const user_schema = api_database.model("User",
    new mongoose.Schema({
        id: {type: String, required: true, unique: true},
        username: {type: String, required: true},
        avatar: {type: String, required: true},
        access_token: {type: String, required: true},
        guilds: {type: Array, required: true}
    }),
    "Users");
