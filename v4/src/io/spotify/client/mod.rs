/*
 * File:          mod.rs
 * Path:          v4\src\io\spotify\client\mod.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-12-25 18:57:51 +01:00
 *
 * SHA-256: 972c92bb8bd644b13465f7902af90d30028c95b4d1b02ea420cb703fcb5ea617
 *
 */


//! Clients for every OAuth2 authorization flow Spotify supports.
//!
//! # Which authorization flow to use?
//!
//! - If you require access to user-specific scoped endpoints, or cannot store the application client secret safely, use
//!   the [authorization code flow client](authorization_code). The client supports PKCE if the application client
//!   secret is unavailable.
//! - If you do not require access to user-specific scoped endpoints and have the application client secret available,
//!   use the [client credentials flow client](SpotifyClientWithSecret).
//!
//! Every client requires an application client ID. You can create a new application in the
//! [Spotify developer dashboard](https://developer.spotify.com/dashboard), from where you get the application's client
//! ID and secret. If the secret can be safely stored in your environment, you may use the client credentials flow by
//! building a [SpotifyClientWithSecret] which can access all [unscoped endpoints](UnscopedClient). From there, you
//! can retrieve a user-authorized [authorization code flow client](authorization_code) which can access all [scoped
//! endpoints](ScopedClient) in addition to the [unscoped endpoints](UnscopedClient).
//!
//! However, if the client secret cannot be safely stored in your environment, you may still access all
//! [unscoped](UnscopedClient) and [scoped endpoints](ScopedClient) by using the [authorization code flow with
//! PKCE](SpotifyClient::authorization_code_client_with_pkce). The [implicit grant flow is also
//! supported](SpotifyClient::implicit_grant_client), but it is not recommended for use.
//!
//! [Spotify documentation on authorization.](https://developer.spotify.com/documentation/general/guides/authorization/)
//!
//! | Authorization flow | Access user resources | Requires secret key | Access token refresh |
//! |-|-|-|-|
//! | [AuthorizationCodeUserClient with PKCE](authorization_code) | Yes | No | Yes |
//! | [AuthorizationCodeUserClient](authorization_code) | Yes | Yes | Yes |
//! | [Client credentials](SpotifyClientWithSecret) | No | Yes | Yes |
//! | [ImplicitGrantUserClient](implicit_grant) (not recommended) | Yes | No | No |
//!
//! ## Client credentials flow
//!
//! [SpotifyClientWithSecret](SpotifyClientWithSecret) implements the client credentials flow. A new client may be built
//! with a [SpotifyClientBuilder]
//!
//! ```no_run
//! # use ferrispot::{client::SpotifyClientBuilder, model::ItemType, prelude::*};
//! # async fn foo() {
//! let spotify_client = SpotifyClientBuilder::new(
//!     std::env::var("CLIENT_ID").expect("Spotify client ID not in environment"),
//! )
//! .client_secret(
//!     std::env::var("CLIENT_SECRET").expect("Spotify client secret not in environment"),
//! )
//! // a synchronous (blocking) client may be built with .build_sync() if the "sync" crate
//! // feature is enabled
//! .build_async()
//! .await
//! .expect("failed to build Spotify client");
//!
//! // all unscoped endpoints are now available through this client
//!
//! let search_results = spotify_client
//!     .search("hatsune miku")
//!     .types([ItemType::Track])
//!     .send_async()
//!     .await
//!     .unwrap();
//!
//! let first_page = search_results.tracks().unwrap();
//!
//! println!("First page:");
//! for track in first_page.items() {
//!     println!(
//!         "{} - {} ({}) [{}]",
//!         track.name(),
//!         track.artists().first().unwrap().name(),
//!         track.album().name(),
//!         track.id(),
//!     );
//! }
//! # }
//! ```
//!
//! ## Authorization code flow with optional PKCE
//!
//! See the module-level documentation for the [authorization code module](authorization_code).
//!
//! ## Implicit grant flow
//!
//! See the module-level documentation for the [implicit grant module](implicit_grant). Note that it is not recommended
//! for use. It is recommended to use the [authorization code module](authorization_code) in order to access scoped
//! endpoints.

pub mod authorization_code;
pub mod implicit_grant;
pub mod request_builder;

pub(crate) mod object;
pub(crate) mod private;
pub(crate) mod unscoped;

use std::sync::{Arc, RwLock};

use base64::Engine;
use const_format::concatcp;
use log::debug;
use rand::prelude::SliceRandom;
use reqwest::{
    header::{self, HeaderMap},
    IntoUrl, Method, StatusCode,
};
use serde::Deserialize;

