/*
 * File:          djrole.ts
 * Path:          v2\utils\djrole.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 21:27:03 +02:00
 *
 * SHA-256: 3b1f5bbf8d994999d844a7179e85d0ddaacce6c06645e557c89d0a17171d9ab1
 *
 */


import Default from "./config/default";
import { GuildMember, VoiceChannel } from "discord.js";
import { Player } from "../functions/manager";

export const checkRole = (member: GuildMember, config: typeof Default, player: Player): boolean => {

    if (!config.dj_role || config.dj_role == "") return true;

    if (member.roles.cache.find(r => r.id === config.dj_role))
        return true;
    else {
        if (player.queue.current) {
            // @ts-ignore
            if (player.queue.current.requester.id) { // @ts-ignore
                return member.id === player.queue.current.requester.id;
            }
            else return false
        } else
            return false;
    }
};

export const checkChannel = (channel: VoiceChannel, config: typeof Default, player: Player): boolean => {

    if (!config.dj_role || config.dj_role == "") return false;

    let existDjOnChannel = false;
    if (channel && channel.type == "GUILD_VOICE") {
        channel.members.forEach(member => {

            if (member.user.id === channel.client.user?.id) return;
            if (checkRole(member, config, player))
                existDjOnChannel = true
        })
    }
    return existDjOnChannel;
}