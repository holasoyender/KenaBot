/*
 * File:          dbconfig.ts
 * Path:          v2\database\dbconfig.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-23 20:20:09 +01:00
 *
 * SHA-256: 3f95a9d0209c9cbbbd41cd7d0a40886153758e966be4aa767ce8db69b793877e
 *
 */


import Zeppelin from "../utils/Client";

export function getDatabaseURL(client:Zeppelin) {

    const baseURL = process.env.MONGODB_URL

    if(!baseURL)
        return null;

    return {
        name: client.user?.username+"",
        url: baseURL.replace("myFirstDatabase", client.user?.username.replace(/ /g, "_")+""),
    }
}

export function getStatsDatabaseURL() {

    const baseURL = process.env.MONGODB_URL

    if(!baseURL)
        return null;

    return {
        name: "Stats",
        url: baseURL.replace("myFirstDatabase", "Stats"),
    }
}