use self::implicit_grant::ImplicitGrantUserClientBuilder;
use self::{
    authorization_code::{AsyncAuthorizationCodeUserClient, AsyncAuthorizationCodeUserClientBuilder},
    implicit_grant::AsyncImplicitGrantUserClientBuilder,
    private::AsyncClient,
};
pub use self::{unscoped::UnscopedClient};
use crate::io::spotify::{
    error::{Error, Result},
    model::error::{AuthenticationErrorKind, AuthenticationErrorResponse},
};
use crate::managers::spotify::SpotifyKey;
use crate::util;

/// Type alias for an asynchronous Spotify client. See [SpotifyClient](SpotifyClient).
pub type AsyncSpotifyClient = SpotifyClient<AsyncClient>;

const RANDOM_STATE_LENGTH: usize = 16;
const PKCE_VERIFIER_LENGTH: usize = 128; // maximum Spotify allows
const CLIENT_CREDENTIALS_TOKEN_REQUEST_FORM: &[(&str, &str)] = &[("grant_type", "client_credentials")];

const API_BASE_URL: &str = "https://api.spotify.com/v1/";

// unscoped endpoints
const API_TRACKS_ENDPOINT: &str = concatcp!(API_BASE_URL, "tracks");
const API_SEARCH_ENDPOINT: &str = concatcp!(API_BASE_URL, "search");
const API_ARTIST_ENDPOINT: &str = concatcp!(API_BASE_URL, "artists");


// scoped endpoints
const API_USER_PROFILE_ENDPOINT: &str = concatcp!(API_BASE_URL, "users");
const API_CURRENT_USER_PROFILE_ENDPOINT: &str = concatcp!(API_BASE_URL, "me");

// accounts
const ACCOUNTS_BASE_URL: &str = "https://accounts.spotify.com/";
const ACCOUNTS_AUTHORIZE_ENDPOINT: &str = concatcp!(ACCOUNTS_BASE_URL, "authorize");
const ACCOUNTS_API_TOKEN_ENDPOINT: &str = concatcp!(ACCOUNTS_BASE_URL, "api/token");

/// Clients that have automatically refreshable access tokens implement this trait. The asynchronous
/// [SpotifyClientWithSecret](SpotifyClientWithSecret) and
/// [AuthorizationCodeUserClient](authorization_code::AuthorizationCodeUserClient) implement this trait.
///
/// Note that [ImplicitGrantUserClient](implicit_grant::ImplicitGrantUserClient) does *not* implement
/// this trait, since even though it has an access token, it cannot be automatically refreshed.
#[async_trait::async_trait]
pub trait AccessTokenRefreshAsync: crate::io::spotify::private::Sealed {
    /// Asynchronously request a new access token from Spotify and save it internally in the client.
    async fn refresh_access_token(&mut self) -> Result<()>;
}

/// A base Spotify client that does *not* have a client secret.
///
/// This client by itself cannot be used to access the Spotify API, since it has no way of authenticating itself to the
/// API. However, it can be used to retrieve either user-authenticated client; [AuthorizationCodeUserClient with
/// PKCE](authorization_code::AuthorizationCodeUserClient) or an
/// [ImplicitGrantUserClient](implicit_grant::ImplicitGrantUserClient).
///
/// This struct is generic over its internal asynchronous/synchronous HTTP client. You cannot refer to the internal
/// client types directly, hence there are type aliases for both kinds of clients: [AsyncSpotifyClient] and
/// [SyncSpotifyClient]. Likewise, the builder struct is similarly generic, and has equivalent type aliases.
///
/// This client uses `Arc` and interior mutability internally, so you do not need to wrap it in an `Arc` in order to
/// reuse it; it is cheap to clone, and all clones refer to the same internal structures.
#[derive(Debug, Clone)]
pub struct SpotifyClient<C>
    where
        C: private::HttpClient,
{
    inner: Arc<SpotifyClientRef>,
    http_client: C,
}

#[derive(Debug)]
struct SpotifyClientRef {
    keys: Vec<SpotifyKey>,
    client_id: String,
    access_token: RwLock<String>,
}

/// Builder for [SpotifyClient](SpotifyClient).
#[derive(Debug, Clone)]
pub struct SpotifyClientBuilder {
    keys: Vec<SpotifyKey>,
    current_key: usize,
}

#[derive(Debug, Deserialize)]
struct ClientTokenResponse {
    access_token: String,

    // these fields are in the response but the library doesn't need them. keep them here for logging purposes
    #[allow(dead_code)]
    token_type: String,
    #[allow(dead_code)]
    expires_in: u32,
}

