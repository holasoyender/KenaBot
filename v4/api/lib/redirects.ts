/*
 * File:          redirects.ts
 * Path:          v4\api\lib\redirects.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 15c9df721f0c42cf580895de4a93d43e7fac8bea599ef9f19bdb4a5036b20178
 *
 */


const redirects: Array<{ ip: string, redirect: string }> = []

export const add_redirect = (ip: string, redirect: string) => {

    if (redirects.find(x => x.ip == ip)) {
        redirects.splice(redirects.findIndex(x => x.ip == ip), 1)
    }

    redirects.push({ip, redirect})
}

export const get_redirect = (ip: string) => {
    if (redirects.find(x => x.ip == ip)) {
        return redirects.find(x => x.ip == ip)?.redirect
    }
    return "/"
}

export const remove_redirect = (ip: string) => {
    if (redirects.find(x => x.ip == ip)) {
        redirects.splice(redirects.findIndex(x => x.ip == ip), 1)
    }
}