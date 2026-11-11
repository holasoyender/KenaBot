/*
 * File:          voiceStateUpdate.ts
 * Path:          v2\events\voiceStateUpdate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 9bf7a78f48809da272b8a1aca50982603c73f23fe1331d11113f0a7077db791a
 *
 */


import Zeppelin from "../utils/Client";
import {VoiceBasedChannel, VoiceState} from "discord.js";
import delay from "delay";
import Voice from "../database/schema/Voice";
import Guild from "../database/schema/Guild";
import Default from "../utils/config/default";

exports.run = async (client:Zeppelin, oldState:VoiceState, newState:VoiceState) => {

    const db = await Guild.findOne({
        guildID: oldState.guild.id
    })

    let guildConfig: any;
    guildConfig = db
    if (!db) guildConfig = Default;

    if (oldState.member?.id == client.user?.id) {
        const guildId = newState.guild.id;
        const player = client.manager?.get(guildId);

        if (!player || player.state !== "CONNECTED") return;

        const stateChange: { type?: string, channel?: VoiceBasedChannel | null, members?: any } = {};

        if (oldState.channel === null && newState.channel !== null)
            stateChange.type = "JOIN";
        if (oldState.channel !== null && newState.channel === null)
            stateChange.type = "LEAVE";
        if (oldState.channel !== null && newState.channel !== null)
            stateChange.type = "MOVE";
        if (oldState.channel === null && newState.channel === null) return;
        if (newState.serverMute == true && oldState.serverMute == false)
            return player.pause(true);
        if (newState.serverMute == false && oldState.serverMute == true)
            return player.pause(false);

        if (stateChange.type === "MOVE") {
            if (oldState.channel?.id === player.voiceChannel) stateChange.type = "LEAVE";
            if (newState.channel?.id === player.voiceChannel) stateChange.type = "JOIN";
        }
        if (stateChange.type === "JOIN") stateChange.channel = newState.channel;
        if (stateChange.type === "LEAVE") stateChange.channel = oldState.channel;

        if (!stateChange.channel || stateChange.channel.id !== player.voiceChannel)
            return;

        stateChange.members = stateChange.channel.members.filter(
            (member) => !member.user.bot
        );

        switch (stateChange.type) {
            case "JOIN":
                if (stateChange.members.size === 1 && player.paused) {
                    player.pause(false);
                }

                if (guildConfig.premium.enabled) {
                    if (!guildConfig._247.enabled) {
                        // eslint-disable-next-line no-case-declarations
                        const Join: any = await Voice.findOne({
                            channelID: newState.channel?.id,
                        })
                        if (Join) {
                            Join.joins += 1;
                            Join.save();
                        } else {
                            await Voice.create({
                                channelID: newState.channel?.id,
                                joins: 1,
                            });
                        }
                    }
                } else {
                    // eslint-disable-next-line no-case-declarations
                    const Join: any = await Voice.findOne({
                        channelID: newState.channel?.id,
                    })
                    if (Join) {
                        Join.joins += 1;
                        Join.save();
                    } else {
                        await Voice.create({
                            channelID: newState.channel?.id,
                            joins: 1,
                        });
                    }
                }
                break;
            case "LEAVE":
                player.destroy()
                break;
        }
    }

    if (oldState.id !== client.user?.id) {
        if (guildConfig.premium.enabled && !guildConfig._247.enabled || !guildConfig._247.enabled) {
            // @ts-ignore
            if (!oldState.guild.members.cache.get(client.user.id).voice.channelId) return

            // @ts-ignore
            if (oldState.guild.members.cache.get(client.user.id).voice.channelId === oldState.channelId) {
                if (oldState.guild.me?.voice?.channel) {
                    await delay(180000)
                    const vcMembers = oldState.guild.me.voice.channel?.members.size
                    if (!vcMembers || vcMembers === 1) {
                        const newPlayer = client.manager?.players.get(newState.guild.id)
                        try {
                            //@ts-ignore
                            newPlayer ? newPlayer.destroy() : oldState.guild.me.voice?.channel?.leave().catch(() => null);
                        } catch (e) {
                            return;
                        }
                    }
                }
            }
        }

        if (oldState.guild.me?.voice?.channel && oldState.guild.me.voice.channel.id == oldState.channelId) {
            const vcMembers = oldState.channel?.members.size
            if (!vcMembers || vcMembers === 1) {
                if (guildConfig.premium.enabled && guildConfig._247.enabled) {
                    const newPlayer = client.manager?.players.get(newState.guild.id)
                    if (newPlayer) newPlayer.pause(true)
                }
            }
        }
    }
}
