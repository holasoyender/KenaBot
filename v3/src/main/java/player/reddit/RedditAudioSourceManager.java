/*
 * File:          RedditAudioSourceManager.java
 * Path:          v3\src\main\java\player\reddit\RedditAudioSourceManager.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-10-10 17:32:19 +02:00
 *
 * SHA-256: ae0a9e9459e831d624c26ff2c7456c8289a94bb98f6fefe4e1f7d424d79cd5a2
 *
 */


package player.reddit;

import org.jetbrains.annotations.Nullable;
import player.AbstractHttpSource;
import player.AudioTrackInfoWithImage;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.JsonBrowser;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.track.AudioItem;
import com.sedmelluq.discord.lavaplayer.track.AudioReference;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static player.Utils.USER_AGENT;
import static com.sedmelluq.discord.lavaplayer.tools.JsonBrowser.NULL_BROWSER;
import static player.reddit.RedditAudioTrack.getPlaybackUrl;

public class RedditAudioSourceManager extends AbstractHttpSource {
    private static final Pattern FULL_LINK_REGEX = Pattern.compile("https://(?:www|old)\\.reddit\\.com/r/[^/]+/[^/]+/([^/]+)(?:/?(?:[^/]+)?/?)?");
    private static final Pattern VIDEO_LINK_REGEX = Pattern.compile("https://v\\.redd\\.it/([^/]+)(?:.*)?");

    public RedditAudioSourceManager() {
        this.configureBuilder(
            (builder) -> builder.setUserAgent(USER_AGENT)
        );
    }

    @Override
    public String getSourceName() {
        return "reddit";
    }

    @Override
    public AudioItem loadItem(AudioPlayerManager manager, AudioReference reference) {
        final String identifier = reference.identifier;
        final Matcher fullLink = FULL_LINK_REGEX.matcher(identifier);

        if (fullLink.matches()) {
            final String group = fullLink.group(fullLink.groupCount());
            final JsonBrowser data = this.fetchJson(group);

            return this.buildTrack(data, identifier);
        }

        final Matcher videoLink = VIDEO_LINK_REGEX.matcher(identifier);

        // If we have a short video link we firstly need to follow all redirects
        if (videoLink.matches()) {
            // Once we have the link we can extract the post id and build the track the normal way
            final String actualRedditUrl = this.fetchRedirectUrl(identifier);
            final String id = this.getPostId(actualRedditUrl);
            final JsonBrowser data = this.fetchJson(id);

            return this.buildTrack(data, actualRedditUrl);
        }

        return null;
    }

    @Override
    public boolean isTrackEncodable(AudioTrack track) {
        return true;
    }

    @Override
    public void encodeTrack(AudioTrack track, DataOutput output) {
        // nothing to encode
    }

    @Override
    public AudioTrack decodeTrack(AudioTrackInfo trackInfo, DataInput input) {
        return new RedditAudioTrack(trackInfo, this);
    }

    private String getPostId(String url) {
        final Matcher matcher = FULL_LINK_REGEX.matcher(url);

        if (matcher.matches()) {
            return matcher.group(matcher.groupCount());
        }

        return url;
    }

    private String fetchRedirectUrl(String vRedditUrl) {
        final HttpGet httpGet = new HttpGet(vRedditUrl);
        final HttpInterface httpInterface = this.getHttpInterface();

        // Follow all redirects until there are no more to follow and return that
        try (final CloseableHttpResponse ignored = httpInterface.execute(httpGet)) {
            return httpInterface.getFinalLocation().toString();
        }
        catch (IOException e) {
            return null;
            //throw ExceptionTools.wrapUnfriendlyExceptions("Could not load data from reddit", COMMON, e);
        }
    }

    @Nullable
    private JsonBrowser fetchJson(String pageURl) {
        final HttpGet httpGet = new HttpGet("https://api.reddit.com/api/info/?id=t3_" + pageURl);

        try (final CloseableHttpResponse response = this.getHttpInterface().execute(httpGet)) {
            final String content = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
            final JsonBrowser child = JsonBrowser.parse(content).get("data").get("children").index(0);

            // If we have nothing in the children array we can safely return null
            if (child.equals(NULL_BROWSER)) {
                return null;
            }

            return child.get("data");
        }
        catch (IOException e) {
            return null;
            //throw ExceptionTools.wrapUnfriendlyExceptions("Could not load data from reddit", COMMON, e);
        }
    }

    private boolean canPlayAudio(String id) {
        final HttpGet httpGet = new HttpGet(getPlaybackUrl(id));

        // Probe the audio and check the response code, if it is 200 we have some audio
        try (final CloseableHttpResponse response = this.getHttpInterface().execute(httpGet)) {
            return response.getStatusLine().getStatusCode() == 200;
        }
        catch (IOException e) {
            return false;
        }
    }

    private AudioItem buildTrack(@Nullable JsonBrowser data, String pageURl) {
        // If we don't have any data we can return null
        if (data == null) {
            return AudioReference.NO_TRACK;
        }

        final String postHint = data.get("post_hint").safeText();

        if (!"hosted:video".equals(postHint)) {
            return AudioReference.NO_TRACK;
        }

        final JsonBrowser media = data.get("media").get("reddit_video");
        final String url = data.get("url").safeText();

        if (media.get("is_gif").asBoolean(false)) {
            return AudioReference.NO_TRACK;
        }

        final Matcher videoLink = VIDEO_LINK_REGEX.matcher(url);

        if (!videoLink.matches()) {
            return AudioReference.NO_TRACK;
        }

        final String videoId = videoLink.group(videoLink.groupCount());

        if (!this.canPlayAudio(videoId)) {
            return AudioReference.NO_TRACK;
        }

        return new RedditAudioTrack(
            new AudioTrackInfoWithImage(
                data.get("title").safeText(),
                "u/" + data.get("author").safeText(),
                media.get("duration").asLong(1) * 1000,
                videoId,
                false,
                pageURl
            ),
            this
        );
    }
}
