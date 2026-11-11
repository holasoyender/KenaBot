/*
 * File:          Plugin.ts
 * Path:          v2\plugins\apple\Plugin.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-06-17 22:32:37 +02:00
 *
 * SHA-256: 2608181f013376412a616d6f047a696734366a399c65d76ae3e49310bff8b4d1
 *
 */


import {
  Manager, Plugin, SearchQuery, SearchResult
} from '../../functions/manager'
import { AppleMusicOptions } from './types'
import { resolver } from './resolver'

export class AppleMusic extends Plugin {
  public constructor(public options: AppleMusicOptions = { cacheTrack: true, maxCacheLifeTime: 360000 }) {
    super()
  }

  public readonly resolver = new resolver(this);

  public manager: Manager | undefined;

  public appleMusicMatch = /(?:https:\/\/music\.apple\.com\/)(?:.+)?(artist|album|music-video|playlist)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)\/([\w\-.]+(\/)+[\w\-.]+|[^&]+)/;

  private _search!: (query: SearchQuery | string, requester?: unknown) => Promise<SearchResult>;

  public async load(manager: Manager) {
    this.manager = manager
    this._search = manager.search.bind(manager)
    manager.search = this.search.bind(this)

    await this.resolver.fetchAccessToken()
  }

  private async search(query: SearchQuery | string, requester?: unknown): Promise<SearchResult> {
    const finalQuery = (query as SearchQuery).query || query as string
    const [, type, , , id] = this.appleMusicMatch.exec(finalQuery) ?? []
    if (type in this.resolver.resolveManager) {
      return this.resolver.resolveManager[type as keyof resolver['resolveManager']].fetch(id, requester)
    }
    return this._search(query, requester)
  }
}
