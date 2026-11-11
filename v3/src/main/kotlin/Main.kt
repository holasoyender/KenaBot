/*
 * File:          Main.kt
 * Path:          v3\src\main\kotlin\Main.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-06-29 21:43:39 +02:00
 *
 * SHA-256: 727b05610e27b097deead1ecba2525bdf56e206c177d85f8c4d743ef45e20dbc
 *
 */


import atenea.NodeManager
import atenea.NodeManager.nodeManager
//import club.minnced.discord.webhook.WebhookClient
//import club.minnced.discord.webhook.exception.HttpException
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException
import config.Env
import enums.States
import events.*
import functions.LastState
import io.sentry.Sentry
import managers.CommandManager
import managers.DevCommandManager
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.exceptions.ErrorResponseException
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException
import net.dv8tion.jda.api.exceptions.PermissionException
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.requests.RestAction
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder
import net.dv8tion.jda.api.sharding.ShardManager
import net.dv8tion.jda.api.utils.ChunkingFilter
import net.dv8tion.jda.api.utils.Compression
import net.dv8tion.jda.api.utils.MemberCachePolicy
import net.dv8tion.jda.api.utils.cache.CacheFlag
import net.dv8tion.jda.api.utils.messages.MessageRequest
import org.slf4j.LoggerFactory
import services.ServicesController
import thirdparty.errors.Error
import javax.print.attribute.standard.Severity
import javax.security.auth.login.LoginException


object Main {

    var cmdArgs: Array<String> = emptyArray()
    var shards: ShardManager? = null

    @JvmStatic
    fun main(args: Array<String>) {

        cmdArgs = args
        val builder = DefaultShardManagerBuilder.createDefault(null)
        val logger = LoggerFactory.getLogger("Main")

        CommandManager()
        DevCommandManager()
        NodeManager.load()

        MessageRequest.setDefaultMentionRepliedUser(false)
        RestAction.setDefaultFailure { error ->
            if (error is PermissionException) {
                logger.error("Error de permisos (Ignored)")
            }
            if (error is InsufficientPermissionException) {
                logger.error("Error de permisos (Ignored)")
            }

            if(error !is ErrorResponseException && error !is FriendlyException) {
                error.printStackTrace()
                val e = Error(
                    "RestError",
                    Severity.ERROR,
                    error as Exception
                )
                e.push()
            }

            //error.printStackTrace()
        }

        /*WebhookClient.setDefaultErrorHandler { client, _, throwable ->
            throwable?.printStackTrace()

            if (throwable is HttpException && throwable.code.toString() === "404") {
                client.close()
            }
        }*/

        builder.disableCache(
            CacheFlag.EMOJI,
            CacheFlag.ACTIVITY,
            CacheFlag.MEMBER_OVERRIDES,
            CacheFlag.CLIENT_STATUS,
            CacheFlag.ONLINE_STATUS,
            CacheFlag.FORUM_TAGS,
            CacheFlag.ROLE_TAGS,
            CacheFlag.SCHEDULED_EVENTS,
            CacheFlag.STICKER
        )
        builder.enableCache(
            CacheFlag.VOICE_STATE
        )
        builder.setCompression(Compression.ZLIB)
        builder.setActivity(Activity.watching("kenabot.xyz"))

        builder.setToken(Env.TOKEN!!)

        builder.setEventPassthrough(true)

        builder.addEventListeners(
            Interactions(),
            Internal(),
            Message(),
            Guilds(),
            Buttons(),
            Menus(),
            Voice(),
            nodeManager
        )

        builder.setVoiceDispatchInterceptor(nodeManager.voiceInterceptor)

        builder.setMemberCachePolicy(MemberCachePolicy.VOICE/*.or(MemberCachePolicy.NONE)*/)
        builder.setChunkingFilter(ChunkingFilter.NONE)

        builder.disableIntents(
            GatewayIntent.DIRECT_MESSAGE_REACTIONS,
            GatewayIntent.DIRECT_MESSAGE_TYPING,
            GatewayIntent.GUILD_MESSAGE_TYPING,
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_INVITES,
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MODERATION,
            GatewayIntent.GUILD_WEBHOOKS,
            GatewayIntent.SCHEDULED_EVENTS,
            GatewayIntent.MESSAGE_CONTENT,
            GatewayIntent.AUTO_MODERATION_CONFIGURATION,
            GatewayIntent.AUTO_MODERATION_EXECUTION,
        )
        builder.enableIntents(
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.DIRECT_MESSAGES
        )
        builder.setLargeThreshold(50)

        builder.setShardsTotal(-1)

        try {
            val shardManager = builder.build()
            shards = shardManager

            ServicesController.start(shardManager)

            if (Env.SENTRY_DNS != null) {
                Sentry.init { options ->
                    options.dsn = Env.SENTRY_DNS
                    options.tracesSampleRate = 1.0
                    options.isDebug = false
                    options.isAttachStacktrace = true
                    options.tags["bot"] = shardManager.getShardById(0)?.selfUser?.name ?: "Shard 0"
                }
                logger.info("Conectado con Sentry")
            }

            LastState.setLastState(States.READY)
            LastState.setReadyAt(System.currentTimeMillis())

        } catch (e: LoginException) {
            e.printStackTrace()
            Sentry.captureException(e)
        }

        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                logger.info("Shutting down services, please wait...")
                shards!!.shutdown()
            }
        })
    }
}