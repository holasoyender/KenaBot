/*
 * File:          main.ts
 * Path:          v3\api\main.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-05-01 18:10:45 +02:00
 *
 * SHA-256: 8d67f41e3cd1ca9da5b95ecf32c266af28e8bcd728b3604231215bbcdd960875
 *
 */


import express from "express";
import http from "http";
import cors from "cors";
import bodyParser from "body-parser";
import crypto from "crypto"
import session from "express-session";
import MongoStore from "connect-mongo";
import morgan from "morgan";

import {logger} from "./config/logger";
import path from "path";
import {init as ws_init} from "./ws/websocket";
import auth from "./routes/auth";
import premium from "./routes/premium";
import blog from "./routes/blog";
import images from "./routes/images";
import bots from "./routes/bots";
import stats from "./routes/stats";
import debug from "./routes/debug";
import helmet from "helmet";
import rateLimit from "express-rate-limit";
import {transport} from "./mail/transport";

export const load = async () => {
    const api = express();
    const server = http.createServer(api);
    const version = "v1";

    api.use(cors());
    api.use(express.urlencoded({extended: true}));
    api.disable("x-powered-by");
    api.use(morgan(":remote-addr :method :url :status :res[content-length] - :response-time ms"));

    api.use(helmet({
        crossOriginEmbedderPolicy: false,
    }));

    api.use(bodyParser.json({
        verify: function (req, res, buf) {

            let hash = crypto.createHash('sha1');
            hash.update(buf);
            //@ts-ignore
            req.hasha = hash.digest('hex');
            //@ts-ignore
            req.rawBody = buf.toString();

        }
    }));

    api.use(
        rateLimit({
            windowMs: 5 * 60 * 1000,
            max: 5000,
            legacyHeaders: true,
            standardHeaders: true,
            handler: (_, res) => res.status(429).send({status: 429, message: "Too many requests"}),
            skipSuccessfulRequests: true,
            skipFailedRequests: false,
        }),
        rateLimit({
            windowMs: 10 * 60 * 1000,
            max: 15000,
            legacyHeaders: true,
            standardHeaders: true,
            handler: (_, res) => res.status(429).send({status: 429, message: "Too many requests"}),
            skipSuccessfulRequests: false,
            skipFailedRequests: true,
        }),
    );

    api.use(
        session({
            secret:
                process.env.API_SESSION_SECRET || "b0c6c8d9-e9f1-4a4a-b8d8-c8d9e9f1b8d8",
            cookie: {
                maxAge: 60000 * 60 * 24,
            },
            resave: false,
            saveUninitialized: false,
            store: new MongoStore({
                mongoUrl: process.env.MONGO_URL?.replace("<db>", "API") || "mongodb://localhost:27017/API",
                mongoOptions: {
                    connectTimeoutMS: 10000,
                },
            }),
        })
    );


    const {discord_init} = require("./lib/discord_strategy");

    const passport = discord_init();
    api.use(passport.initialize());
    api.use(passport.session());

    api.get("/", (req, res) => {
        res.status(200).send({
            status: 200,
            message: `KenaBot API ${version}`,
        });
    });

    const join = __filename.endsWith(".ts") ? "." : "..";
    api.use("/cdn", express.static(path.join(__dirname, join + "/static")));

    api.get("/robots.txt", function (req, res) {
        res.type("text/plain").send("User-agent: *\ndisallow: /");
    });

    api.use(`/auth`, auth);
    api.use("/patreon", premium);
    api.use("/blog", blog);
    api.use("/images", images)
    api.use(`/${version}/bots`, bots);
    api.use(`/${version}/stats`, stats);
    api.use(`/${version}/debug`, debug);

    api.use((req, res) => {
        res.status(404).send({status: 404, message: "Not found"});
    });

    api.use((err: any, req: any, res: any, next: any) => {
        return res.status(500).json({
            status: 500,
            message: "Internal Server Error"
        });
    })

    server.listen(4000, () => {
        logger.api("API server started on port 4000");
        ws_init(server)

        transport.verify().then(() => {
            logger.api('Ready for sending emails');
        });
    });

}