/*
 * File:          HttpManager.kt
 * Path:          v3\src\main\kotlin\thirdparty\genius\core\HttpManager.kt
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-09-17 14:43:07 +02:00
 *
 * SHA-256: 76fe037f3c40b22f6fa999f6211d5d6b0977d5ba6bbff9d3da3776d020f8469d
 *
 */


package thirdparty.genius.core

import io.sentry.Sentry
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class HttpManager {
    @Throws(IOException::class)
    fun getConnection(url: URL): HttpURLConnection {
        val con = url.openConnection() as HttpURLConnection
        con.setRequestProperty("User-Agent", Statics.USER_AGENT)
        con.setRequestProperty("accept-language", "en-US")
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Accept", "application/json")
        con.setRequestProperty("Connection", "keep-alive")
        con.setRequestProperty("DNT", "1")
        con.setRequestProperty("Upgrade-Insecure-Requests", "1")
        con.setRequestProperty("Accept-Encoding", "none")
        con.setRequestProperty("Sec-Ch-Ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"105\", \"Chromium\";v=\"105\"")
        con.setRequestProperty("Sec-Ch-Ua-Mobile", "?0")
        con.setRequestProperty("Sec-Fetch-Dest", "document")
        con.setRequestProperty("Sec-Fetch-Mode", "no-cors")
        con.setRequestProperty("Sec-Fetch-Site", "cross-site")
        return con
    }

    fun executeGet(con: HttpURLConnection): String? {
        return try {
            con.requestMethod = "GET"
            val `in` = BufferedReader(InputStreamReader(con.inputStream))
            var inputLine: String?
            val response = StringBuilder()
            while (`in`.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }
            `in`.close()
            response.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            Sentry.captureException(e)
            null
        }
    }

}