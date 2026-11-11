/*
 * File:          stats.ts
 * Path:          v2\api\cache\stats.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-05-03 17:45:45 +02:00
 *
 * SHA-256: ed2fe4d7275a7c6d932c5edd0a9afe510ba878d020c0abc9d7b92fafbbf92fb8
 *
 */


let cache = {
    commands: {
        total_uses: 0,
        command_count: 0,
        top_commands: [""]
    },
    voice: {
        total_joins: 0,
        channel_count: 0,
    },
    guilds: 0,
    users: 0,
    renew_at: 0,
    renewed: false
}

const get_cache = () => {
    return cache;
}
const set_cache = (data: typeof cache) => {
    cache = data
    cache.renew_at = Date.now() + 1000 * 60 * 60 * 24
    cache.renewed = true
}

export default {
    get_cache,
    set_cache
}