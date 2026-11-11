/*
 * File:          index.ts
 * Path:          v2\plugins\apple\types\index.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 22:32:37 +02:00
 *
 * SHA-256: f24a6d10d52578cfaeea6d42d4d9578be15432e366c322d0a10a95268150127e
 *
 */


export interface AppleMusicOptions {
    cacheTrack?: boolean;
    maxCacheLifeTime?: number;
    convertUnresolved?: boolean;
}

export interface AppleMusicTrack {
    name: string;
    duration: number;
    uri: string;
    artist: string;
}

export interface AppleMusicMetaTagResponse {
    MEDIA_API: {
        token: string;
    }
}
