/*
 * File:          DotenvReader.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\internal\DotenvReader.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: afaf9cf4cfd36bdf8387988dadfa87775d975a9b9bc92762f16ee7eccb9b9485
 *
 */


package xyz.kenabot.config.env.internal;

import xyz.kenabot.config.env.DotenvException;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * (Internal) Reads a .env file
 */
public class DotenvReader {
    private final String directory;
    private final String filename;

    /**
     * Creates a dotenv reader
     * @param directory the directory containing the .env file
     * @param filename the file name of the .env file e.g. .env
     */
    public DotenvReader(String directory, String filename) {
        this.directory = directory;
        this.filename = filename;
    }

    /**
     * (Internal) Reads the .env file
     * @return a list containing the contents of each line in the .env file
     * @throws DotenvException if a dotenv error occurs
     * @throws IOException if an I/O error occurs
     */
    public List<String> read() throws DotenvException, IOException {
        String dir = directory
                .replaceAll("\\\\", "/")
                .replaceFirst("\\.env$", "")
                .replaceFirst("/$", "");

        String location = dir + "/" + filename;
        String lowerLocation = location.toLowerCase();
        Path path = lowerLocation.startsWith("file:") || lowerLocation.startsWith("android.resource:")
                ? Paths.get(URI.create(location))
                : Paths.get(location);

        if (Files.exists(path)) {
            return Files.readAllLines(path);
        }

        try {
            return ClasspathHelper
                    .loadFileFromClasspath(location.replaceFirst("^\\./", "/"))
                    .collect(Collectors.toList());
        } catch (DotenvException e) {
            Path cwd = FileSystems.getDefault().getPath(".").toAbsolutePath().normalize();
            String cwdMessage = !path.isAbsolute() ? "(working directory: " + cwd + ")" : "";
            e.addSuppressed(new DotenvException("Could not find " + path + " on the file system " + cwdMessage));
            throw e;
        }
    }
}