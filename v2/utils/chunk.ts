/*
 * File:          chunk.ts
 * Path:          v2\utils\chunk.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-08 17:46:14 +01:00
 *
 * SHA-256: c564d84b758a2e9760d561d6634e9450e4540617e15e30914e2513182c791509
 *
 */


export default function (Array:any, chunk: number) {
    const output: any[] = [];
    for (let i = 0; i < Array.length; i += chunk) {
        output.push(Array.slice(i, i + chunk));
    }
    return output
}

export const inArray = (array: any[], value: string) => {
    return array.indexOf(value.toLowerCase()) > -1
}
