/*
 * File:          index.ts
 * Path:          v4\api\index.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 5796e38c54765345316eec43e69f16e60e3b5cb9b586adb27f8fc48e0bf38e0f
 *
 */


import { init as mongo_init } from "./database/mongo";
import env from "./config/env";

env()
mongo_init().then(async () => {
    const { load } = await import("./main")
    await load()
})
