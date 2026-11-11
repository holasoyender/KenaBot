/*
 * File:          interactionCreate.ts
 * Path:          v2\events\interactionCreate.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-08-13 01:12:50 +02:00
 *
 * SHA-256: 96b6ed6976abdbab26f8fd682ea5d87f3190051f05166a16a9cc746a1f1471c7
 *
 */


/* eslint-disable no-case-declarations */
import Zeppelin from '../utils/Client';
import { Interaction, MessageActionRow, VoiceChannel, MessageButton, MessageEmbed, GuildMember } from 'discord.js';
import Guild from '../database/schema/Guild';
import Default from '../utils/config/default';
import esES from '../langs/es-ES';
import chunk, { inArray } from '../utils/chunk';
import { stringFormat } from '../utils/stringFormat';
import { convertTime } from '../utils/convert';
import { Player } from '../functions/manager';
import Commands from '../database/schema/Commands';
import handleConfig from '../handlers/config';
import handleLang from '../handlers/lang';
import handleDisabled from '../handlers/channels';
import handleEmbed from '../handlers/embed';
import handleDisabledCommands from '../handlers/commands';
import handleVoiceChannels from '../handlers/voice';
import handleDJRole from '../handlers/djrole';
import { checkChannel, checkRole } from '../utils/djrole';
import Backup from '../database/schema/Backup';
import getApp from '../utils/together';

