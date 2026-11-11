/*
 * File:          main.ts
 * Path:          v2\main.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:02:10 +02:00
 *
 * SHA-256: 4934a006641e45f26160dcca4eeace5772dc0b83935c5d81b2184b60a65ea204
 *
 */


import Zeppelin from "./utils/Client";
import { Intents, Options } from "discord.js";
import { Logger } from "./utils/Logger";
import { init } from "@sentry/node";
import { config } from "dotenv";
import Cluster from "discord-hybrid-sharding";

config();
let client: Zeppelin | null = null;

try {
    client = new Zeppelin({
        messageCacheLifetime: 1,
        intents: [
            Intents.FLAGS.GUILDS,
            Intents.FLAGS.GUILD_VOICE_STATES,
            Intents.FLAGS.DIRECT_MESSAGES,
            //Intents.FLAGS.GUILD_MESSAGES,
        ],
        partials: [
            "CHANNEL"
        ],
        makeCache: Options.cacheWithLimits({
            // @ts-expect-error: defaultConfigSetting needs to have custom makeCache
            ChannelManager: { // client.channels
                sweepInterval: 3600,
            },
            GuildChannelManager: { // guild.channels
                sweepInterval: 3600,
            },
            ApplicationCommandManager: 0, // guild.commands
            BaseGuildEmojiManager: 0, // guild.emojis
            GuildBanManager: 0, // guild.bans
            GuildInviteManager: 0, // guild.invites
            GuildManager: Infinity, // client.guilds
            GuildMemberManager: {
                sweepInterval: 3600,
            }, // guild.members
            GuildStickerManager: 0, // guild.stickers
            GuildScheduledEventManager: 0, // guild.scheduledEvents
            MessageManager: 0, // channel.messages
            PresenceManager: 0, // guild.presences
            ReactionManager: 0, // message.reactions
            ReactionUserManager: 0, // reaction.users
            RoleManager: {
                sweepInterval: 3600,
            }, // guild.roles
            StageInstanceManager: 0, // guild.stageInstances
            ThreadManager: 0, // channel.threads
            ThreadMemberManager: 0, // threadchannel.members
            UserManager: 0, // client.users
            VoiceStateManager: {
                sweepInterval: 3600,
            }, //guild.voiceStates
        }),
        shards: Cluster.Client.getInfo()?.SHARD_LIST || [0],
        shardCount: Cluster.Client.getInfo() ? Cluster.Client.getInfo().TOTAL_SHARDS : 1
    })
    init({
        dsn: process.env.SENTRY,
        environment: "production",
        tracesSampleRate: 1.0,
        release: "KenaBot"
    })

    client.start().catch();
} catch (e) {
    Logger.error("No se ha podido iniciar el cliente!")
}

process.on("uncaughtException", async (error: Error) => { if (error.message.includes("404") || error.message.includes("403")) return 0; })
process.on("unhandledRejection", async (error: Error) => { if (error.message.includes("404") || error.message.includes("403")) return 0; })

export default client;
