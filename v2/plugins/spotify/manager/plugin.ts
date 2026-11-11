/*
 * File:          plugin.ts
 * Path:          v2\plugins\spotify\manager\plugin.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 01:14:19 +02:00
 *
 * SHA-256: 31fef4b41037e344d518ac6380c3a73486d91050288c510aba06994f41e2d4b5
 *
 */


import {
    Manager,
    Plugin,
    TrackUtils,
    UnresolvedTrack,
    UnresolvedQuery,
    LoadType,
    SearchQuery,
    ModifyRequest
} from "../../../functions/manager";
import fetch from "petitio";
import { getToken } from "../api/api";

const BASE_URL = "https://api.spotify.com/v1";
const REGEX = /(?:https:\/\/open\.spotify\.com\/|spotify:)(?:.+)?(track|playlist|album|artist)[/:]([A-Za-z0-9]+)/;

const buildSearch = (loadType: LoadType, tracks: UnresolvedTrack[] | null, error: string | null, name: string | null): SearchResult => ({
    loadType: loadType,
    tracks: tracks ?? [],
    playlist: name ? {
        name,
        duration: (tracks as any).reduce(
            (acc: number, cur: UnresolvedTrack) => acc + (cur.duration || 0),
            0,
        ),
    } : undefined,
    exception: error ? {
        message: error,
        severity: "COMMON",
    } : undefined,
});

export class Spotify extends Plugin {
    private _search: ((query: string | SearchQuery, requester?: unknown) => Promise<SearchResult>) | null;
    private manager: Manager | null;
    private readonly functions: Record<string, any>;

    public constructor() {
        super();
        this.functions = {
            track: this.getTrack.bind(this),
            album: this.getAlbumTracks.bind(this),
            playlist: this.getPlaylistTracks.bind(this),
            artist: this.getArtistTracks.bind(this),
        };
        this.manager = null
        this._search = null
    }

    public load(manager: Manager) {
        this.manager = manager;
        //@ts-ignore
        this._search = manager.search.bind(manager);
        //@ts-ignore
        manager.search = this.search.bind(this);
    }

    public makeRequest<T>(endpoint: string, modify: ModifyRequest = () => void 0): Promise<T> {
        const req = fetch(`${BASE_URL}${/^\//.test(endpoint) ? endpoint : `/${endpoint}`}`)
            .header("Authorization", `Bearer ${getToken()}`);

        modify(req);
        return req.json();
    }

    private async search(query: string | SearchQuery, requester?: unknown): Promise<SearchResult> {
        const finalQuery = (query as SearchQuery).query || query as string;
        const [, type, id] = finalQuery.match(REGEX) ?? [];

        if (type in this.functions) {
            try {
                const func = this.functions[type];

                if (func) {
                    const data: Result = await func(id);

                    const loadType = type === "track" ? "TRACK_LOADED" : "PLAYLIST_LOADED";
                    const name = ["playlist", "album", "artist"].includes(type) ? data.name : null;

                    const tracks = data.tracks.map(query => {
                        const track = TrackUtils.buildUnresolved(query, requester);

                        return track;
                    }).filter(track => !!track);

                    return buildSearch(loadType, tracks, null, name || null);
                }

                const msg = "Incorrect type for Spotify URL, must be one of \"track\", \"album\" or \"playlist\".";
                return buildSearch("LOAD_FAILED", null, msg, null);
            } catch (e: any) {
                return buildSearch(e.loadType ?? "LOAD_FAILED", null, e.message ?? null, null);
            }
        }

        //@ts-ignore
        return this._search(query, requester);
    }

    private async getAlbumTracks(id: string): Promise<Result> {
        const album = await this.makeRequest<Album>(`albums/${id}`)
        const tracks = album.tracks.items.filter(this.filterNullOrUndefined).map(item => Spotify.convertToUnresolved(item));
        let next = album.tracks.next;

        while (next) {
            const nextPage = await this.makeRequest<AlbumTracks>(next);
            tracks.push(...nextPage.items.filter(this.filterNullOrUndefined).map(item => Spotify.convertToUnresolved(item)));
            next = nextPage.next;
        }

        return { tracks, name: album.name };
    }

    private async getPlaylistTracks(id: string): Promise<Result> {
        const playlist = await this.makeRequest<Playlist>(`playlists/${id}`);
        const tracks = playlist.tracks.items.filter(this.filterNullOrUndefined).map(item => Spotify.convertToUnresolved(item.track));
        let next = playlist.tracks.next

        while (next) {
            const nextPage: any = await fetch(next).header("Authorization", `Bearer ${getToken()}`).json();
            tracks.push(...nextPage.items.filter(this.filterNullOrUndefined).map((item: any) => Spotify.convertToUnresolved(item.track)));
            next = nextPage.next;
        }

        return { tracks, name: playlist.name };
    }

    private async getArtistTracks(id: string): Promise<Result> {

        const playlist = await this.makeRequest<any>(`artists/${id}/top-tracks?market=ES`);
        const tracks: any = playlist.tracks.filter(this.filterNullOrUndefined).map((item: any) => Spotify.convertToUnresolved(item));
        let next = playlist.tracks.next

        while (next) {
            const nextPage = await this.makeRequest<PlaylistTracks>(next);
            tracks.push(...nextPage.items.filter(this.filterNullOrUndefined).map(item => Spotify.convertToUnresolved(item.track)));
            next = nextPage.next;
        }

        return { tracks, name: tracks[0].author.split(", ")[0] }

    }

    private async getTrack(id: string): Promise<Result> {
        const data = await this.makeRequest<SpotifyTrack>(`tracks/${id}`);
        const track = Spotify.convertToUnresolved(data);
        return { tracks: [track] };
    }

    private static convertToUnresolved(track: SpotifyTrack): UnresolvedQuery {
        if (!track) throw new ReferenceError("The Spotify track object was not provided");
        if (!track.artists) throw new ReferenceError("The track artists array was not provided");
        if (!track.name) throw new ReferenceError("The track name was not provided");
        if (!Array.isArray(track.artists)) throw new TypeError(`The track artists must be an array, received type ${typeof track.artists}`);
        if (typeof track.name !== "string") throw new TypeError(`The track name must be a string, received type ${typeof track.name}`);

        return {
            title: track.name,
            author: track.artists.map(u => u.name).join(", "),
            duration: track.duration_ms,
            //@ts-ignore
            uri: track.external_urls.spotify,
        };
    }


    private filterNullOrUndefined(value: unknown): value is unknown {
        return typeof value !== 'undefined' ? value !== null : typeof value !== 'undefined';
    }
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
    items: SpotifyTrack[];
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
            track: SpotifyTrack;
        }
    ];
    next: string | null;
}

export interface SpotifyTrack {
    artists: Artist[];
    name: string;
    duration_ms: number;
    uri: string
}

export interface SearchResult {
    exception?: {
        severity: string;
        message: string
    };
    loadType: string;
    playlist?: {
        duration: number;
        name: string
    };
    tracks: UnresolvedTrack[]
}
