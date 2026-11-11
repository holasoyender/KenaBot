/*
 * File:          discord.rs
 * Path:          v4\src\discord.rs
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2024-08-28 17:42:45 +02:00
 *
 * SHA-256: c4d9670a5029a26f9695dedb261684bf7e73c134b5780cb5ae8879fb8d2ac50f
 *
 */


use std::error::Error;
use std::sync::Arc;
use std::time::Instant;
use redis::aio::ConnectionManager as RedisConnection;
use skua::all::{ActivityData, GatewayIntents, UserId};
use skua::cache::Settings;
use skua::Client;
use skua::http::Http;
use mongodb::Database;
use mongodb::Client as MongoClient;
use skua_voice::SkuaInit;
use crate::api;
use crate::globals::{Atenea, CachePool, DatabasePool, ShardManagerContainer, Uptime};
use crate::io::voice::atenea::load_client;
use crate::managers::command::CommandManager;
use crate::locale::manager::LanguageManager;
use crate::managers::dev_command::DevCommandManager;
use crate::managers::spotify::SpotifyManager;

pub struct Handler {
    pub db: Database,
    pub stats_db: Database,
    pub cache: RedisConnection,
    pub bot_id: parking_lot::RwLock<Option<UserId>>,
    pub bot_name: parking_lot::RwLock<Option<String>>,
    pub command_manager: CommandManager,
    pub dev_command_manager: DevCommandManager,
    pub language_manager: LanguageManager,
    pub spotify_manager: SpotifyManager
}

pub(crate) async fn setup_bot(database: &MongoClient, redis: &RedisConnection) -> Result<Client, Box<dyn Error>> {

    let intents = GatewayIntents::GUILD_VOICE_STATES | GatewayIntents::DIRECT_MESSAGES | GatewayIntents::GUILDS;
    let token = std::env::var("BOT_TOKEN")?;

    let mut cache = Settings::default();
    cache.cache_guilds = true;
    cache.cache_channels = true;
    cache.cache_users = false;
    cache.max_messages = 0;

    let http = Http::new(&token);

    let (bot_id, client_name) = match http.get_current_user().await {
        Ok(bot) => (bot.id, bot.name.clone()),
        Err(why) => panic!("Could not access the bot id: {:?}", why),
    };

    let spotify_manager = SpotifyManager::new().await;
    let language_manager = LanguageManager::new();
    let command_manager = CommandManager::new();
    let dev_command_manager = DevCommandManager::new();

    let db = database.database(client_name.replace(" ", "_").as_str());
    let stats_db = database.database("Stats");

    let handler = Handler {
        db: db.clone(),
        stats_db: stats_db.clone(),
        cache: redis.clone(),
        bot_id: parking_lot::RwLock::new(Some(bot_id)),
        bot_name: parking_lot::RwLock::new(Some(client_name.clone())),
        command_manager: command_manager.clone(),
        dev_command_manager: dev_command_manager.clone(),
        language_manager: language_manager.clone(),
        spotify_manager
    };

    let mut client =
        Client::builder(&token, intents)
            .activity(ActivityData::custom("/help - kenabot.xyz"))
            .event_handler(handler)
            .cache_settings(cache)
            .register_songbird()
            .await
            .expect("Error creating client");
    {
        let mut data = client.data.write().await;

        data.insert::<ShardManagerContainer>(Arc::clone(&client.shard_manager));
        data.insert::<Uptime>(Arc::new(Instant::now()));
        data.insert::<DatabasePool>(database.clone());
        data.insert::<CachePool>(redis.clone());

        let http = Arc::clone(&client.http);

        let atenea_client = load_client(bot_id, redis.clone(), db, stats_db, http, language_manager.clone()).await;
        data.insert::<Atenea>(atenea_client);

    }

    let shard_manager = client.shard_manager.clone();

    tokio::spawn(async move {
        tokio::signal::ctrl_c().await.expect("Could not register ctrl+c handler");
        info!("Shutting down shard manager...");
        shard_manager.shutdown_all().await;
    });

    let c_db = database.clone();
    let c_main_db_name = client_name.replace(" ", "_");
    let c_cache = redis.clone();
    let c_shard_manager = client.shard_manager.clone();
    let c_bot_id = bot_id.clone();
    let c_lang_manager = language_manager.clone();
    let c_command_manager = command_manager.clone();

    tokio::spawn(async move {
        api::api::start_api(c_db, c_main_db_name, c_cache, c_shard_manager, c_bot_id, c_lang_manager, c_command_manager).await.expect("Failed to start the api");
    });

    if let Err(why) = client.start_autosharded().await {
        error!("An error occurred while running the client: {:?}", why);
    }

    Ok(client)
}