impl AsyncSpotifyClient {
    /// Returns a new builder for an [ImplicitGrantUserClient](implicit_grant::ImplicitGrantUserClient).
    ///
    /// # Note
    ///
    /// The implicit grant user client is not recommended for use. The access token is returned in the callback URL
    /// instead of through a trusted channel, and the token cannot be automatically refreshed. It is recommended to use
    /// the [authorization code flow with PKCE flow](SpotifyClient::authorization_code_client_with_pkce) instead.
    pub fn implicit_grant_client<S>(&self, redirect_uri: S) -> AsyncImplicitGrantUserClientBuilder
        where
            S: Into<String>,
    {
        ImplicitGrantUserClientBuilder::new(redirect_uri.into(), Arc::clone(&self.inner), self.http_client.clone())
    }

    /// Returns a new builder for an [AuthorizationCodeUserClient](authorization_code::AuthorizationCodeUserClient)
    /// that uses PKCE.
    ///
    /// PKCE is required for strong authentication when the client secret cannot be securely stored in the environment.
    pub fn authorization_code_client_with_pkce<S>(&self, redirect_uri: S) -> AsyncAuthorizationCodeUserClientBuilder
        where
            S: Into<String>,
    {
        AsyncAuthorizationCodeUserClientBuilder::new(
            redirect_uri.into(),
            self.inner.client_id.clone(),
            self.http_client.clone(),
        )
            .with_pkce()
    }

    /// Returns a new [AuthorizationCodeUserClient](authorization_code::AuthorizationCodeUserClient) that uses PKCE and
    /// an existing refresh token.
    ///
    /// The refresh token will be used to retrieve a new access token before the client is returned. PKCE is required
    /// for strong authentication when the client secret cannot be securely stored in the environment.
    pub async fn authorization_code_client_with_refresh_token_and_pkce<S>(
        &self,
        refresh_token: S,
    ) -> Result<AsyncAuthorizationCodeUserClient>
        where
            S: Into<String>,
    {
        AsyncAuthorizationCodeUserClient::new_with_refresh_token(
            self.http_client.clone(),
            refresh_token.into(),
            Some(self.inner.client_id.clone()),
        )
            .await
    }

    /// Returns a new builder for an asynchronous
    /// [AuthorizationCodeUserClient](authorization_code::AuthorizationCodeUserClient).
    pub fn authorization_code_client<S>(&self, redirect_uri: S) -> AsyncAuthorizationCodeUserClientBuilder
        where
            S: Into<String>,
    {
        AsyncAuthorizationCodeUserClientBuilder::new(
            redirect_uri.into(),
            self.inner.client_id.clone(),
            self.http_client.clone(),
        )
    }

    /// Returns a new asynchronous [AuthorizationCodeUserClient](authorization_code::AuthorizationCodeUserClient) that
    /// uses an existing refresh token.
    ///
    /// The refresh token will be used to retrieve a new access token before the client is returned.
    pub async fn authorization_code_client_with_refresh_token<S>(
        &self,
        refresh_token: S,
    ) -> Result<AsyncAuthorizationCodeUserClient>
        where
            S: Into<String>,
    {
        AsyncAuthorizationCodeUserClient::new_with_refresh_token(self.http_client.clone(), refresh_token.into(), None)
            .await
    }
}

impl SpotifyClientBuilder {
    /// Return a new Spotify client builder with a given client ID.
    pub fn new(keys: Vec<SpotifyKey>) -> Self
    {
        Self {
            keys,
            current_key: 0,
        }
    }

    pub async fn build_async(self) -> Result<AsyncSpotifyClient> {

        let http_client = AsyncClient(
            reqwest::Client::builder()
                .default_headers(self.get_default_headers())
                .build()
                // this can only fail due to a system error or system misconfiguration
                .expect("failed to build HTTP client: system error or system misconfiguration"),
        );

        let response = http_client
            .post(ACCOUNTS_API_TOKEN_ENDPOINT)
            .form(CLIENT_CREDENTIALS_TOKEN_REQUEST_FORM)
            .send()
            .await?;

        let response = extract_authentication_error_async(response)
            .await
            .map_err(map_client_authentication_error)?;

        let token_response = response.json().await?;

        Ok(self.build_client(token_response, http_client))
    }

    fn get_default_headers(&self) -> HeaderMap {
        let mut default_headers = HeaderMap::new();

        // insert default client credentials header. the same HTTP client will be used with future user clients that
        // require the authorization in order to exchange an authorization code for access and refresh tokens
        // when finalizing the clients

        let key = self.keys.choose(&mut rand::thread_rng()).unwrap();
        info!("Requesting access token for client credentials flow with key: {}", util::censor(&key.client_id));

        default_headers.insert(
            header::AUTHORIZATION,
            build_authorization_header(&key.client_id, &key.client_secret)
                .parse()
                // this can only fail if the header value contains non-ASCII characters, which shouldn't happen since
                // the given header value is in base64
                .expect(
                    "failed to insert authorization header into header map: non-ASCII characters in value (this is \
                     likely a bug)",
                ),
        );

        default_headers
    }

