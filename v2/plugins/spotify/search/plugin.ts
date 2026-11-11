/*
 * File:          plugin.ts
 * Path:          v2\plugins\spotify\search\plugin.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-07-16 01:14:19 +02:00
 *
 * SHA-256: f5d45b3732ad0958a6a38b49722dc1e55fbce78aec8d80727cf931dd1cad7e8c
 *
 */


import axios from "axios";
import { getToken, authenticate } from "../api/api";

let err_cnt = 0;

export const searchTrack = async (query: string, offset = 0, limit = 20) => {

  if (limit < 0 && limit > 50)
    return console.log("search failed: limit can at min be 1 or max 50, your limit=" + limit);

  try {
    const q = encodeURIComponent(query);
    const encodedQuery = `q=${q}&type=track&offset=${offset}&limit=${limit}`;
    const search = await searchService(encodedQuery, getToken());

    err_cnt = 0;
    return search.data;
  } catch (e: any) {
    if (err_cnt < 3) {
      await authenticate();
      searchTrack(query, offset, limit);
      err_cnt++;
    }
  }
}

const searchService = async (query: string, token: string) => {
  return await axios.get(`https://api.spotify.com/v1/search?${query}`, {
    headers: {
      Authorization: "Bearer " + token,
    }
  })
};
