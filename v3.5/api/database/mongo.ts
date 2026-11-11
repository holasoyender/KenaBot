/*
 * File:          mongo.ts
 * Path:          v3.5\api\database\mongo.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 96b54a2a20358e485478ea7161e1e67f966d2f2552fc12e1005d95bd8a424162
 *
 */


import mongoose from "mongoose";
import {logger} from "../config/logger";

export let stats_database: mongoose.Connection;
export let api_database: mongoose.Connection;

export const init = async () => {

    const base_url = process.env.MONGO_URL || "";
    if (!base_url) throw new Error("No se ha encontrado la URL de MongoDB");
    if (!base_url.includes("<db>")) throw new Error("La URL de MongoDB no contiene el marcador <db>");

    stats_database = await mongoose.createConnection(base_url.replace("<db>", "Stats") || "", {
        autoIndex: false,
        connectTimeoutMS: 10000,
    });

    api_database = await mongoose.createConnection(base_url.replace("<db>", "API"), {
        autoIndex: false,
        connectTimeoutMS: 10000,
    });

    stats_database.on('connected', () => logger.db(`Conectado con la base de datos de estadística.`));
    stats_database.on('err', err => logger.error(`MongoDB error:  ${err}`));
    stats_database.on('disconnected', () => logger.error(`Desconectado de MongoDB`))

    api_database.on('connected', () => logger.db(`Conectado con la base de datos de la API.`));
    api_database.on('err', err => logger.error(`MongoDB error:  ${err}`));
    api_database.on('disconnected', () => logger.error(`Desconectado de MongoDB`))

}
