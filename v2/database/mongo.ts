/*
 * File:          mongo.ts
 * Path:          v2\database\mongo.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-01-23 20:20:09 +01:00
 *
 * SHA-256: bd49102cd6170fdf816f5aeec42c7c5f52cb2c66a276203beba7556ce32ab207
 *
 */


import mongoose from "mongoose";
import { Logger } from "../utils/Logger";
import Zeppelin from "../utils/Client";
import {getDatabaseURL} from "./dbconfig";

export default async function init(client:Zeppelin) {

    const dbInfo = getDatabaseURL(client);
    if(!dbInfo) {
        Logger.error("No database URL found!");
        return;
    }

    if(dbInfo.url)
        mongoose.connect(dbInfo.url, {
            autoIndex: false,
            connectTimeoutMS: 10000,
        });

    mongoose.connection.on('connected', () => Logger.db(`Conectado con la base de datos "${dbInfo.name}".`));
    mongoose.connection.on('err', err => Logger.error(`MongoDB error:  ${err}`));
    mongoose.connection.on('disconnected', () => Logger.error(`Desconectado de MongoDB`))
}