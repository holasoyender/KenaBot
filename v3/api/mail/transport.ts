/*
 * File:          transport.ts
 * Path:          v3\api\mail\transport.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-01 18:10:45 +02:00
 *
 * SHA-256: fef6b62abdf0dbe76bfe5b9736f02150497d8ca200304f0ab17b407847e1753a
 *
 */


import nodemailer from 'nodemailer';

export const transport = nodemailer.createTransport(
    {
        service: 'gmail',
        auth:{
            user: process.env.EMAIL,
            pass: process.env.EMAIL_PASSWORD
        }
    }
);