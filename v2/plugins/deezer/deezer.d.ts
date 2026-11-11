/*
 * File:          deezer.d.ts
 * Path:          v2\plugins\deezer\deezer.d.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 22:32:37 +02:00
 *
 * SHA-256: 8c22589fecc2bd33fc8ae76256905ed0b9b1991d06f6fb4158d234c825d1c2bf
 *
 */


import { Manager, Plugin, UnresolvedTrack, UnresolvedQuery } from "../../functions/manager";
export declare class Deezer extends Plugin {
    private _search;
    private manager;
    private readonly functions;
    private readonly options;
    constructor(options: DeezerOptions);
    load(manager: Manager): void;
    private search;
    private getAlbumTracks;
    private getPlaylistTracks;
    private getTrack;
    private static convertToUnresolved;
}
export interface DeezerOptions {
    /** Amount of pages to load, each page having 100 tracks. */
    playlistLimit?: number;
    /** Amount of pages to load, each page having 50 tracks. */
    albumLimit?: number;
    /**
     * Whether to convert UnresolvedTracks to Track. Defaults to false.
     * **Note: This is** ***not*** **recommended as it spams YouTube and takes a while if a large playlist is loaded.**
     */
    convertUnresolved?: boolean;
}
export interface Result {
    tracks: UnresolvedQuery[];
    name?: string;
}
export interface Album {
    name: string;
    tracks: AlbumTracks;
}
export interface AlbumTracks {
    items: DeezerTrack[];
    next: string | null;
}
export interface Artist {
    name: string;
}
export interface Playlist {
    tracks: PlaylistTracks;
    name: string;
}
export interface PlaylistTracks {
    items: [
        {
            track: DeezerTrack;
        }
    ];
    next: string | null;
}
export interface DeezerTrack {
    artists: Artist[];
    name: string;
    duration_ms: number;
}
export interface SearchResult {
    exception?: {
        severity: string;
        message: string;
    };
    loadType: string;
    playlist?: {
        duration: number;
        name: string;
    };
    tracks: UnresolvedTrack[];
}
