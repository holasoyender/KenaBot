/*
 * File:          logger.ts
 * Path:          v4\api\config\logger.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: e444c63e46f596fc56ca57b067796f301f622118b8745a6dfb4013528e694741
 *
 */


import chalk from 'chalk';

export const logger = {

    db: function (msg: string) {
        console.log(chalk.cyan("DB ") + chalk.white(" - ") + chalk.green(msg))
    },
    error: function (msg: string) {
        console.log(chalk.red("ERROR ") + chalk.white(" - ") + chalk.red(msg))
    },
    ws: function (msg: string) {
        console.log(chalk.yellow("WS ") + chalk.white(" - ") + chalk.green(msg))
    },
    api: function (msg: string) {
        console.log(chalk.green("API ") + chalk.white(" - ") + chalk.whiteBright(msg))
    }

}