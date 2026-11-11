/*
 * File:          private.rs
 * Path:          v4\src\io\spotify\client\private.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-23 22:16:13 +01:00
 *
 * SHA-256: dcd5808b341a8093622ea9a3fdc3dbb68392bc72d5a890667feac08547824417
 *
 */


mod async_client {
    use std::ops::Deref;

    #[derive(Debug, Clone)]
    pub struct AsyncClient(pub(crate) reqwest::Client);

    impl super::HttpClient for AsyncClient {
        fn new() -> Self {
            Self(reqwest::Client::new())
        }
    }

    impl Deref for AsyncClient {
        type Target = reqwest::Client;

        fn deref(&self) -> &Self::Target {
            &self.0
        }
    }
}

use reqwest::{IntoUrl, Method};

pub use self::async_client::AsyncClient;
use crate::io::spotify::error::Result;

pub trait HttpClient {
    fn new() -> Self;
}

/// Every Spotify client implements this trait.
pub trait BuildHttpRequestAsync: crate::io::spotify::private::Sealed {
    /// Returns a new async [RequestBuilder](reqwest::RequestBuilder) with any necessary information (e.g.
    /// authentication headers) filled in. This method doesn't account for any known Spotify error responses
    /// automatically; for that you should use [send_http_request](SendHttpRequestAsync::send_http_request)
    fn build_http_request<U>(&self, method: Method, url: U) -> reqwest::RequestBuilder
    where
        U: IntoUrl;
}


/// Every Spotify client implements this trait.
#[async_trait::async_trait]
pub trait AccessTokenExpiryAsync: crate::io::spotify::private::Sealed {
    // if specialisation was a thing, this function could be refactored into two generic trait impls
    async fn handle_access_token_expired(&mut self) -> Result<AccessTokenExpiryResult>;
}

/// Result to having tried to refresh a client's access token.
#[derive(Debug, PartialEq, Eq)]
pub enum AccessTokenExpiryResult {
    /// Refreshing the token succeeded
    Ok,
    /// Refreshing an access token is not applicable to this client
    Inapplicable,
}
