/*
 * File:          garbageCollector.ts
 * Path:          v2\events\garbageCollector.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-15 17:25:24 +01:00
 *
 * SHA-256: ea6b2c7d7d0da94cb99bb08997fb5c5ee32897e312bcf5e22428ced2a67ad9f3
 *
 */


import Zeppelin from "../utils/Client";

exports.run = async (client:Zeppelin) => {
        client.users.cache.clear();
        client.channels.cache.clear();
}