exports.run = async (client: Zeppelin, interaction: Interaction) => {
    if (interaction.user.bot) return;
    //@ts-ignore
    // if (interaction.channel.type == "DM") return interaction.reply({
    //     ephemeral: true,
    //     content: "No puedo usar comandos en canales que no sean de texto!\n`Esto se arreglará en una futura update`",
    // });

    if (interaction.isCommand()) {
        const db = await Guild.findOne({
            guildID: interaction.guild?.id,
        });

        let guildConfig:any = db;
        if (!db) guildConfig = Default;

        if (!guildConfig.lang) guildConfig.lang = 'es-ES';
        const guildLang = client.langs?.[guildConfig.lang] ?? esES;

        if (guildConfig.blocked.enabled)
            if (!inArray(client.develop.owners, interaction.user.id))
                return interaction.reply({
                    content: guildLang.manager.blocked_server.replace('%s%', guildConfig.blocked.reason),
                    ephemeral: true,
                });

        if (client.Commands.find(cmd => cmd.name.toLowerCase() === interaction.commandName.toLowerCase())) {
            if (inArray(guildConfig.disabled.channels, `${interaction.channel?.id}`)) {
                // @ts-ignore
                if (!interaction.member?.permissions.has('MANAGE_GUILD')) {
                    if (!inArray(client.develop.owners, interaction.user.id))
                        return await interaction.reply({
                            embeds: [
                                new MessageEmbed()
                                    // @ts-ignore
                                    .setColor(guildConfig.color)
                                    .setDescription(guildLang.manager.blocked),
                            ],
                            ephemeral: true,
                        });
                }
            }

            if (inArray(guildConfig.disabled.commands, `${interaction.commandName.toLowerCase()}`)) {
                // @ts-ignore
                if (!interaction.member?.permissions.has('MANAGE_GUILD')) {
                    if (!inArray(client.develop.owners, interaction.user.id))
                        return await interaction.reply({
                            embeds: [
                                new MessageEmbed()
                                    // @ts-ignore
                                    .setColor(guildConfig.color)
                                    .setDescription(guildLang.manager.blocked_command),
                            ],
                            ephemeral: true,
                        });
                }
            }

            const cmdStats: any = await Commands.findOne({
                command: interaction.commandName.toLowerCase(),
            });
            if (cmdStats) {
                cmdStats.uses += 1;
                cmdStats.save();
            } else {
                await Commands.create({
                    command: interaction.commandName.toLowerCase(),
                    uses: 1,
                });
            }

            try {
                client.Commands.find(cmd => cmd.name.toLowerCase() === interaction.commandName.toLocaleLowerCase()).run(
                    client,
                    interaction,
                    guildConfig,
                    guildLang,
                );
            } catch (e: unknown) {
                await interaction.reply({ content: 'Ha ocurrido un error al ejecutar el comando!', ephemeral: true });
                client.Logger.error('Error: ' + e);
            }
        } else {
            return await interaction.reply({
                content: guildLang.events.interactionCreate.unknown_command,
                ephemeral: true,
            });
        }
    }
    if (interaction.isButton()) {
        const args = interaction.customId.split(':');

        if (args[0] == 'cmd') {
            const db = await Guild.findOne({
                guildID: interaction.guild?.id,
            });

            let guildConfig:any = db;
            if (!db) guildConfig = Default;

            if (!guildConfig.lang) guildConfig.lang = 'es-ES';

            const guildLang = client?.langs?.[guildConfig.lang] ?? esES;

            if (guildConfig.blocked.enabled)
                if (!inArray(client.develop.owners, interaction.user.id))
                    return interaction.reply({
                        content: guildLang.manager.blocked_server.replace('%s%', guildConfig.blocked.reason),
                        ephemeral: true,
                    });

            switch (args[1]) {
                case 'queue': {
                    if (!interaction.guild) return;

                    const player = client.manager?.get(interaction.guild.id);
                    if (!player)
                        return interaction.reply({
                            content: guildLang.manager.nothing_playing,
                            ephemeral: true,
                        });

                    const queue = player.queue;

                    if (!queue)
                        return interaction.reply({
                            content: guildLang.manager.nothing_playing,
                            ephemeral: true,
                        });

                    const chunkQueue: Array<typeof queue> = chunk(queue, 5);

                    const userID = args[4];
                    let page: number = parseInt(args[2]);
                    const action = args[3];

                    if (userID !== interaction.user.id)
                        return interaction.reply({
                            content: guildLang.events.interactionCreate.cant_use_button,
                            ephemeral: true,
                        });

                    if (action == 'back') {
                        page -= 1;
                        if (page < 1)
                            return interaction.reply({
                                content: guildLang.commands.queue.no_previous,
                                ephemeral: true,
                            });
                    }
                    if (action == 'next') {
                        page += 1;
                        if (page > chunkQueue.length)
                            return interaction.reply({
                                content: guildLang.commands.queue.no_next,
                                ephemeral: true,
                            });
                    }

                    const embed = new MessageEmbed()
                        //@ts-ignore
                        .setColor(guildConfig.color)
                        .setFooter({
                            text: guildLang.commands.queue.embed_footer
                                .replace('%s%', page + '')
                                .replace('%d%', chunkQueue.length.toString())
                                .replace('%f%', queue.length + 1 + ''),
                        })
                        .setAuthor({
                            name: guildLang.commands.queue.embed_author,
                            iconURL: client.user?.displayAvatarURL(),
                        });

                    if (queue.current) {
                        embed.setDescription(
                            `\`\`\`yml\n --- ${guildLang.commands.queue.now_playing} ---\n${stringFormat(
                                queue.current.title,
                            )}\`\`\``,
                        );
                    }

                    if (page - 1 < 0) page = 1;

                    let i = (page - 1) * 5;
                    for (const track of chunkQueue[page - 1]) {
                        i++;
                        embed.addField(
                            `${i}. ${stringFormat(track.title)}`,
                            `**Autor:** ${track.author}\n**Duración**: ${convertTime(track.duration ?? 0)}`,
                        );
                    }

                    const row = new MessageActionRow().addComponents([
                        new MessageButton()
                            .setStyle('PRIMARY')
                            .setLabel('◀')
                            .setCustomId(`cmd:queue:${page}:back:${interaction.user.id}`),
                        new MessageButton()
                            .setStyle('PRIMARY')
                            .setLabel('▶')
                            .setCustomId(`cmd:queue:${page}:next:${interaction.user.id}`),
                    ]);

                    await interaction.update({ embeds: [embed], components: [row] });
                    break;
                }
                case 'play': {
                    if (!interaction.guild) return;

                    if (args[2] == 'showQueue') {
                        const player = client.manager?.get(interaction.guild.id);
                        if (!player)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        const queue = player.queue;

                        if (!queue)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        let chunkQueue: Array<typeof queue> = chunk(queue, 5);

                        if (queue.length == 0) chunkQueue = [queue];
                        const embed = new MessageEmbed()
                            //@ts-ignore
                            .setColor(guildConfig.color)
                            .setFooter({
                                text: guildLang.commands.queue.embed_footer
                                    .replace('%s%', '1')
                                    .replace('%d%', chunkQueue.length.toString())
                                    .replace('%f%', queue.length + 1 + ''),
                            })
                            .setAuthor({
                                name: guildLang.commands.queue.embed_author,
                                iconURL: client.user?.displayAvatarURL(),
                            });

                        if (queue.current) {
                            embed.setDescription(
                                `\`\`\`yml\n --- ${guildLang.commands.queue.now_playing} ---\n${stringFormat(
                                    queue.current.title,
                                )}\`\`\``,
                            );
                        }

                        let i = 0;
                        for (const track of chunkQueue[0]) {
                            i++;
                            embed.addField(
                                `${i}. ${stringFormat(track.title)}`,
                                `**Autor:** ${track.author}\n**Duración**: ${convertTime(track.duration ?? 0)}`,
                            );
                        }

                        const row = new MessageActionRow().addComponents([
                            new MessageButton()
                                .setStyle('PRIMARY')
                                .setLabel('◀')
                                .setCustomId('cmd:queue:1:back:' + interaction.user.id),
                            new MessageButton()
                                .setStyle('PRIMARY')
                                .setLabel('▶')
                                .setCustomId('cmd:queue:1:next:' + interaction.user.id),
                        ]);

                        await interaction.reply({
                            embeds: [embed],
                            components: [row],
                            ephemeral: true,
                        });
                    }
                    if (args[2] == 'pause') {
                        const player = client.manager?.get(interaction.guild.id);
                        if (!player)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        if (!player.queue.current)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        // @ts-ignore
                        if (interaction.guild.me?.voice.channel !== interaction.member?.voice.channel)
                            return interaction.reply({
                                content: guildLang.manager.cant_modify_queue,
                                ephemeral: true,
                            });

                        const channel = interaction.guild.channels.cache.get(`${player.voiceChannel}`);
                        if (channel && channel.type == 'GUILD_VOICE') {
                            if (checkChannel(channel, guildConfig, player)) {
                                // @ts-ignore
                                if (!checkRole(interaction.member, guildConfig, player)) {
                                    const embed = new MessageEmbed()
                                        // @ts-ignore
                                        .setColor(guildConfig.color)
                                        .setDescription(guildLang.manager.dj_role_required);
                                    return interaction.reply({ embeds: [embed], ephemeral: true });
                                }
                            }
                        }

                        if (player.paused) {
                            player.pause(false);

                            const embed = new MessageEmbed()
                                //@ts-ignore
                                .setColor(guildConfig.color)
                                .setDescription(guildLang.commands.pause.resumed);

                            return interaction.reply({
                                embeds: [embed],
                                ephemeral: true,
                            });
                        } else {
                            player.pause(true);
                            const embed = new MessageEmbed()
                                //@ts-ignore
                                .setColor(guildConfig.color)
                                .setDescription(guildLang.commands.pause.paused);

                            return interaction.reply({
                                embeds: [embed],
                                ephemeral: true,
                            });
                        }
                    }
                    if (args[2] == 'stop') {
                        const player = client.manager?.get(interaction.guild.id);
                        if (!player)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        if (!player.queue.current)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        // @ts-ignore
                        if (interaction.guild.me?.voice.channel !== interaction.member?.voice.channel)
                            return interaction.reply({
                                content: guildLang.manager.cant_modify_queue,
                                ephemeral: true,
                            });

                        const channel = interaction.guild.channels.cache.get(`${player.voiceChannel}`);
                        if (channel && channel.type == 'GUILD_VOICE') {
                            if (checkChannel(channel, guildConfig, player)) {
                                // @ts-ignore
                                if (!checkRole(interaction.member, guildConfig, player)) {
                                    const embed = new MessageEmbed()
                                        // @ts-ignore
                                        .setColor(guildConfig.color)
                                        .setDescription(guildLang.manager.dj_role_required);
                                    return interaction.reply({ embeds: [embed], ephemeral: true });
                                }
                            }
                        }

                        player.setTrackRepeat(false);

                        player.stop();
                        player.queue.clear();

                        let isLeave = false;

                        if (guildConfig.premium.enabled) {
                            if (!guildConfig._247.enabled) {
                                player.destroy();
                                isLeave = true;
                            }
                        } else {
                            player.destroy();
                            isLeave = true;
                        }

                        const embed = new MessageEmbed()
                            //@ts-ignore
                            .setColor(guildConfig.color);
                        isLeave
                            ? embed.setDescription(guildLang.commands.stop_leave)
                            : embed.setDescription(guildLang.commands.stop);

                        return interaction.reply({
                            embeds: [embed],
                            ephemeral: true,
                        });
                    }
                    if (args[2] == 'skip') {
                        const player = client.manager?.get(interaction.guild.id);
                        if (!player)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        if (!player.queue.current)
                            return interaction.reply({
                                content: guildLang.manager.nothing_playing,
                                ephemeral: true,
                            });

                        // @ts-ignore
                        if (interaction.guild.me?.voice.channel !== interaction.member?.voice.channel)
                            return interaction.reply({
                                content: guildLang.manager.cant_modify_queue,
                                ephemeral: true,
                            });

                        const channel = interaction.guild.channels.cache.get(`${player.voiceChannel}`);
                        if (channel && channel.type == 'GUILD_VOICE') {
                            if (checkChannel(channel, guildConfig, player)) {
                                // @ts-ignore
                                if (!checkRole(interaction.member, guildConfig, player)) {
                                    const embed = new MessageEmbed()
                                        // @ts-ignore
                                        .setColor(guildConfig.color)
                                        .setDescription(guildLang.manager.dj_role_required_usecmd);
                                    return interaction.reply({ embeds: [embed], ephemeral: true });
                                }
                            }
                        }

                        player.stop();

                        const s_embed = new MessageEmbed()
                            //@ts-ignore
                            .setColor(guildConfig.color)
                            .setDescription(guildLang.commands.skip);

                        return interaction.reply({
                            embeds: [s_embed],
                            ephemeral: true,
                        });
                    }
                    break;
                }
                case 'dev': {
                    if (args[2] == 'restart') {
                        await interaction.update({
                            components: [],
                            embeds: [
                                new MessageEmbed()
                                    .setAuthor({ name: 'Bot reiniciado', iconURL: client.user?.displayAvatarURL() })
                                    .setColor('#5865F2')
                                    .setDescription(
                                        'El bot ha mandado un mensaje a todos los nodos para el reinicio\nSe han desconectado `' +
                                            client.manager?.players.size +
                                            '` nodos',
                                    ),
                            ],
                        });
                        if (!client.manager?.players || client.manager.players.size == 0) return;

                        // eslint-disable-next-line no-unsafe-optional-chaining
                        for (const _player of client.manager.players) {
                            const player: Player = _player[1];
                            if (player && player.textChannel) {
                                if (!player.playing) return;

                                const channel: any = client.channels.cache.get(player.textChannel);
                                await Backup.create({
                                    guildID: player.guild,
                                    textChannelID: player.textChannel,
                                    voiceChannelID: player.voiceChannel,
                                    queue: player.queue,
                                    current: player.queue.current,
                                    currentMetadata: (player as any).metadata[0],
                                    volume: player.volume,
                                    playing: player.playing,
                                    paused: player.paused,
                                    loop: player.trackRepeat,
                                    position: player.position,
                                });
                                if (channel) {
                                    const db = await Guild.findOne({
                                        guildID: interaction.guild?.id,
                                    });

                                    let guildConfig:any = db;
                                    if (!db) guildConfig = Default;

                                    if (!guildConfig.lang) guildConfig.lang = 'es-ES';

                                    const guildLang = client.langs?.[guildConfig.lang] ?? esES;

                                    const $embed = new MessageEmbed()
                                        // @ts-ignore
                                        .setColor(guildConfig.color)
                                        .setDescription(guildLang.manager.restart.embed_description)
                                        .setAuthor({
                                            name: guildLang.manager.restart.embed_author,
                                            iconURL: client.user?.displayAvatarURL(),
                                        });

                                    try {
                                        player.stop();
                                        player.queue.clear();
                                        if (!guildConfig._247.enabled) {
                                            player.destroy();

                                            channel
                                                .send({
                                                    embeds: [$embed],
                                                })
                                                .catch(() => {
                                                    console.log(
                                                        `Error al enviar el mensaje de reinicio en el servidor [${player.guild}]`,
                                                    );
                                                });
                                        }
                                    } catch (e) {
                                        return;
                                    }
                                }
                            }
                        }
                    }
                    if (args[2] == 'cancel') {
                        await interaction.update({
                            content: 'Operación cancelada',
                            embeds: [],
                            components: [],
                        });
                    }
                    break;
                }

                case 'error': {
                    await interaction.reply({
                        content: 'Tu error ha sido reportado! Muchas gracias y lamentamos las molestias',
                        ephemeral: true,
                    });
                    const info = args.slice(2).join(' ');

                    await client.error_webhook.send({
                        content: `Se ha reportado un posible error en el comando \`play\`: \n\`\`\`Servidor: ${interaction.guild?.id}\nUsuario: ${interaction.user.id}\nCanción: ${info}\`\`\``,
                    });
                }
            }

            if (args[1] === 'config') {
                if (args[3] !== interaction.user.id) {
                    const embed = new MessageEmbed()
                        //@ts-ignore
                        .setColor(guildConfig.color)
                        .setDescription(guildLang.commands.config.cant_use);

                    return interaction.reply({ embeds: [embed], ephemeral: true });
                }

                await handleConfig(interaction);
            }
        }
    }

    if (interaction.isSelectMenu()) {
        const db = await Guild.findOne({
            guildID: interaction.guild?.id,
        });

        let guildConfig:any = db;
        if (!db) guildConfig = Default;

        if (!guildConfig.lang) guildConfig.lang = 'es-ES';

        const guildLang = client?.langs?.[guildConfig.lang] ?? esES;

        if (guildConfig.blocked.enabled)
            if (!inArray(client.develop.owners, interaction.user.id))
                return interaction.reply({
                    content: guildLang.manager.blocked_server.replace('%s%', guildConfig.blocked.reason),
                    ephemeral: true,
                });

        if (interaction.values[0]) {
            const args = interaction.values[0].split(':');

            if (args[0] === 'cmd') {
                switch (args[1]) {
                    case 'config':
                        if (args[3] !== interaction.user.id && args[4] !== interaction.user.id) {
                            const embed = new MessageEmbed()
                                //@ts-ignore
                                .setColor(guildConfig.color)
                                .setDescription(guildLang.commands.config.cant_use);

                            return interaction.reply({ embeds: [embed], ephemeral: true });
                        }

                        switch (args[2]) {
                            case 'setlang':
                                await handleLang(interaction, guildConfig, guildLang);
                                break;
                            case 'setdj':
                                await handleDJRole(interaction, guildConfig, guildLang);
                                break;
                            case 'setdchannel':
                                await handleDisabled(interaction, guildConfig, guildLang);
                                break;

                            case 'setcolor':
                                await handleEmbed(interaction, guildConfig, guildLang);
                                break;

                            case 'disablecommand':
                                await handleDisabledCommands(interaction, guildConfig, guildLang);
                                break;
                            case 'setvoicechannel':
                                await handleVoiceChannels(interaction, guildConfig, guildLang);
                                break;
                        }
                        break;
                    case 'together':
                        if (!args[2])
                            return interaction.reply({
                                content: 'No he podido reconocer el comando',
                                ephemeral: true,
                            });

                        if (!(interaction.member as GuildMember).voice.channel)
                            return interaction.reply({
                                content: 'Debes estar en un canal de voz para usar este comando',
                                ephemeral: true,
                            });

                        const activityInfo = await getApp(
                            args[2],
                            (interaction.member as GuildMember | null)?.voice.channel as VoiceChannel,
                        );
                        if (activityInfo.url === 'error')
                            return interaction.reply({
                                content: ':no_entry_sign:  No se pudo crear una invitación para la actividad.',
                                ephemeral: true,
                            });

                        const embed = new MessageEmbed()
                            //@ts-ignore
                            .setColor(guildConfig.color)
                            .setAuthor({
                                name: 'Aquí está tu invitación para ' + activityInfo.name,
                                iconURL: interaction.user.displayAvatarURL(),
                            })
                            .setDescription(
                                `Para acceder a la actividad puedes hacer click [aqui](${activityInfo.url}) o en el botón!`,
                            );

                        interaction.update({
                            embeds: [embed],
                            components: [
                                new MessageActionRow().addComponents([
                                    new MessageButton()
                                        .setURL(activityInfo.url)
                                        .setLabel('Ir a la actividad')
                                        .setEmoji('🚀')
                                        .setStyle('LINK'),
                                ]),
                            ],
                        });
                        break;
                }
            }
        } else {
            const args = interaction.customId.split(':');

            if (args[3] !== interaction.user.id && args[4] !== interaction.user.id) {
                const embed = new MessageEmbed()
                    //@ts-ignore
                    .setColor(guildConfig.color)
                    .setDescription(guildLang.commands.config.cant_use);

                return interaction.reply({ embeds: [embed], ephemeral: true });
            }
            await handleDisabled(interaction, guildConfig, guildLang);
            await handleDisabledCommands(interaction, guildConfig, guildLang);
            await handleVoiceChannels(interaction, guildConfig, guildLang);
            await handleDJRole(interaction, guildConfig, guildLang);
        }
    }
};
