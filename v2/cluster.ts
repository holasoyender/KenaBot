/*
 * File:          cluster.ts
 * Path:          v2\cluster.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 17:08:32 +02:00
 *
 * SHA-256: c2df104413d32340400ad5734249146e7e352a26d41833300c591b363a0304ff
 *
 */


import Cluster from "discord-hybrid-sharding"
import { Logger } from "./utils/Logger";
import { config } from "dotenv";

config();
const manager = new Cluster.Manager(`${__dirname}/main.js`, {
    totalShards: 3,
    shardsPerClusters: 3,
    mode: "process",
    token: process.env.TOKEN,
})

manager.on('clusterCreate', cluster => Logger.cluster(`Launched Cluster ${cluster.id}`));
manager.spawn({ timeout: -1 });
