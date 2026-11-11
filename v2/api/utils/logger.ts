/*
 * File:          logger.ts
 * Path:          v2\api\utils\logger.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-27 16:13:23 +02:00
 *
 * SHA-256: 8ca2c902445e775a978c6d8503d23210c39c81d7dd756792ac477a1928be3dad
 *
 */


import chalk from 'chalk';

export const logger = {

    db: function (msg: string) { console.log(chalk.cyan("DB ") + chalk.white(" - ") + chalk.green(msg)) },
    error: function (msg: string) { console.log(chalk.red("ERROR ") + chalk.white(" - ") + chalk.red(msg)) },
    ws: function (msg: string) { console.log(chalk.yellow("WS ") + chalk.white(" - ") + chalk.green(msg)) },
    api: function (msg: string) { console.log(chalk.green("API ") + chalk.white(" - ") + chalk.whiteBright(msg)) }

}