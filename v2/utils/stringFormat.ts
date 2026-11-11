/*
 * File:          stringFormat.ts
 * Path:          v2\utils\stringFormat.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-04-26 20:17:10 +02:00
 *
 * SHA-256: dae05b56a360879debe03cbf5023257fead451b16a2b69187c3b87869083aeba
 *
 */


export const stringFormat = (string: string) => {
    return string
        .replace(/{.*?}/g, "")
        .replace(/\[.*?]/g, "")
        .replace(/<.*?>/g, "")
        .replace(/\(.*?\)/g, "")
        .replace("lyric", "")
        .replace("lyrics", "")
        .replace("music", "")
        .replace("video", "")
};
