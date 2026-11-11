/*
 * File:          DjRole.kt
 * Path:          v3\src\main\kotlin\functions\DjRole.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-22 00:04:01 +01:00
 *
 * SHA-256: 999f3e516b932a434e51d73893a0418753a0871cd39793de2197dc513e7d21e1
 *
 */


package functions

import database.schema.Guild
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel
import player.TrackScheduler

object DjRole {

    fun checkChannel(channel: AudioChannel?, config: Guild, scheduler: TrackScheduler): Boolean {

        if(config.djRoleID == "")
            return false

        var exists = false
        if (channel != null && channel.type == ChannelType.VOICE) {
            channel.members.forEach {
                if (it.user.id == channel.jda.selfUser.id) return@forEach
                if (it.user.isBot) return@forEach

                if(checkRole(it, config, scheduler)) {
                    exists = true
                }
            }
        }
        return exists
    }

    fun checkRole(member:Member, config: Guild, scheduler: TrackScheduler): Boolean {

        if(config.djRoleID == "")
            return false

        val role = member.roles.find { it.id == config.djRoleID }

        return if (role != null) {
            true
        } else {
            if(!scheduler.queue.isEmpty() && scheduler.queue.first() != null) {
                val requester = scheduler.queue.first().userData as Member?
                if(requester != null) {
                    requester.user.id == member.user.id
                } else
                    false
            } else
                false
        }
    }
}