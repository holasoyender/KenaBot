/*
 * File:          env.ts
 * Path:          v4\api\config\env.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 8ed285a044aba2e64942c03117112e9e0afb229a565ecef1d68f3826f130ce0b
 *
 */


import fs from "fs";
import path from "path";

const LINE = /(?:^|^)\s*(?:export\s+)?([\w.-]+)(?:\s*=\s*?|:\s+?)(\s*'(?:\\'|[^'])*'|\s*"(?:\\"|[^"])*"|\s*`(?:\\`|[^`])*`|[^#\r\n]+)?\s*(?:#.*)?(?:$|$)/mg

function parse(src: string) {
    const obj: any = {}

    let lines = src.toString()
    lines = lines.replace(/\r\n?/mg, '\n')

    let match
    while ((match = LINE.exec(lines)) != null) {
        const key: string = match[1]

        let value: string = (match[2] || '')
        value = value.trim()
        const maybeQuote = value[0]
        value = value.replace(/^(['"`])([\s\S]*)\1$/mg, '$2')

        if (maybeQuote === '"') {
            value = value.replace(/\\n/g, '\n')
            value = value.replace(/\\r/g, '\r')
        }

        obj[key] = value
    }

    return obj
}

function _log(message: string) {
    console.log(`[dotenv][DEBUG] ${message}`)
}

export default function config(options?: { path?: string, encoding?: string, debug?: boolean, override?: boolean }) {
    const dotenvPath = path.resolve(process.cwd(), '.env')
    const debug = Boolean(options && options.debug)
    const override = Boolean(options && options.override)

    try {
        const parsed = parse(fs.readFileSync(dotenvPath, {encoding: "utf8"}))

        Object.keys(parsed).forEach(function (key) {
            if (!Object.prototype.hasOwnProperty.call(process.env, key)) {
                process.env[key] = parsed[key]
            } else {
                if (override === true) {
                    process.env[key] = parsed[key]
                }

                if (debug) {
                    if (override === true) {
                        _log(`"${key}" is already defined in \`process.env\` and WAS overwritten`)
                    } else {
                        _log(`"${key}" is already defined in \`process.env\` and was NOT overwritten`)
                    }
                }
            }
        })

        return {parsed}
    } catch (e: any) {
        if (debug) {
            _log(`Failed to load ${dotenvPath} ${e.message}`)
        }

        return {error: e}
    }
}