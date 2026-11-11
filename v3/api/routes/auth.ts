/*
 * File:          auth.ts
 * Path:          v3\api\routes\auth.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-15 19:05:01 +02:00
 *
 * SHA-256: e8ddcd3bcfc37e6d51b2eb75f44344f246f7aa59f3e1bd17f133f9844004306d
 *
 */


import {Request, Router} from "express";
import passport from "passport";
import cookie from "cookie-signature";
import {add_redirect, get_redirect, remove_redirect} from "../lib/redirects";

const router = Router();

router.get("/login", (req, _, next) => {

    const redirect = req.query.redirect;
    add_redirect(get_ip(req), redirect as string | null || "kenabot.xyz");

    next();
}, passport.authenticate("discord"));

router.post("/logout", async (req: any, res: any) => {
    if (!req.user)
        return res.status(401).send({
            status: 401,
            message: "Unauthorized",
        });

    await req.session.destroy();
    res.send({
        status: 200,
        message: `OK`,
    });
});

router.get("/user", async (req, res) => {


    if (req.user) {
        const {id, username, avatar, guilds} = req.user as any;

        return res.status(200).send({
            status: 200,
            id,
            username,
            avatar,
            guilds: guilds.map((g: any) => {
                return {
                    id: g.id,
                    name: g.name,
                    icon: g.icon,
                    owner: g.owner,
                    permissions: g.permissions,
                    permissions_new: g.permissions_new,
                };
            }),
        });

    } else {
        try {
            // eslint-disable-next-line @typescript-eslint/no-explicit-any
            const user_id = (req.session as any).passport?.user as string | undefined;
            if (!user_id)
                return res.status(200).send({
                    status: 401,
                    message: "Unauthorized",
                    reason: "Not logged in"
                });
            const user = await (await import("../database/schema/user")).user_schema.findOne({id: user_id})

            if (!user)
                return res.status(200).send({
                    status: 401,
                    message: "Unauthorized",
                    reason: "Not found in the database"
                });

            const {id, username, avatar, guilds} = user as any;

            return res.status(200).send({
                status: 200,
                id,
                username,
                avatar,
                guilds: guilds.map((g: any) => {
                    return {
                        id: g.id,
                        name: g.name,
                        icon: g.icon,
                        owner: g.owner,
                        permissions: g.permissions,
                        permissions_new: g.permissions_new,
                    };
                }),
            });

        } catch (e) {
            console.log(e)
            return res.status(200).send({status: 401, message: "Unauthorized", reason: "Unknown error"});
        }
    }
});

router.get("/redirect", async (req, res, next) => {

    const ip = get_ip(req);

    let redirect = "https://" + (get_redirect(ip) || "kenabot.xyz");

    if (req.headers.host?.startsWith("localhost"))
        redirect = "http://localhost:4000";

    try {

        passport.authenticate("discord", (
            err: any,
            user: Express.User | false | null
        ) => {

            if (err)
                return res.redirect("/auth/login");

            if (!user)
                return res.redirect(redirect + "?error=DISCORD_USER_NOT_FOUND");

            req.login(user, async (e) => {
                if (e) {
                    console.log(e)
                    return res.redirect(redirect + "?error=DISCORD_LOGIN_FAILED");
                }

                remove_redirect(ip);

                if (redirect == "http://localhost:3000") {
                    res.redirect(redirect);
                    return
                } else {
                    res
                        .setHeader(
                            "Set-Cookie",
                            "connect.sid=s%3A" +
                            cookie.sign(
                                req.session.id,
                                process.env.API_SESSION_SECRET || "b0c6c8d9-e9f1-4a4a-b8d8-c8d9e9f1b8d8"
                            ) +
                            "; Domain=kenabot.xyz; Path=/; HttpOnly"
                        )
                        .redirect(redirect);

                    return;
                }
            });
        })(req, res, next);
    } catch (e) {
        return res.redirect(redirect + "?error=DISCORD_LOGIN_CACHED_ERROR");
    }
});


const get_ip = (req: Request) => {
    let ip: string = Array.isArray(req.headers["x-forwarded-for"])
        ? req.headers["x-forwarded-for"][0]
        : req.headers["x-forwarded-for"] || req.connection.remoteAddress?.replace(/^.*:/, "") || "Unknown";
    if (ip === "1") ip = "127.0.0.1"

    return ip
}

export default router;
