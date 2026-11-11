/*
 * File:          Lyrics.java
 * Path:          v3\src\main\java\lyrics\Lyrics.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2022-10-16 19:44:27 +02:00
 *
 * SHA-256: 0763d4c3f64cb454c6551588541248d61b5906fb23313f5736e235b67f730863
 *
 */


package lyrics;

public class Lyrics
{
    private final String title, author, content, url, source;

    protected Lyrics(String title, String author, String content, String url, String source)
    {
        this.title = title;
        this.author = author;
        this.content = content;
        this.url = url;
        this.source = source;
    }

    public String getTitle()
    {
        return title;
    }
    public String getAuthor()
    {
        return author;
    }
    public String getContent()
    {
        return content;
    }
    public String getURL()
    {
        return url;
    }
    public String getSource()
    {
        return source;
    }
}
