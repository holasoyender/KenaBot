/*
 * File:          Commands.ts
 * Path:          v2\api\database\schema\Commands.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-02 18:35:10 +02:00
 *
 * SHA-256: ae1e17b1b3078af7de79e605c2533d01ead91ccd7a82b8d842bb6ae54fbefa8a
 *
 */


import mongoose from "mongoose";

const cmdSchema = new mongoose.Schema({
    command: String,
    uses: Number,
});
export default mongoose.connection.model("Command", cmdSchema, "Commands");