    fn build_client<C>(self, token_response: ClientTokenResponse, http_client: C) -> SpotifyClient<C>
        where
            C: private::HttpClient + Clone,
    {
        debug!("Got token response for client credentials flow: {:?}", token_response);

        SpotifyClient {
            inner: Arc::new(SpotifyClientRef {
                keys: self.keys.clone(),
                client_id: self.keys[self.current_key].client_id.clone(),
                // client_secret: self.client_secret,
                access_token: RwLock::new(token_response.access_token),
            }),
            http_client,
        }
    }
}

impl<C> crate::io::spotify::private::Sealed for SpotifyClient<C> where C: private::HttpClient + Clone {}

impl<C> SpotifyClient<C>
    where
        C: private::HttpClient + Clone,
{
    fn save_access_token(&self, token_response: ClientTokenResponse) {
        debug!("Got token response for client credentials flow: {:?}", token_response);
        *self.inner.access_token.write().expect("access token rwlock poisoned") = token_response.access_token;
    }
}

impl private::BuildHttpRequestAsync for AsyncSpotifyClient {
    fn build_http_request<U>(&self, method: Method, url: U) -> reqwest::RequestBuilder
        where
            U: IntoUrl,
    {
        let access_token = self.inner.access_token.read().expect("access token rwlock poisoned");
        self.http_client.request(method, url).bearer_auth(access_token.as_str())
    }
}

impl UnscopedClient for AsyncSpotifyClient {}

#[async_trait::async_trait]
impl AccessTokenRefreshAsync for AsyncSpotifyClient {
    async fn refresh_access_token(&mut self) -> Result<()> {

        // build the HTTP request straight from the client so it'll use the client credentials authorization header
        // instead of the access token

        let key = self.inner.keys.choose(&mut rand::thread_rng()).unwrap();
        info!("Refreshing access token for client credentials flow with key: {}", util::censor(&key.client_id));

        let mut headers = HeaderMap::new();

        headers.insert(
            header::AUTHORIZATION,
            build_authorization_header(&key.client_id, &key.client_secret)
                .parse()
                // this can only fail if the header value contains non-ASCII characters, which shouldn't happen since
                // the given header value is in base64
                .expect(
                    "failed to insert authorization header into header map: non-ASCII characters in value (this is \
                     likely a bug)",
                ),
        );

        self.http_client = AsyncClient(
            reqwest::Client::builder()
                .default_headers(headers)
                .build()
                // this can only fail due to a system error or system misconfiguration
                .expect("failed to build HTTP client: system error or system misconfiguration"),
        );

        let response = self
            .http_client
            .post(ACCOUNTS_API_TOKEN_ENDPOINT)
            .form(CLIENT_CREDENTIALS_TOKEN_REQUEST_FORM)
            .send()
            .await?;

        let response = extract_authentication_error_async(response)
            .await
            .map_err(map_client_authentication_error)?;

        let token_response = response.json().await?;
        self.save_access_token(token_response);

        Ok(())
    }
}

#[async_trait::async_trait]
impl private::AccessTokenExpiryAsync for AsyncSpotifyClient {
    async fn handle_access_token_expired(&mut self) -> Result<private::AccessTokenExpiryResult> {
        self.refresh_access_token().await?;
        Ok(private::AccessTokenExpiryResult::Ok)
    }
}

fn build_authorization_header(client_id: &str, client_secret: &str) -> String {
    let auth = format!("{client_id}:{client_secret}");
    format!(
        "Basic {}",
        base64::engine::general_purpose::STANDARD_NO_PAD.encode(auth)
    )
}

/// Takes a response for an authentication request and if its status is 400, parses its body as an authentication error.
/// On success returns the given response without modifying it.
async fn extract_authentication_error_async(response: reqwest::Response) -> Result<reqwest::Response> {
    if let StatusCode::BAD_REQUEST = response.status() {
        let error_response: AuthenticationErrorResponse = response.json().await?;
        debug!("Authentication error response: {error_response:?}");

        Err(error_response.into_unhandled_error())
    } else {
        Ok(response)
    }
}

// sleeping with tokio takes precedence over async_std so if the user enables both features for some reason, they get
// tokio sleep
/// Sleep for the specified amount of time using tokio's sleep function.
async fn rate_limit_sleep_async(sleep_time: u64) -> Result<()> {
    tokio::time::sleep(std::time::Duration::from_secs(sleep_time)).await;
    Ok(())
}

fn map_client_authentication_error(err: Error) -> Error {
    if let Error::UnhandledAuthenticationError(AuthenticationErrorKind::InvalidClient, description) = err {
        Error::InvalidClient(description)
    } else {
        err
    }
}