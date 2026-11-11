/*
 * File:          TikTokAudioTrack.java
 * Path:          v3\src\main\java\player\tiktok\TikTokAudioTrack.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-12-01 16:41:49 +01:00
 *
 * SHA-256: 0fab447be3bd413595026f1d8f5116cbbed70715fe654a24ee9b336313523ad8
 *
 */


package player.tiktok;

import player.AbstractHttpSource;
import player.MpegTrack;
import player.Pair;
import com.sedmelluq.discord.lavaplayer.container.mp3.Mp3AudioTrack;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.tools.io.HttpInterface;
import com.sedmelluq.discord.lavaplayer.tools.io.SeekableInputStream;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.track.InternalAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.playback.LocalAudioTrackExecutor;

import static com.sedmelluq.discord.lavaplayer.tools.FriendlyException.Severity.SUSPICIOUS;

public class TikTokAudioTrack extends MpegTrack {
    private final TikTokAudioTrackHttpManager httpManager = new TikTokAudioTrackHttpManager();
    private Pair<String, String> urlCache = null;
    private boolean failedOnce = false;

    public TikTokAudioTrack(AudioTrackInfo trackInfo, AbstractHttpSource manager) {
        super(trackInfo, manager);
    }

    @Override
    public String getPlaybackUrl() {
        try {
            if (this.urlCache == null) {
                this.urlCache = loadPlaybackUrl();
            }

            if (this.failedOnce) {
                return this.urlCache.right();
            }

            return this.urlCache.left();
        } catch (Exception e) {
            throw new FriendlyException("Could not load TikTok video", SUSPICIOUS, e);
        }
    }

    @Override
    public void process(LocalAudioTrackExecutor executor) throws Exception {
        this.httpManager.loadCookies();

        try (HttpInterface httpInterface = this.getHttpInterface()) {
            loadStream(executor, httpInterface);
        }
    }

    @Override
    protected void loadStream(LocalAudioTrackExecutor localExecutor, HttpInterface httpInterface) throws Exception {
        try {
            super.loadStream(localExecutor, httpInterface);
        } catch (Exception e) {
            if (this.failedOnce) {
                throw e;
            }

            this.failedOnce = true;
            super.loadStream(localExecutor, httpInterface);
        }
    }

    /*@Override
    protected void loadStream(LocalAudioTrackExecutor localExecutor, HttpInterface httpInterface) throws Exception {
        final String trackUrl = getPlaybackUrl();
        log.debug("Starting {} track from URL: {}", getSourceManager().getSourceName(), trackUrl);
        // Setting contentLength (last param) to null makes it default to Long.MAX_VALUE
        try (final PersistentHttpStream stream = new PersistentHttpStream(httpInterface, new URI(trackUrl), this.getTrackDuration())) {
            // dump the stream
            Files.copy(
                stream,
                new File("DUMP.raw").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
            processDelegate(createAudioTrack(this.trackInfo, stream), localExecutor);
        }
    }*/

    protected Pair<String, String> loadPlaybackUrl() throws Exception {

        final TikTokAudioSourceManager.MetaData metdata = this.getSourceManager().extractData(
                this.trackInfo.author,
                this.trackInfo.identifier
        );

        return new Pair<>(
                metdata.videoUrl,
                metdata.musicUrl
        );
    }

    /*protected MetaData extractFromJson(String url) throws IOException {
        final Matcher matcher = VIDEO_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new IOException("Url does not match tiktok anymore? wtf");
        }
        final String user = matcher.group("user");
        final String video = matcher.group("video");
        final HttpGet httpGet = new HttpGet(
            "https://www.tiktok.com/node/share/video/@" + user + '/' + video
        );
        fakeChrome(httpGet);
        try (final HttpInterface httpInterface = this.httpManager.getHttpInterface()) {
            try (final CloseableHttpResponse response = httpInterface.execute(httpGet)) {
                final int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    if (statusCode == 302) { // most likely a 404
                        return null;
                    }
                    throw new IOException("Unexpected status code for video page response: " + statusCode);
                }
                final String string = IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8);
                final JsonBrowser json = JsonBrowser.parse(string);
                final JsonBrowser base = json.get("itemInfo").get("itemStruct");
                return getMetaData(url, base);
            }
        }
    }*/

    @Override
    protected InternalAudioTrack createAudioTrack(AudioTrackInfo trackInfo, SeekableInputStream stream) {
        if (this.failedOnce && this.urlCache.right().contains(".mp3")) {
            return new Mp3AudioTrack(trackInfo, stream);
        }

        return super.createAudioTrack(trackInfo, stream);
    }

    @Override
    protected HttpInterface getHttpInterface() {
        return this.httpManager.getHttpInterface();
    }

    @Override
    public TikTokAudioSourceManager getSourceManager() {
        return (TikTokAudioSourceManager) super.getSourceManager();
    }

    @Override
    protected AudioTrack makeShallowClone() {
        return new TikTokAudioTrack(this.trackInfo, getSourceManager());
    }
}
