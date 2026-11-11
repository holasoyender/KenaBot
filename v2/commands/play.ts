/*
 * File:          play.ts
 * Path:          v2\commands\play.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 01:16:01 +02:00
 *
 * SHA-256: 4ecc6bde16e7c64aa8ae37a609eeb725fb1188802f869286dddff948f6220c62
 *
 */


import Zeppelin from "../utils/Client";
import esES from "../langs/es-ES";
import Default from "../utils/config/default";
import { SlashCommandBuilder } from "@discordjs/builders";
import { CommandInteraction, GuildMember, MessageActionRow, MessageButton, MessageEmbed } from "discord.js";
import { convertTime } from "../utils/convert";
import { stringFormat } from "../utils/stringFormat";
import searchTrack from "../plugins/spotify/search";
import { inArray } from "../utils/chunk";
import delay from "delay";
import { authenticate } from "../plugins/spotify/api/api";

module.exports = {
    name: "play",
    desc: "Reproduce una canción en el canal de voz!",
    category: "Music",
    permissions: "everyone",
    usage: "<Canción>",
    run: async (client: Zeppelin, event: CommandInteraction, config: typeof Default, lang: typeof esES) => {
        if (!event.guild || !(event.member instanceof GuildMember) || !event.channel || !event.guild.me) return event.reply({
            content: lang.manager.cant_do,
            ephemeral: true
        });

        const { channel } = event.member.voice;
        let player: any = client.manager?.get(event.guild.id);

        if (!channel) {
            await event.reply({ content: lang.commands.play.no_vc, ephemeral: true });
            return;
        }

        if (!channel.permissionsFor(event.guild.me).has("CONNECT") || !channel.permissionsFor(event.guild.me).has("SPEAK")) {
            return event.reply({ content: lang.commands.play.no_vc_perms, ephemeral: true });
        }

        if (inArray(config.disabled.voice, channel.id)) {
            if (!event.member.permissions.has("MANAGE_GUILD")) {
                return event.reply({ content: lang.commands.play.vc_disabled, ephemeral: true });
            }
        }
        let search = event.options.getString("canción");
        if (!search) return event.reply({
            content: lang.commands.play.no_song,
            ephemeral: true
        })

        if (/^(https?:\/\/)?((www\.)?twitch\.tv)\/(.*)$/.test(search) && !config.premium.enabled) {
            return event.reply({
                content: lang.manager.no_premium_twitch,
                ephemeral: true
            });
        }

        if (channel.type == "GUILD_STAGE_VOICE" && !config.premium.enabled) {
            return event.reply({
                content: lang.manager.no_premium_stage,
                ephemeral: true
            });
        }

        if (player) {
            //  SKT-06-2022-660292 Fix: Cuando el player es demasiado antiguo, se destruye y se crea uno nuevo.
            if (player.createdAt < new Date(Date.now() - (1000 * 60 * 60 * 5)) && !player.playing && player.queue.length == 0) {
                player.destroy();
                await delay(500);
                player = client.manager?.create({
                    guild: event.guild.id,
                    voiceChannel: channel.id,
                    textChannel: event.channel.id,
                    volume: 50,
                    selfDeafen: true,
                });
            } else {
                if (player.state !== "CONNECTED") {
                    player.destroy();
                    player = client.manager?.create({
                        guild: event.guild.id,
                        voiceChannel: channel.id,
                        textChannel: event.channel.id,
                        volume: 50,
                        selfDeafen: true,
                    });
                } else {
                    if (!event.guild.me.voice.channel) {
                        player.destroy();
                        player = client.manager?.create({
                            guild: event.guild.id,
                            voiceChannel: channel.id,
                            textChannel: event.channel.id,
                            volume: 50,
                            selfDeafen: true,
                        });
                    }
                    if (event.guild.me.voice.channel && event.member.voice.channel !== event.guild.me.voice.channel) {
                        return event.reply({ content: lang.commands.play.different_vc, ephemeral: true })
                    }
                }
            }
        } else {
            player = client.manager?.create({
                guild: event.guild.id,
                voiceChannel: channel.id,
                textChannel: event.channel.id,
                volume: 50,
                selfDeafen: true,
            });
        }

        if (!player) return event.reply({ content: lang.commands.play.no_player, ephemeral: true });

        player.set("autoplay", false);
        player.setTextChannel(event.channel.id);

        let res;

        if (/^((?:https?:)?\/\/)?((?:www|m)\.)?(youtube\.com|youtu.be)(\/(?:[\w-]+\?v=|embed\/|v\/)?)([\w-]+)(\S+)?$/.test(search)) return event.reply({
            content: lang.commands.play.youtube_link,
            ephemeral: true
        })

        if (player.state !== "CONNECTED") {
            try {
                player.connect();
            } catch (e) {
                return event.reply({
                    content: lang.commands.play.no_vc_perms,
                    ephemeral: true
                })
            }
        }

        if (
            !/^https?:\/\/(open.spotify\.com)\/(.*)$/.test(search) &&
            !/^https?:\/\/(soundcloud\.com|snd\.sc)\/(.*)$/.test(search) &&
            !/^(https?:\/\/)?((www\.)?twitch\.tv)\/(.*)$/.test(search) &&
            !/^(https?:\/\/)?((www\.)?deezer\.com)\/(.*)$/.test(search) &&
            !/https:\/\/music\.apple\.com\/(?:.+)?(artist|album|music-video|playlist)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)/i.test(search)
        ) {
            const algo = await searchTrack(search)
            if (!algo) return event.reply({
                content: lang.commands.play.no_song_found,
                ephemeral: true
            })
            if (!algo.tracks) return event.reply({
                content: lang.commands.play.no_song_found,
                ephemeral: true
            })
            if (!algo.tracks.items[0]) return event.reply({
                content: lang.commands.play.no_song_found,
                ephemeral: true
            })

            search = algo.tracks.items[0].external_urls.spotify;
        }

        try {
            res = await player.search(search, event.member);
            if (res.loadType === 'LOAD_FAILED') {

                switch (res.exception.message) {
                    case "The track name was not provided":
                    case "The Spotify track object was not provided":
                        await event.reply({
                            content: "No puedo reproducir playlists de Spotify privadas!",
                            ephemeral: true
                        })
                        break;

                    case "Unknown file format.":
                        await event.reply({
                            content: "No puedo reproducir archivos de este tipo!",
                            ephemeral: true
                        })
                        break;
                    case "Request failed with status code 500":
                        await event.reply({
                            content: "Parece que **Spotify** ha tenido un error inesperado! Intentalo otra vez",
                            ephemeral: true
                        })
                        break;
                    case "Request failed with status code 401":
                        await event.reply({
                            content: "Parece que **Spotify** ha tenido un error inesperado! Intentalo otra vez",
                            ephemeral: true
                        })
                        authenticate();
                        break;

                    default:
                        console.log("[DEBUG] LOAD FAILED ON " + search, res);
                        await event.reply({
                            content: lang.commands.play.no_song_found,
                            ephemeral: true,
                            components: [
                                new MessageActionRow()
                                    .addComponents([
                                        new MessageButton()
                                            .setStyle("PRIMARY")
                                            .setLabel("Esto es un error")
                                            .setCustomId("cmd:error:" + search)
                                    ])
                            ]
                        });
                        await client.error_webhook.send({
                            content: `Posible excepción en el comando play: \`\`\`Servidor: ${event.guild.id}\nCanción: ${search}\nExcepción: ${JSON.stringify(res.exception)}\nBot: ${client.user?.username}\`\`\``,
                        })
                        break;
                }
                return;
            }
        } catch (err: unknown) {
            await event.reply({
                content: lang.commands.play.no_song_found,
                ephemeral: true
            });
            return;
        }


        let embed;
        let track: any;

        switch (res.loadType) {

            case 'NO_MATCHES':
                await event.reply({
                    content: lang.commands.play.no_song_found,
                    ephemeral: true
                });
                break;
            case 'TRACK_LOADED':
                if (!player.metadata) player.metadata = [];
                player.metadata = [...player.metadata, search];
                track = res.tracks[0];
                player.queue.add(track);
                if (!player.playing && !player.paused && !player.queue.size) {
                    await player.play();
                }

                embed = new MessageEmbed()
                    //@ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.manager.song_added.replace("%s%", stringFormat(track.title.replace("lyrics", ""))).replace("%l%", track.url).replace("%t%", convertTime(track.duration)))
                await event.reply({
                    embeds: [embed],
                    ephemeral: false
                });
                break;
            case 'PLAYLIST_LOADED':
                if (!search) return event.reply({
                    content: lang.commands.play.youtube_link,
                    ephemeral: true
                })

                if (/^https?:\/\/(open.spotify\.com)\/(.*)$/.test(search)) {
                    for (const track of res.tracks) {
                        if (!player.metadata) player.metadata = [];
                        player.metadata = [...player.metadata, track.uri];
                    }
                }

                if (
                    !/^https?:\/\/(open.spotify\.com)\/(.*)$/.test(search) &&
                    !/^https?:\/\/(soundcloud\.com|snd\.sc)\/(.*)$/.test(search) &&
                    !/^(https?:\/\/)?((www\.)?twitch\.tv)\/(.*)$/.test(search) &&
                    !/^(https?:\/\/)?((www\.)?deezer\.com)\/(.*)$/.test(search) &&
                    !/https:\/\/music\.apple\.com\/(?:.+)?(artist|album|music-video|playlist)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)/i.test(search)
                ) {
                    return event.reply({
                        content: lang.commands.play.youtube_link,
                        ephemeral: true
                    })
                }

                player.queue.add(res.tracks);

                // for (const track of res.tracks) {
                //     if (!player.metadata) player.metadata = [];
                //     player.metadata = [...player.metadata, track.url];
                // }

                if (!player.playing && !player.paused && player.queue.totalSize === res.tracks.length) await player.play();
                embed = new MessageEmbed()
                    .setDescription(lang.manager.playlist_added.replace("%s%", res.tracks.length))
                    //@ts-ignore
                    .setColor(config.color)
                return event.reply({ embeds: [embed], ephemeral: false });
            case 'SEARCH_RESULT':
                track = res.tracks[0];
                if (!player.metadata) player.metadata = [];
                player.metadata = [...player.metadata, search];
                player.queue.add(track);
                if (!player.playing && !player.paused && !player.queue.size) {
                    await player.play();
                }

                embed = new MessageEmbed()
                    //@ts-ignore
                    .setColor(config.color)
                    .setDescription(lang.manager.song_added.replace("%s%", stringFormat(track.title.replace("lyrics", ""))).replace("%l%", track.url).replace("%t%", convertTime(track.duration)))
                await event.reply({
                    embeds: [embed],
                    ephemeral: false
                });
                break;
            default:
                break;

        }
    },
    slashInfo: new SlashCommandBuilder()
        .setName("play")
        .setDescription("Reproducir una canción en el canal de voz!")
        .addStringOption(option =>
            option.setName('canción')
                .setDescription('Canción a reproducir, puede ser un link de Spotify, SoundCloud o un título a buscar!')
                .setRequired(true))
}