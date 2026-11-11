/*
 * File:          blog.ts
 * Path:          v4\api\database\schema\blog.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-05-01 19:34:27 +02:00
 *
 * SHA-256: 34837df6f0e01658b6c2aa66ce9028a5e5fbd257fe4d7afd4abfa8507fbd8248
 *
 */


import mongoose from "mongoose";
import {api_database} from "../mongo";

export const blog_schema = api_database.model("Blog",
    new mongoose.Schema({
        id: Number,
        title: String,
        icon: String,
        banner: String,
        category: String,
        tags: [String],
        created_at: Number,
        content: String,
        author: {
            name: String,
            description: String,
            avatar: String,
        }
    }),
    "Blogs");

export const generateId = async (): Promise<number> => {
    const lastEntry = await blog_schema.findOne().sort({id: -1});
    if (!lastEntry) return 1;
    return lastEntry.id + 1;
}
