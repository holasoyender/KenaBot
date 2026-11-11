/*
 * File:          api.ts
 * Path:          v2\plugins\spotify\api\api.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-18 15:18:18 +02:00
 *
 * SHA-256: f052387fcd01433aa5521d177d78328a1a383d6e7b599acf272b31101eef687f
 *
 */


import axios from "axios";
import fetch from "petitio";
import Zeppelin from "../../../utils/Client";

let token = ""
let client: Zeppelin | null = null
export const getToken = (): string => {
    return token;
}

export const renewToken = async (): Promise<number> => {
    const { access_token, expires_in } = await fetch("https://accounts.spotify.com/api/token", "POST")
        .query("grant_type", "client_credentials")
        .header("Authorization", `Basic ${Buffer.from(`${process.env.SPOTIFY_ID?.toString()}:${process.env.SPOTIFY_SECRET?.toString()}`).toString("base64")}`)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .json();

    if (!access_token) {
        throw new Error("Invalid Spotify client.");
    }

    token = access_token;
    return (expires_in * 1000);
}

export const authenticate = async (_client?: Zeppelin): Promise<void> => {
    if (_client && !client)
        client = _client

    client?.error_webhook.send({
        content: "```yml\nSe ha detectado una nueva conexión a Spotify desde el bot " + client.user?.username + "```",
    })

    const expiresIn = await renewToken();
    setTimeout(() => authenticate(), expiresIn);
}

export const getTrack = async (trackID: string) => {
    const res = await axios.get(`https://api.spotify.com/v1/tracks/${trackID}`, {
        headers: {
            Authorization: `Bearer ${getToken()}`,
        }
    })
    return res.data;
}

export const getPlaylist = async (playListID: string) => {
    const res = await axios.get(`https://api.spotify.com/v1/playlists/${playListID}`, {
        headers: {
            Authorization: `Bearer ${getToken()}`,
        }
    })
    return res.data;
}

export const getAlbum = async (albumID: string) => {
    const res = await axios.get(`https://api.spotify.com/v1/albums/${albumID}`, {
        headers: {
            Authorization: `Bearer ${getToken()}`,
        }
    })
    return res.data;
}


export const getTrackByURL = async (trackURL: string) => {
    const regex = /(?<=https:\/\/open\.spotify\.com\/track\/)([a-zA-Z0-9]{15,})/g;
    if (!regex.test(trackURL)) return null;
    // @ts-ignore
    const trackID = trackURL.match(regex)[0];
    return await getTrack(trackID);
}

export const getAlbumByURL = async (albumURL: string) => {
    const regex = /(?<=https:\/\/open\.spotify\.com\/album\/)([a-zA-Z0-9]{15,})/g;
    if (!regex.test(albumURL)) return null;
    // @ts-ignore
    const albumID = albumURL.match(regex)[0];
    return await getAlbum(albumID);
}

export const getPlaylistByURL = async (playlistURL: string) => {
    const regex = /(?<=https:\/\/open\.spotify\.com\/playlist\/)([a-zA-Z0-9]{15,})/g;
    if (!regex.test(playlistURL)) return null;
    // @ts-ignore
    const playListID = playlistURL.match(regex)[0];
    return await getPlaylist(playListID);
}
