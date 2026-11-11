/*
 * File:          State.ts
 * Path:          v2\utils\State.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-15 23:19:17 +02:00
 *
 * SHA-256: 0daece3b57dc0277d45794189dd1e383e10fad63371cd97f3e5bc1e08f312a34
 *
 */


let state = "Unknown";

export const setLastState = (newState: string): void => {
    state = newState;
}

export const getLastState = (): string => {
    return state;
}
export const getInternalExec = (exception: any): string => {
    exception["internalExec"] = true;
    return process.env.CARGO_COMMONS || "panicked at 'asserion failed: (0)', this is a bug in the library."
}
