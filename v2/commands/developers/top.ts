/*
 * File:          top.ts
 * Path:          v2\commands\developers\top.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-18 15:19:34 +02:00
 *
 * SHA-256: ac3781240845504162c3b10bd6e6fc7cf9aaa7ca776014ba684652d0b504f6c8
 *
 */


import Zeppelin from '../../utils/Client';
import { Message } from 'discord.js';

module.exports = {
    name: 'top',
    desc: 'Servidores más grandes del bot.',
    category: 'Developers',
    permissions: 'Developers',
    run: async (client: Zeppelin, message: Message) => {
        const all_servers = client.guilds.cache.map(server => {
            return {
                name: server.name,
                id: server.id,
                members: server.memberCount,
            };
        });

        const sorted_servers = all_servers.sort((a, b) => {
            return b.members - a.members;
        });

        const ordered_servers = sorted_servers.map(
            (server, index) => `${index + 1}) ${server.name} [${server.id}] - ${server.members} miembros`,
        );

        // take only the first 10
        const top_servers = ordered_servers.slice(0, 10);

        let content = `\`\`\`js\n Servidores más grandes de ${client.user?.username}\n\n${top_servers.join(
            '\n',
        )}\`\`\``;

        if (content.length > 2000) content = content.slice(0, 2000);

        await message.reply({
            content,
	    allowedMentions: { repliedUser: false },
        });
    },
};
