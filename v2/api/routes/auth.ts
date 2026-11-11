/*
 * File:          auth.ts
 * Path:          v2\api\routes\auth.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-18 14:13:40 +02:00
 *
 * SHA-256: c3fe3528e1718b6e71b3bd02a26ad7e9a6acf9ad2b1bd9974e19470a1173e64c
 *
 */


import { Router } from "express";
import passport from "passport";
import { REDIRECT } from "../utils/config";
import cookie from "cookie-signature";
const router = Router();

router.get("/login", passport.authenticate("discord"));

router.get("/user", (req, res) => {
  if (!req.user)
    return res.status(200).send({ status: 401, message: "Unauthorized" });
  // @ts-ignore
  const { id, username, avatar, guilds } = req.user;
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
});

router.get("/redirect", async (req, res, next) => {
  try {
    passport.authenticate("discord", (err, user) => {
      if (err) {
        //console.log(err);
        return res.redirect("/auth/login");
      }

      if (!user)
        return res.redirect(REDIRECT + "?error=DISCORD_USER_NOT_FOUND");

      req.login(user, async (e) => {
        if (e) return res.redirect(REDIRECT + "?error=DISCORD_LOGIN_FAILED");

        if (REDIRECT == "http://localhost:3000") {
          return res.redirect(REDIRECT);
        } else {
          return res
            .setHeader(
              "Set-Cookie",
              "connect.sid=s%3A" +
                cookie.sign(
                  req.session.id,
                  process.env.API_SESSION_SECRET ||
                    "b0c6c8d9-e9f1-4a4a-b8d8-c8d9e9f1b8d8"
                ) +
                "; Domain=kenabot.xyz; Path=/; HttpOnly"
            )
            .redirect(REDIRECT);
        }
      });
    })(req, res, next);
  } catch (e) {
    console.log(e);
    return res.redirect(REDIRECT + "?error=DISCORD_LOGIN_CACHED_ERROR");
  }
});
export default router;
