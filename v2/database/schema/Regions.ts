/*
 * File:          Regions.ts
 * Path:          v2\database\schema\Regions.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 14:31:21 +02:00
 *
 * SHA-256: 0d3957ed14f7d3122dd7e7701e509f77889ed4d2ffc2171ee5db5d29c21d45d6
 *
 */


import mongoose from 'mongoose';
import { stats } from '../stats';

const regionsSchema = new mongoose.Schema({
    region: String,
    count: Number,
});
export default stats.model('Regions', regionsSchema, 'Regions');
