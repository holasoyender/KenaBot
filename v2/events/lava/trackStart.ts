/*
 * File:          trackStart.ts
 * Path:          v2\events\lava\trackStart.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:02:10 +02:00
 *
 * SHA-256: 8513456988b852503ef0be32b6a091622c0ea243fe47470fb5e4541b8a0d4be9
 *
 */


import Zeppelin from '../../utils/Client';
import Guild from '../../database/schema/Guild';
import Default from '../../utils/config/default';
import { Message, MessageActionRow, MessageButton, MessageEmbed } from 'discord.js';
import { stringFormat } from '../../utils/stringFormat';
import Songs from '../../database/schema/Songs';
import { convertTime } from '../../utils/convert';
import esES from '../../langs/es-ES';
import { getTrackByURL } from '../../plugins/spotify/api/api';
import Regions from '../../database/schema/Regions';

exports.run = async (client: Zeppelin, player: any, track: any) => {
    if (player.msg) {
        try {
            const message: Message = player.msg;
            await message.edit({ embeds: message.embeds, components: [] });
        } catch (e) {
            return;
        }
    }

    if (player.voiceState?.event?.endpoint) {
        const endpoint:any = await Regions.findOne({ region: player.voiceState.event.endpoint });
        if (endpoint) {
            endpoint.count++;
            endpoint.save();
        } else {
            const newEndpoint = new Regions({ region: player.voiceState.event.endpoint, count: 1 });
            newEndpoint.save();
        }
    }

    const song: any = await Songs.findOne({ url: track.uri });
    if (song) {
        if (isNaN(song.plays)) song.plays = 0;
        song.plays += 1;
        song.save();
    } else {
        await Songs.create({
            url: track.uri,
            plays: 1,
        });
    }
    const channel: any = client.channels.cache.get(player.textChannel);

    let guild: any = await Guild.findOne({ guildID: player.guild });
    if (!guild) guild = Default;
    const lang = client.langs?.[guild.lang] ?? esES;

    if (!player.metadata) player.metadata = [];

    if (player.metadata?.length == 0 && player.queue.length !== 0) {
        player.metadata.unshift(null);
        for (let i = 0; i < player.queue.length; i++) {
            player.metadata.unshift(null);
        }
    }
    /**
     * Input operations "null" to fill metadata mismatch of queue ^^
     * Added by toshr1
     */

    let spInfo;
    if (player.metadata && /^https?:\/\/(open.spotify\.com)\/(.*)$/.test(player.metadata[0]))
        spInfo = await getTrackByURL(player.metadata[0]);

    const metadata = {
        url: player.metadata[0] ?? '',
        title: spInfo?.name ?? track.title,
        author: spInfo?.artists?.map((a: any) => a.name) ?? track.author,
        image:
            spInfo?.album?.images[0]?.url ??
            'https://cdn.discordapp.com/attachments/755000173922615336/955506132372291664/no_image.png',
        duration: spInfo?.duration_ms ?? track.duration,
    };

    const plur = player.queue.length == 1 ? lang.commands.play.song : lang.commands.play.songs;
    const embed = new MessageEmbed()
        //@ts-ignore
        .setColor(guild.color)
        .setAuthor({
            name: lang.events.trackStart.embed_author,
            iconURL:
                track.requester.user?.avatarURL() ||
                'https://cdn.discordapp.com/attachments/755000173922615336/955506132372291664/no_image.png',
        })
        .setThumbnail(metadata.image)
        .setDescription(
            `**[${stringFormat(metadata.title)}](${metadata.url})** from \`${metadata.author
                ?.toString()
                .replace(/,/g, ', ')}\``,
        )
        .addField(lang.commands.play.duration, `\`${convertTime(metadata.duration)}\``, true)
        .addField(lang.commands.play.in_queue, `\`${player.queue.length}\` ${plur}`, true);

    const btn = new MessageActionRow().addComponents([
        new MessageButton()
            .setStyle('SUCCESS')
            .setCustomId('cmd:play:pause:' + channel?.guild.id)
            .setLabel(lang.commands.play.pause_resume),
        new MessageButton()
            .setStyle('PRIMARY')
            .setCustomId('cmd:play:skip:' + channel?.guild.id)
            .setLabel('Skip'),
        new MessageButton()
            .setStyle('DANGER')
            .setCustomId('cmd:play:stop:' + channel?.guild.id)
            .setLabel(lang.commands.play.stop),
        new MessageButton().setStyle('SECONDARY').setCustomId('cmd:play:showQueue').setLabel('Queue'),
    ]);
    try {
        player.msg = await channel?.send({ embeds: [embed], components: [btn] });
    } catch (e) {
        return;
    }
};
