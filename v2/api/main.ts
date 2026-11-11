/*
 * File:          main.ts
 * Path:          v2\api\main.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-29 15:59:06 +02:00
 *
 * SHA-256: e6ff41d311fa996073a6ce008549a5fd797b4670f5d85ce4a8556cb6450484a8
 *
 */


import express from "express";
import http from "http";
import ws from "ws";
import cors from "cors";
import { logger } from "./utils/logger";
import path from "path";
import router from "./routes";
import IWebSocket from "./types/WebSocket";
import init from "./database/mongo";
import session from "express-session";
import MongoStore from 'connect-mongo';
import {getApiDatabaseURL} from "./utils/config";
import passport from "passport";
import auth from "./routes/auth";
import {discord_init} from "./strategies/discord";

const api = express();
const server = http.createServer(api);
const wss = new ws.Server({ server });
const version = "v1";
const PASSWORD = "1234"

api.use(cors());
api.use(express.json());
api.use(express.urlencoded({ extended: true }));
api.disable('x-powered-by');

init().catch();

api.use(
    session({
        secret:
            process.env.API_SESSION_SECRET ||
            "b0c6c8d9-e9f1-4a4a-b8d8-c8d9e9f1b8d8",
        cookie: {
            maxAge: 60000 * 60 * 24,
        },
        resave: false,
        saveUninitialized: false,
        store: new MongoStore({
            mongoUrl: getApiDatabaseURL(),
            mongoOptions: {
                connectTimeoutMS: 10000,
            }
        })
    })
);

discord_init();
api.use(passport.initialize())
api.use(passport.session())

wss.on("connection", (ws: IWebSocket, req) => {
    if (req.headers.authorization) {
        const password = req.headers.authorization;
        if (password !== PASSWORD) {
            ws.close(1003, "Unauthorized");
            return;
        }

        const id = req.headers["client-name"]?.toString();

        for (const client of wss.clients) {
            //@ts-ignore
            if (client.id == id) {
                logger.error(`${id} is already connected`);
                ws.close(1003, "Already connected");
                return;
            }
        }
        ws.id = id;
        ws.setMaxListeners(100);
        logger.ws("Nuevo cliente conectado con ID: " + ws.id);

    }
    ws.on("close", () => {
        logger.ws("Cliente desconectado con ID: " + ws.id);
    })
});

wss.on("close", (ws: IWebSocket) => {
    if (ws.id) {
        logger.ws("Cliente con ID: " + ws.id + " desconectado");
    }
})

wss.on("error", (ws: IWebSocket) => {
    if (ws.id) {
        logger.ws("Cliente con ID: " + ws.id + " desconectado");
    }
})

api.get("/", (req, res) => {
    res.status(200).send({
        status: 200,
        message: `KenaBot API ${version}`,
    });
});

api.get('/robots.txt', function (req, res) {
    res.type('text/plain').send('User-agent: *\ndisallow: /');
})

api.use(`/${version}`, router);
api.use(`/auth`, auth);

api.get("/favicon.ico", (req, res) => {
    res.sendFile(path.join(__dirname, "./static/favicon.ico"));
})

api.use("/cdn", express.static(path.join(__dirname, "./static")));

api.post("/logout", async (req: any, res: any) => {
    if(!req.user) return res.status(401).send({
        status: 401,
        message: "Unauthorized"
    })

    await req.session.destroy();
    res.send({
        status: 200,
        message: `OK`,
    });
});

api.use((req, res) => {
    res.status(404).send({ status: 404, message: 'Not found' });
});

server.listen(4000, () => {
    logger.api("API Central iniciada en el puerto 4000");
})

export default wss;
