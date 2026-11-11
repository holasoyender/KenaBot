/*
 * File:          PatreonBody.d.ts
 * Path:          v3.5\api\lib\PatreonBody.d.ts
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-30 18:35:29 +02:00
 *
 * SHA-256: 27049be1cd5419ff5d98fa38e45e7ceb78eef5c4433ef268d78f749f75bfc02f
 *
 */


interface PatreonBody {
    data?: PatreonData,
    included?: [
        PatreonIncludedCapaign,
        PatreonIncludedUser,
        PatreonIncludedTier,
    ]
    links?: {
        self?: string
    }
}

interface PatreonIncludedCapaign {
    attributes?: {
        created_at?: string
        creation_name?: string
        discord_server_id?: string
        google_analytics_id?: string
        has_rss?: boolean
        has_sent_rss_notify?: boolean
        image_small_url?: string
        image_url?: string
        is_charged_immediately?: boolean
        is_monthly?: boolean
        is_nsfw?: boolean
        main_video_embed?: string
        main_video_url?: string
        one_liner?: string
        patron_count?: number
        pay_per_name?: string
        pledge_url?: string
        published_at?: string
        rss_artwork_url?: string
        rss_feed_title?: string
        summary?: string
        thanks_embed?: string
        thanks_msg?: string
        thanks_video_url?: string
        url?: string
        vanity?: string
    },
    id?: string,
    type?: "campaign"
}

interface PatreonIncludedUser {
    attributes?: {
        about?: string
        created?: string
        first_name?: string
        full_name?: string
        hide_pledges?: boolean
        image_url?: string
        is_creator?: boolean
        last_name?: string
        like_count?: number
        social_connections?: SocialConnections
        thumb_url?: string
        url?: string
        vanity?: string
    },
    id?: string,
    type?: "user"
}

interface PatreonIncludedTier {
    attributes?: {
        amount_cents?: number
        created_at?: string
        description?: string
        discord_role_ids?: string[]
        edited_at?: string
        image_url?: string
        patron_count?: number
        post_count?: number
        published?: boolean
        published_at?: string
        remaining?: number
        requires_shipping?: boolean
        title?: string
        unpublished_at?: string
        url?: string
        user_limit?: number
    },
    id?: string,
    type?: "tier"
}

interface SocialConnections {
    [key: "deviantart" | "discord" | "google" | "facebook" | string]: {
        url?: string,
        user_id?: string,
    }
}

interface PatreonData {
    attributes?: {
        campaign_lifetime_support_cents?: number
        currently_entitled_amount_cents?: number
        email?: string
        full_name?: string
        is_follower?: boolean
        last_charge_date?: string
        last_charge_status?: string
        lifetime_support_cents?: number
        next_charge_date?: string
        note?: string
        patron_status?: string
        pledge_cadence?: number
        pledge_relationship_start?: string
        will_pay_amount_cents?: number
    },
    id?: string,
    relationships?: Relationships,
    type?: "member"
}

interface Relationships {
    address?: {
        data?: null
    },
    campaign?: {
        data?: {
            id?: string,
            type?: "campaign"
        },
        links?: {
            related?: string
        }
    },
    currently_entitled_tiers?: {
        data?: {
            id?: string,
            type?: "tier"
        }[]
    },
    user?: {
        data?: {
            id?: string,
            type?: "user"
        },
        links?: {
            related?: string
        }
    },
}