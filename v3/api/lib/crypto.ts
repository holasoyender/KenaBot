/*
 * File:          crypto.ts
 * Path:          v3\api\lib\crypto.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: e764cf18d1e66ea5d50dc772a1aca2cdf1ff01c58ee87d416ada177694705ed5
 *
 */


import { v4 as idv4 } from "uuid";
import crypto_js from "crypto-js"
import { premium_schema } from "../database/schema/premium";

export const generate_codes = (amount: number): string[] => {

    const codes: string[] = [];
    console.log("Trying to generate " + amount + " codes.");
    for (let i = 0; i < amount; i++) {
        const raw_id = idv4();
        const formatted_uuid = raw_id.replace(/-/g, "");

        const store = {
            code: formatted_uuid,
            duration: "31d",
            createdAt: new Date(),
            claimed: false,
            claimedAt: "",
            claimedOn: null,
            createdBy: "System",
            bots: [],
        }

        premium_schema.create(store).then(r => console.log("Created code " + formatted_uuid)).catch(e => console.log(e));
        codes.push(formatted_uuid);
    }

    return codes

}

export const compute_hash = (signature: string, body: any) => {
    const hash = crypto_js.HmacMD5(body, process.env.PATREON_TOKEN || "");
    const digest = hash.toString(crypto_js.enc.Hex);
    return digest;
}