/*
 * File:          mongo.ts
 * Path:          v3\api\database\mongo.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-04 19:01:52 +01:00
 *
 * SHA-256: 6a70220da4db66ba94d1b79f72298a89091ff86c95505774c47989d02a4cc7b1
 *
 */


import mongoose from "mongoose";
import { logger } from "../config/logger";

export let stats_database: mongoose.Connection;
export let api_database: mongoose.Connection;

export const init = async () => {

    const base_url = process.env.MONGO_URL || "";
    if (!base_url) throw new Error("No se ha encontrado la URL de MongoDB");

    const connection = await mongoose.createConnection(base_url, {
        autoIndex: false,
        connectTimeoutMS: 10000,
    });

    stats_database = connection.useDb("Stats");
    api_database = connection.useDb("API");

    stats_database.on('connected', () => logger.db(`Conectado con la base de datos de estadística.`));
    stats_database.on('err', err => logger.error(`MongoDB error:  ${err}`));
    stats_database.on('disconnected', () => logger.error(`Desconectado de MongoDB`))

    api_database.on('connected', () => logger.db(`Conectado con la base de datos de la API.`));
    api_database.on('err', err => logger.error(`MongoDB error:  ${err}`));
    api_database.on('disconnected', () => logger.error(`Desconectado de MongoDB`))

}
