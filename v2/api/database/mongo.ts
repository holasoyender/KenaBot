/*
 * File:          mongo.ts
 * Path:          v2\api\database\mongo.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:27:27 +02:00
 *
 * SHA-256: aa6bf94b1f31a653faad9ce427b47d55bf2fdc31011c84067d5edc79a01da645
 *
 */


import mongoose from "mongoose";
import {logger} from "../utils/logger";
import {getApiDatabaseURL, getStatsDatabaseURL} from "../utils/config";

export default async function init() {

    await mongoose.connect(getStatsDatabaseURL(), {
        autoIndex: false,
        connectTimeoutMS: 10000,
    });

    mongoose.connection.on('connected', () => logger.db(`Conectado con la base de datos de estadística.`));
    mongoose.connection.on('err', err => logger.error(`MongoDB error:  ${err}`));
    mongoose.connection.on('disconnected', () => logger.error(`Desconectado de MongoDB`))
}

export const apidb = mongoose.createConnection(getApiDatabaseURL(), {
    autoIndex: false,
    connectTimeoutMS: 10000,
});

apidb.on('connected', () => logger.db(`Conectado con la base de datos de la API.`));
apidb.on('err', err => logger.error(`MongoDB error:  ${err}`));
apidb.on('disconnected', () => logger.error(`Desconectado de MongoDB`))