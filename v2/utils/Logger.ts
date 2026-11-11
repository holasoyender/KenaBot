/*
 * File:          Logger.ts
 * Path:          v2\utils\Logger.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-23 16:54:52 +01:00
 *
 * SHA-256: d8fd4fdf190e7c2883639bdcec62f6c9aee1f78849394e4229a7f8828a7749ba
 *
 */


import chalk from 'chalk';

export const Logger = {

    db: function (msg: string) { console.log(chalk.cyan("DB") + chalk.white(" - ") + chalk.green(msg)) },

    error: function (msg: string) { console.log(chalk.red("ERROR") + chalk.white(" - ") + chalk.red(msg)) },

    shard: function (msg:string) { console.log(chalk.yellow("SHARD")+ chalk.white(" - ")+chalk.green(msg)) },

    ws: function (msg: string) { console.log(chalk.yellow("WS") + chalk.white(" - ") + chalk.green(msg)) },

    start: function (msg:string) { console.log(chalk.bold(chalk.yellow("+")+ chalk.white(" - ")+chalk.blue(msg))) },

    lava: function (msg:string) { console.log(chalk.bold(chalk.cyan("LAVA")+ chalk.white(" - ")+chalk.whiteBright(msg))) },

    api: function (msg: string) { console.log(chalk.green("API") + chalk.white(" - ") + chalk.whiteBright(msg)) },

    cluster: function (msg: string) { console.log(chalk.green("CLUSTER") + chalk.white(" - ") + chalk.whiteBright(msg)) }

}