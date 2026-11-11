/*
 * File:          GeniusAPI.ts
 * Path:          v2\functions\GeniusAPI.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-03-02 20:22:17 +01:00
 *
 * SHA-256: 4e8153db34112b39e1029940a1201fa307c94458f4ce1967d5a1e307698075c3
 *
 */


import axios from "axios";
import cio from "cheerio";

const searchUrl = 'https://api.genius.com/search?q=';
export const getSong = async function (options:{apiKey?: string, title: string, artist: string, optimizeQuery: boolean}) {
    try {
        const results = await searchSong(options);
        if (!results) return null;
        const lyrics = await extractLyrics(results[0].url);
        return {
            id: results[0].id,
            title: results[0].title,
            url: results[0].url,
            lyrics,
            albumArt: results[0].albumArt
        };
    } catch (e) {
        return;
    }
};

export const searchSong = async function (options: {apiKey?: string, title: string, artist: string, optimizeQuery: boolean, authHeader?: boolean}) {
    try {
        const { apiKey, title, artist, optimizeQuery = false, authHeader = false } = options;
        const song = optimizeQuery ? getTitle(title, artist) : `${title} ${artist}`;
        const reqUrl = `${searchUrl}${encodeURIComponent(song)}`;
        const headers = {
            Authorization: 'Bearer ' + apiKey
        };
        const { data } = await axios.get(
            authHeader ? reqUrl : `${reqUrl}&access_token=${apiKey}`,
             // @ts-ignore
            authHeader && { headers }
        );
        if (data.response.hits.length === 0) return null;
        return data.response.hits.map((val:any) => {
            const {full_title, song_art_image_url, id, url} = val.result;
            return {id, title: full_title, albumArt: song_art_image_url, url};
        });
    } catch (e) {
        return;
    }
}

export const getTitle = (title:string, artist:string) => {
    return `${title} ${artist}`
        .toLowerCase()
        .replace(/ *\([^)]*\) */g, '')
        .replace(/ *\[[^\]]*]/, '')
        .replace(/feat.|ft./g, '')
        .replace(/\s+/g, ' ')
        .trim();
};

export const extractLyrics = async function (url:string) {
    try {
        const { data } = await axios.get(url);
        const $ = cio.load(data);
        let lyrics = $('div[class="lyrics"]').text().trim();
        if (!lyrics) {
            lyrics = ''
            $('div[class^="Lyrics__Container"]').each((i:any, elem:any) => {
                if($(elem).text().length !== 0) {
                    const snippet = $(elem).html()?.replace(/<br>/g, '\n').replace(/<(?!\s*br\s*\/?)[^>]+>/gi, '');
                    lyrics += $('<textarea/>').html(snippet+"").text().trim() + '\n\n';
                }
            })
        }
        if (!lyrics) return null;
        return lyrics.trim();
    } catch (e) {
        return;
    }
};