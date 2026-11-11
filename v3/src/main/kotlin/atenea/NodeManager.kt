/*
 * File:          NodeManager.kt
 * Path:          v3\src\main\kotlin\atenea\NodeManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-04-14 18:48:17 +02:00
 *
 * SHA-256: 27233dac5244a8f57ed10c4da08bf8f2f2cc108661802826d9f6875c4b0509cb
 *
 */


package atenea

import Main.shards
import atenea.driver.io.jda.JdaAtenea
import enums.NodeState
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI


object NodeManager {

    lateinit var nodeManager: JdaAtenea
    private val logger = LoggerFactory.getLogger(NodeManager::class.java)

    private var nodes: MutableList<AteneaNode> = mutableListOf()

    fun load() {

        nodes = try {
            val file = javaClass
                .classLoader
                .getResourceAsStream("keys/nodes.json") ?: throw IllegalArgumentException("Nodes file not found")

            val rawObj = fileContent(file)

            val obj = JSONObject(rawObj)

            val nodes = obj.getJSONArray("nodes")
            val nodesList = nodes.map { node ->
                val nodeObj = node as JSONObject

                val host = try {
                    val rawHost = nodeObj.getString("host")
                    if (!rawHost.startsWith("ws://") && !rawHost.startsWith("wss://")) {
                        "ws://$rawHost"
                    } else {
                        rawHost
                    }
                } catch (e: Exception) {
                    return@map null
                }

                val password = try {
                    nodeObj.getString("password")
                } catch (e: Exception) {
                    return@map null
                }

                val region = try {
                    nodeObj.getString("region")
                } catch (e: Exception) {
                    "eu-central"
                }

                val name = try {
                    nodeObj.getString("name")
                } catch (e: Exception) {
                    "AteneaNode #${nodes.indexOf(node)}"
                }

                AteneaNode(
                    host,
                    password,
                    region,
                    name,
                    NodeState.UNKNOWN
                )
            }

            nodesList.filterNotNull().toMutableList()
        } catch (e: IOException) {
            logger.error("Error loading nodes file")
            mutableListOf()
        }

        val manager = JdaAtenea(
            1,
        ) { shardId ->
            shards!!.getShardById(shardId)
        }

        nodeManager = manager
    }

    fun loadNodes(link: JdaAtenea, botName: String) {
        var i = 0
        for (node in nodes) {
            link.addNode(node.name, URI(node.host), node.password, botName)
            i++
        }
        logger.info("Se han conectado $i nodo(s) remotos")
    }

    @Throws(IOException::class)
    private fun fileContent(`is`: InputStream): String {
        var lines = ""
        InputStreamReader(`is`).use { isr ->
            BufferedReader(isr).use { br ->
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    lines += line
                }
                `is`.close()
            }
        }
        return lines
    }

}