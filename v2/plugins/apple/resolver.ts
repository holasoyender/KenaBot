/*
 * File:          resolver.ts
 * Path:          v2\plugins\apple\resolver.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 22:32:37 +02:00
 *
 * SHA-256: c9f9ec6f44b302fc13c4aeeead4aece6925abe2e5844f2559a3b2000e4ed9806
 *
 */


import { fetch } from 'undici'
import cheerio from 'cheerio'
import { AppleMusic } from './Plugin'
import { AppleMusicMetaTagResponse } from './types'

import {
  MusicVideoManager, PlaylistManager, AlbumManager, ArtistManager
} from './Manager/index'

export class resolver {
  public constructor (public plugin: AppleMusic) { }

  public token: string | undefined = undefined;

  public resolveManager = {
    'music-video': new MusicVideoManager(this),
    playlist: new PlaylistManager(this),
    album: new AlbumManager(this),
    artist: new ArtistManager(this)
  };

  public async fetchAccessToken () {
    try {
      const response = await fetch('https://music.apple.com')
      const textResponse = await response.text()
      const $ = cheerio.load(textResponse)
      const token = JSON.parse(decodeURIComponent($('meta[name=desktop-music-app/config/environment]').attr('content')!)) as AppleMusicMetaTagResponse
      this.token = `Bearer ${token.MEDIA_API.token}`
    } catch (_e) {
      /* Do nothing. */
    }
  }
}
