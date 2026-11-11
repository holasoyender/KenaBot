/*
 * File:          ready.ts
 * Path:          v2\events\ready.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-22 12:10:22 +02:00
 *
 * SHA-256: 0a9a33fdd8262a9697574c70ebd325f6338b87b090f8f331d38061c70b34d8a2
 *
 */


import Zeppelin from '../utils/Client';
import fs from 'fs';
import { setLastState } from '../utils/State';
import check247 from '../utils/checks/check247';
import { MessageEmbed } from 'discord.js';
import checkExpiredPremium from '../utils/checks/checkExpiredPremium';
import Backup from '../database/schema/Backup';
import delay from 'delay';
import { authenticate } from '../plugins/spotify/api/api';

exports.run = async (client: Zeppelin) => {
    client.Logger.start(`Cliente iniciado como ${client.user?.tag}`);

    setLastState('ready');
    authenticate(client);

    const embed = new MessageEmbed()
        .setColor(0x5865f2)
        .setAuthor({ name: `Bot reiniciado`, iconURL: `${client.user?.avatarURL()}` })
        .setFooter({ text: `Bot: ${client.user?.username}` })
        .setTimestamp();
    if (client.user?.username !== 'KenaBot Beta') await client.webhook.send({ embeds: [embed] });

    await client.user?.setPresence({ status: 'online', activities: [{ name: 'kenabot.xyz', type: 'WATCHING' }] });

    client.manager?.init(client.user?.id);

    const commands: Array<any> = [];

    let str = './';
    if (client.env == 'prod') {
        str = './dist/';
    }

    await fs.readdir(str + 'commands', async (err, files) => {
        if (err) return client.Logger.error('Error al encontrar la carpeta de comandos: ' + err);
        await files.forEach(file => {
            if (file.endsWith('.js') || file.endsWith('.ts')) {
                // eslint-disable-next-line @typescript-eslint/no-var-requires
                const command = require(`../commands/${file}`);
                commands.push(command.slashInfo.toJSON());
            }
        });

        try {
            await client.application?.commands.set(commands);
            client.Logger.start(`${commands.length} comandos cargados`);
        } catch (error) {
            console.log(error);
            return;
        }
    });
/*
    const allBackups = await Backup.find();
    if (allBackups.length > 0) {
        await delay(1000);
        for (const backup of allBackups) {
            const guild = client.guilds.cache.get(backup.guildID);
	    if (!guild) {
		    console.log(`Guild ${backup.guildID} no encontrada`);
		    continue;
	    };

            backup.delete();
            const player = client.manager?.create({
                guild: backup.guildID,
                voiceChannel: backup.voiceChannelID,
                selfDeafen: true,
                textChannel: backup.textChannelID,
                volume: backup.volume,
            });
            if (!player) continue;
            (player as any).metadata = [];
            player.queue.add(backup.current);
            (player as any).metadata.push(backup.currentMetadata);
            for (const song of backup.queue) {
                const res = await player.search(song.uri, null);
                if (res && res.loadType == 'TRACK_LOADED') {
                    player.queue.add(res.tracks[0]);
                    (player as any).metadata.push(res.tracks[0].uri);
                } else {
                    continue;
                }
            }
            player.setTrackRepeat(backup.loop);

            player.connect();
            player.play();
            player.seek(backup.position);
        }
    }*/
    /*setInterval(() => {
        client.emit("garbageCollector");
    }, 1000 * 60 * 60 * 5)*/

    setInterval(async () => {
        await check247(client);
        await checkExpiredPremium(client);

        let nodes = 0;
        client.manager?.nodes.map((node: any) => (nodes += node.stats.playingPlayers));

        const mem = process.memoryUsage().rss / 1024 / 1024;
        //@ts-ignore
        if (mem.toFixed(2) > 800) {
            //@ts-ignore
            if (nodes > 0 && mem.toFixed(2) > 510) return;
            const pos = process.env.PM2_POSITION || 0;

            client.emit('memoryLeak', mem.toFixed(2), pos);
        }
    }, 30000);
};
