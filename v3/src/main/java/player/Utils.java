/*
 * File:          Utils.java
 * Path:          v3\src\main\java\player\Utils.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-09-17 13:48:03 +02:00
 *
 * SHA-256: cfc67f13d7e213d5a2736e46609212042a8684abfddbc2ac1c5d4861cfea67b0
 *
 */


package player;

import org.apache.http.HttpRequest;

public class Utils {
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/105.0.0.0 Safari/537.36";

    public static void fakeChrome(HttpRequest request) {
        request.setHeader("Connection", "keep-alive");
        request.setHeader("DNT", "1");
        request.setHeader("Upgrade-Insecure-Requests", "1");
        request.setHeader("Accept", "*/*");
//        request.setHeader("Accept-Encoding", "gzip, deflate, br");
        request.setHeader("Accept-Encoding", "none");
        request.setHeader("Accept-Language", "en-US,en;q=0.9");
        request.setHeader("Sec-Ch-Ua", "\" Not;A Brand\";v=\"99\", \"Google Chrome\";v=\"97\", \"Chromium\";v=\"97\"");
        request.setHeader("Sec-Ch-Ua-Mobile", "?0");
        request.setHeader("Sec-Fetch-Dest", "document");
        request.setHeader("Sec-Fetch-Mode", "no-cors");
        request.setHeader("Sec-Fetch-Site", "cross-site");
        request.setHeader("User-Agent", USER_AGENT);
    }

}
