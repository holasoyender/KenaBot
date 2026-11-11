/*
 * File:          discord.ts
 * Path:          v2\api\strategies\discord.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-27 18:30:10 +02:00
 *
 * SHA-256: 230aaca1bcb9152de7c11b366284b8918f5f8b606e2ebb448d427f2d5bcf2222
 *
 */


import passport from "passport";
import User from "../database/schema/User";
import refresh from "passport-oauth2-refresh";
import Discord, { Profile } from "passport-discord";
import {DISCORD_ID, DISCORD_SECRET} from "../utils/config";

passport.serializeUser(async (user: any, done) => {
    const usr = await user;
    done(null, await usr.id);
});

passport.deserializeUser(async (id, done) => {
    try {
        const user = await User.findOne({id});

        return user ? done(null, user) : done(null, null);
    } catch (e) {
        console.log(e);
        done(e, null);
    }
});
export const discord_init = (): void => {
    const discordStrat = new Discord(
        {
            clientID: DISCORD_ID,
            clientSecret: DISCORD_SECRET,
            callbackURL: "/auth/redirect",
            scope: ["guilds", "identify"],
        },
        async (
            accessToken: string,
            refreshToken: string,
            profile: Profile,
            done: any
        ) => {
            try {
                const {id, username, discriminator, guilds} = profile;
                let {avatar} = profile;
                avatar = avatar
                    ? `https://cdn.discordapp.com/avatars/${id}/${avatar}.png`
                    : "https://cdn.discordapp.com/embed/avatars/0.png";

                let user:any = await User.findOne({id});

                if (user) {
                    user.username = username + "#" + discriminator;
                    user.avatar = avatar;
                    user.access_token = accessToken;
                    user.guilds = guilds;
                    user.save();
                    user = await User.findOne({id});
                } else {
                    user = new User({
                        id,
                        username: username + "#" + discriminator,
                        avatar,
                        access_token: accessToken,
                        guilds,
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
    passport.use(discordStrat);
    refresh.use(discordStrat);
}