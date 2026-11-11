/*
 * File:          raw.ts
 * Path:          v2\events\raw.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-10 17:33:39 +01:00
 *
 * SHA-256: 29ab884c7f04614376993c7ab2d162bc2aed1d6b49bfa09bed43bd36678b52c6
 *
 */


import Zeppelin from "../utils/Client";

exports.run = async (client:Zeppelin, d:any) => {
    client.manager?.updateVoiceState(d)
}