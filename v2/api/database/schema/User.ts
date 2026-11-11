/*
 * File:          User.ts
 * Path:          v2\api\database\schema\User.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:27:27 +02:00
 *
 * SHA-256: 631ebe363bdd02bab32f106516d861f4512c82d7d4263765d1a974d377ea432c
 *
 */


import {apidb} from "../mongo";
import mongoose from "mongoose";

const User = new mongoose.Schema({
    id: { type: String, required: true, unique: true },
    username: { type: String, required: true },
    avatar: { type: String, required: true },
    access_token: { type: String, required: true },
    guilds: { type: Array, required: true }
});

export default apidb.model("User", User, "Users");