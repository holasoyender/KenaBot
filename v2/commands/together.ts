/*
 * File:          together.ts
 * Path:          v2\commands\together.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-15 22:39:49 +02:00
 *
 * SHA-256: 7d1d5a3e0621c948c612aac0245739688b3fbb9c329ce39f7f3dbcc3d3358b42
 *
 */


import Zeppelin from '../utils/Client';
import { SlashCommandBuilder } from '@discordjs/builders';
import {
    CommandInteraction,
    GuildMember,
    MessageActionRow,
    MessageButton,
    MessageEmbed,
    MessageSelectMenu,
    VoiceChannel,
} from 'discord.js';
import Default from '../utils/config/default';
import esES from '../langs/es-ES';
import getApp, { apps } from '../utils/together';


module.exports = {
    name: 'together',
    desc: 'Empezar una actividad de Discord Together.',
    category: 'Misc',
    permissions: 'everyone',
    usage: '[actividad]',
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me)
            return event.reply({
                content: lang.manager.cant_do,
                ephemeral: true,
            });

        if (!event.member.voice.channel)
            return event.reply({
                content: ':no_entry_sign:  Debes estar en un canal de voz para poder usar este comando.',
                ephemeral: true,
            });

        const activity = event.options.getString('actividad');
        if (activity) {
            const activityInfo = await getApp(activity, event.member.voice.channel as VoiceChannel);
            if (activityInfo.url === 'error')
                return event.reply({
                    content: ':no_entry_sign:  No se pudo crear una invitación para la actividad.',
                    ephemeral: true,
                });

            const embed = new MessageEmbed()
                //@ts-ignore
                .setColor(config.color)
                .setAuthor({
                    name: 'Aquí está tu invitación para ' + activityInfo.name,
                    iconURL: event.user.displayAvatarURL(),
                })
                .setDescription(
                    `Para acceder a la actividad puedes hacer click [aqui](${activityInfo.url}) o en el botón!`,
                );

            event.reply({
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
        } else {
            const activities: { value: string, description: string, label: string, emoji: string }[] = [];

	    for (const i in Object.values(apps)) {
		    activities.push({
			    value: `cmd:together:${Object.keys(apps)[i]}`,
			    label: Object.values(apps)[i].name,
			    description: `Lanzar una actividad de ${Object.values(apps)[i].name}`,
			    emoji: "997598523828736071"
		    })
	    }
            const embed = new MessageEmbed()
                //@ts-ignore
                .setColor(config.color)
                .setAuthor({
                    name: 'Elige una actividad de Discord Together!',
                    iconURL: event.user.displayAvatarURL(),
                })
                .setDescription('Haz click en el menú para seleccionar la actividad que deseas!');

            event.reply({
                embeds: [embed],
                components: [
                    new MessageActionRow().addComponents([
                        new MessageSelectMenu()
                            .setCustomId('cmd:together')
                            .setMaxValues(1)
                            .setMinValues(0)
                            .setPlaceholder('Selecciona una actividad!')
                            .addOptions(activities),
                    ]),
                ],
            });
        }
    },
    slashInfo: new SlashCommandBuilder()
        .setName('together')
        .setDescription('Empezar una actividad de Discord Together.')
        .addStringOption(option =>
            option
                .setName('actividad')
                .setDescription('Actividad a lanzar')
                .setRequired(false)
                .addChoices(
                    { name: 'YouTube', value: 'youtube' },
                    { name: 'Poker', value: 'poker' },
                    { name: 'Betrayal', value: 'betrayal' },
                    { name: 'Fishing', value: 'fishing' },
                    { name: 'Chess', value: 'chess' },
                    { name: 'LetterTile', value: 'lettertile' },
                    { name: 'WordSnack', value: 'wordsnack' },
                    { name: 'DoodleCrew', value: 'doodlecrew' },
                    { name: 'AwkWord', value: 'awkword' },
                    { name: 'SpellCast', value: 'spellcast' },
                    { name: 'Checkers', value: 'checkers' },
                    { name: 'PuttParty', value: 'puttparty' },
                    { name: 'SketchHeads', value: 'sketchheads' },
                    { name: 'Blazing 8s', value: 'ocho' },
                ),
        ),
};
