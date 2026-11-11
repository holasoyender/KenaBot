/*
 * File:          stats_cache.ts
 * Path:          v3.5\api\lib\stats_cache.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 20:06:00 +02:00
 *
 * SHA-256: a1e32a518f18c3f79de4019473fcc5ca5f25eafbf50a25bd8ae1f0d642e4b2d1
 *
 */


import axios from "axios";

let cache = {
    command_uses: 0,
    voice_joins: 0,
    guilds: 0,
    users: 0,
    servers: [],
    renew_at: 0,
    renewed: false
}

const get_cache = () => {
    return cache;
}
const set_cache = (data: typeof cache) => {

    let set = data

    if (data.servers.length == 0)
        set.servers = cache.servers

    if (data.guilds < cache.guilds && cache.guilds != 0)
        set.guilds = cache.guilds
    if (data.users < cache.users && cache.users != 0)
        set.users = cache.users

    if (data.guilds == 0 && cache.guilds == 0)
        set.guilds = 60000

    if (data.users == 0 && cache.users == 0)
        set.users = 40000000

    set.renew_at = Date.now() + 1000 * 60 * 60 * 24
    set.renewed = true

    cache = set

    try {
        axios.post("https://top.gg/api/bots/910965586622816326/stats", {
            server_count: data.guilds,
            shard_count: Math.round(data.guilds / 1000),
        }, {
            headers: {
                Authorization: process.env.TOPGG_TOKEN
            }
        }).catch(() => {
        })
    } catch (e) {
        //ignore
    }
}

export default {
    get_cache,
    set_cache
}