/*
 * File:          discord_strategy.ts
 * Path:          v4\api\lib\discord_strategy.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: bbb56276a4943c095dcf1c2869ef249db67ebf7381d82c13997e3b62e3da54e0
 *
 */


import passport, {PassportStatic} from "passport";
import refresh from "passport-oauth2-refresh";
import Discord from "passport-discord";
import {user_schema} from "../database/schema/user";

passport.serializeUser(async (user: any, done) => {
    const usr = await user;
    done(null, await usr.id);
});

passport.deserializeUser(async (id, done) => {
    try {
        const user = await user_schema.findOne({id});
        return user ? done(null, user) : done(null, null);
    } catch (e) {
        console.log(e);
        done(e, null);
    }
});

export const discord_init = (): PassportStatic => {
    const discord_strategy = new Discord(
        {
            clientID: process.env.DISCORD_ID || "",
            clientSecret: process.env.DISCORD_SECRET || "",
            callbackURL: "/auth/redirect",
            scope: ["guilds", "identify"],
        },
        async (
            accessToken: string,
            refreshToken: string,
            profile: any,
            done: any
        ) => {
            try {
                const {id, username, discriminator, guilds} = profile;
                let {avatar} = profile;
                avatar = avatar
                    ? `https://cdn.discordapp.com/avatars/${id}/${avatar}.png`
                    : "https://cdn.discordapp.com/embed/avatars/0.png";

                let user: any = await user_schema.findOne({id});

                if (user) {
                    user.username = username + "#" + discriminator;
                    user.avatar = avatar;
                    user.access_token = accessToken;
                    user.guilds = guilds || [];
                    user.save();

                    user = await user_schema.findOne({id});
                } else {
                    user = new user_schema({
                        id,
                        username: username + "#" + discriminator,
                        avatar,
                        access_token: accessToken,
                        guilds: guilds || [],
                    });
                    await user.save();
                }
                return done(null, user);
            } catch (e) {
                console.log(e);
                return done(e, null);
            }
        }
    )
    passport.use(discord_strategy);
    refresh.use(discord_strategy);

    return passport;
}