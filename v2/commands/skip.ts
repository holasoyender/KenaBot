/*
 * File:          skip.ts
 * Path:          v2\commands\skip.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-02-11 21:21:38 +01:00
 *
 * SHA-256: ac200b35647378ca7195e4d0a536ae1139a7bfc1aee8cf7c0898e9f044e5a830
 *
 */


import Zeppelin from "../utils/Client";
import { SlashCommandBuilder } from "@discordjs/builders";
import {CommandInteraction, GuildMember, Message, MessageActionRow, MessageButton, MessageEmbed} from "discord.js";
import Default from "../utils/config/default";
import esES from "../langs/es-ES";
import {checkChannel, checkRole} from "../utils/djrole";

module.exports = {
    name: "skip",
    desc: "Salta la canción actual",
    category: "Music",
    permissions: "everyone",
    usage: "",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const player = client.manager?.get(event.guild.id);
        if (!player) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (!player.queue.current) return event.reply({
            content: lang.manager.nothing_playing,
            ephemeral: true
        })

        if (event.guild.me.voice.channel !== event.member.voice.channel) return event.reply({
            content: lang.manager.cant_modify_queue,
            ephemeral: true
        })

        const channel = event.guild.channels.cache.get(`${player.voiceChannel}`)
        if(channel && channel.type == "GUILD_VOICE") {
            if(checkChannel(channel, config, player)) {
                if(!checkRole(event.member, config, player)) {
                    const embed = new MessageEmbed()
                        // @ts-ignore
                        .setColor(config.color)
                        .setDescription(lang.commands.skip_vote.vote.replace("%p%", "0"))
                    await event.reply({ content: lang.commands.skip_vote.announce, ephemeral: true})

                    const msg:Message = await event.channel.send({embeds: [embed], components: [ new MessageActionRow().addComponents([
                        new MessageButton()
                            .setLabel(lang.commands.skip_vote.label)
                            .setStyle("PRIMARY")
                            .setCustomId("cmd:skip_vote:vote")
                        ])]})

                    let users = 0
                    const allowedUsers:Array<string> = []
                    channel.members.forEach(member => {
                        if(member.user.id !== client.user?.id) {
                            users++;
                            allowedUsers.push(member.user.id)
                        }
                    })

                    const collector = msg.createMessageComponentCollector({componentType: 'BUTTON', time: 60000 })

                    let isVoted = false
                    const votes:Array<string> = []
                    collector.on("collect", i => {
                        if(votes.includes(i.user.id)) return i.reply({content: lang.commands.skip_vote.already_voted, ephemeral: true})
                        if(!allowedUsers.includes(i.user.id)) return i.reply({content: lang.commands.skip_vote.cant_vote, ephemeral: true})
                        votes.push(i.user.id)

                        const porcentaje = Math.round((votes.length / users) * 100)
                        const embed = new MessageEmbed()
                            // @ts-ignore
                            .setColor(config.color)
                            .setDescription(lang.commands.skip_vote.vote.replace("%p%", porcentaje.toString()))
                        
                        if(porcentaje >= 50) {
                            isVoted = true
                            const embed = new MessageEmbed()
                                // @ts-ignore
                                .setColor(config.color)
                                .setDescription(lang.commands.skip_vote.completed.replace("%s%", porcentaje.toString()))
                            i.update({embeds: [embed], components: [new MessageActionRow().addComponents([
                                    new MessageButton()
                                        .setLabel(lang.commands.skip_vote.label)
                                        .setStyle("PRIMARY")
                                        .setCustomId("cmd:skip_vote:vote")
                                        .setDisabled(true)
                                ])]})

                            player.setTrackRepeat(false);
                            player.stop();

                            return collector.stop()
                        }

                        i.reply({
                            content: lang.commands.skip_vote.voted,
                            ephemeral: true,
                        })
                        msg.edit({embeds: [embed]})
                    })
                    
                    collector.on("end", () => {
                        if(!isVoted) {
                            const embed = new MessageEmbed()
                                // @ts-ignore
                                .setColor(config.color)
                                .setDescription(lang.commands.skip_vote.canceled)

                            msg.edit({embeds: [embed], components: [new MessageActionRow().addComponents([
                                    new MessageButton()
                                        .setLabel(lang.commands.skip_vote.label)
                                        .setStyle("PRIMARY")
                                        .setCustomId("cmd:skip_vote:vote")
                                        .setDisabled(true)
                                ])]})
                            return;
                        }
                    })
                    
                    return;
                }
            }
        }

        player.setTrackRepeat(false);
        player.stop();

        const embed = new MessageEmbed()
            //@ts-ignore
            .setColor(config.color)
            .setDescription(lang.commands.skip)

        return event.reply({
            embeds: [embed],
            ephemeral: false
        })
    },
    slashInfo: new SlashCommandBuilder()
        .setName("skip")
        .setDescription("Salta la canción actual.")
}
