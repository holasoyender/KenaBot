/*
 * File:          LyricsClient.java
 * Path:          v3\src\main\java\lyrics\LyricsClient.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-01-15 14:28:06 +01:00
 *
 * SHA-256: a63477211bc496e93167e366f3485f092eb6e8390da407851f47226a56c4cdc9
 *
 */


package lyrics;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

public class LyricsClient
{
    private final Config config = ConfigFactory.load();
    private final HashMap<String, Lyrics> cache = new HashMap<>();
    private final OutputSettings noPrettyPrint = new OutputSettings().prettyPrint(false);
    private final Safelist newlineSafelist = Safelist.none().addTags("br", "p");
    private final Executor executor;
    private final String defaultSource, userAgent;
    private final int timeout;
    public LyricsClient() { this(null, null); }
    public LyricsClient(String defaultSource)
    {
        this(defaultSource, null);
    }
    public LyricsClient(String defaultSource, Executor executor)
    {
        this.defaultSource = defaultSource == null ? config.getString("lyrics.default") : defaultSource;
        this.userAgent = config.getString("lyrics.user-agent");
        this.timeout = config.getInt("lyrics.timeout");
        this.executor = executor == null ? Executors.newCachedThreadPool() : executor;
    }
    public CompletableFuture<Lyrics> getLyrics(String search)
    {
        return getLyrics(search, defaultSource);
    }
    public CompletableFuture<Lyrics> getLyrics(String search, String source)
    {
        String cacheKey = source + "||" + search;
        if(cache.containsKey(cacheKey))
            return CompletableFuture.completedFuture(cache.get(cacheKey));
        try
        {
            CompletableFuture<String> futureToken;
            boolean jsonSearch = config.getBoolean("lyrics." + source + ".search.json");
            String select = config.getString("lyrics." + source + ".search.select");
            String titleSelector = config.getString("lyrics." + source + ".parse.title");
            String authorSelector = config.getString("lyrics." + source + ".parse.author");
            String contentSelector = config.getString("lyrics." + source + ".parse.content");

            if (config.hasPath("lyrics." + source + ".token")) {
                futureToken = getToken(source);
            } else {
                futureToken = CompletableFuture.completedFuture("");
            }

            assert futureToken != null;
            return futureToken.thenCompose(token -> {
                String searchUrl = String.format(config.getString("lyrics." + source + ".search.url"), search, token);

                return CompletableFuture.supplyAsync(() ->
                {
                    try
                    {
                        Document doc;
                        Connection connection = Jsoup.connect(searchUrl).userAgent(userAgent).timeout(timeout);
                        if(jsonSearch)
                        {
                            String body = connection.ignoreContentType(true).execute().body();
                            JSONObject json = new JSONObject(body);
                            doc = Jsoup.parse(XML.toString(json));
                        }
                        else
                            doc = connection.get();

                        Element urlElement = doc.selectFirst(select);
                        String url;
                        assert urlElement != null;
                        if(jsonSearch) {
                            url = urlElement.text();
                        }
                        else {
                            url = urlElement.attr("abs:href");
                        }
                        if(url.isEmpty())
                            return null;

                        doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();

                        Lyrics lyrics = new Lyrics(Objects.requireNonNull(doc.selectFirst(titleSelector)).ownText(),
                                Objects.requireNonNull(doc.selectFirst(authorSelector)).ownText(),
                                Objects.requireNonNull(doc.selectFirst(contentSelector)).wholeText().replaceAll("\n\n", "\n"),
                                url,
                                source);
                        cache.put(cacheKey, lyrics);
                        return lyrics;
                    }
                    catch(IOException | NullPointerException | JSONException ex)
                    {
                        return null;
                    }
                }, executor);
            });
        }
        catch(ConfigException ex)
        {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        }
        catch(Exception ignored)
        {
            return null;
        }
    }

    private CompletableFuture<String> getToken(String source) {
        try {
            String tokenUrl = config.getString("lyrics." + source + ".token.url");
            String select = config.getString("lyrics." + source + ".token.select");
            boolean textSearch = config.getBoolean("lyrics." + source + ".token.text");

            return CompletableFuture.supplyAsync(() -> {
                try {
                    Pattern pattern = null;
                    if (config.hasPath("lyrics." + source + ".token.regex")) {
                        String regexPattern = config.getString("lyrics." + source + ".token.regex");
                        pattern = Pattern.compile(regexPattern);
                    }

                    Connection connection = Jsoup.connect(tokenUrl).userAgent(userAgent).timeout(timeout);
                    String body;

                    if (textSearch) {
                        body = connection.ignoreContentType(true).execute().body();
                    } else {
                        Document doc = connection.get();
                        body = Objects.requireNonNull(doc.selectFirst(select)).ownText();
                    }

                    if (pattern != null) {
                        Matcher matcher = pattern.matcher(body);
                        if (matcher.find()) {
                            return matcher.group();
                        }
                    }
                    return null;
                } catch (IOException | NullPointerException ex) {
                    return null;
                }
            }, executor);
        } catch (ConfigException ex) {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String cleanWithNewlines(Element element)
    {
        return Jsoup.clean(Jsoup.clean(element.html(), newlineSafelist), "", Safelist.none(), noPrettyPrint);
    }
}
