/*
 * File:          config.ts
 * Path:          v2\api\utils\config.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-11 21:28:22 +02:00
 *
 * SHA-256: 5393d5888298595aa5823d2b2e5afa8270932fc7cedd0e810cce1a13d0685367
 *
 */


const MONGODB_URL = "mongodb+srv://admin:******@********************************/Stats?retryWrites=true&w=majority";

export const getStatsDatabaseURL = () => MONGODB_URL;
export const getApiDatabaseURL = () => MONGODB_URL.replace("Stats", "API");

export const DISCORD_ID = "910965586622816326";
export const DISCORD_SECRET = "************************";
//export const REDIRECT = "https://preview.kenabot.xyz"
export const REDIRECT = "http://localhost:3000"
