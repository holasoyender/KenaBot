/*
 * File:          stats.ts
 * Path:          v2\database\stats.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-23 20:20:09 +01:00
 *
 * SHA-256: a4e2bbbc8326417f973feb3424db53c92b2bfa820d9cd6fce348454937b1f98a
 *
 */


import mongoose from "mongoose";
import {Logger} from "../utils/Logger";
import {getStatsDatabaseURL} from "./dbconfig";
import {config} from "dotenv";
config(); //Por si acaso kekw

const dbInfo = getStatsDatabaseURL();
if (!dbInfo) {
    Logger.error("No database URL found!");
    process.exit(1);
}

export const stats = mongoose.createConnection(dbInfo.url, {
    autoIndex: false,
    connectTimeoutMS: 10000,
});


stats.on('connected', () => Logger.db(`Conectado con la base de datos "${dbInfo.name}".`));
stats.on('err', err => Logger.error(`MongoDB error:  ${err}`));
stats.on('disconnected', () => Logger.error(`Desconectado de MongoDB`))
