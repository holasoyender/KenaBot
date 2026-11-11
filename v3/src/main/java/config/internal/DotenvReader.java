/*
 * File:          DotenvReader.java
 * Path:          v3\src\main\java\config\internal\DotenvReader.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: a656ee93cf1ef47f85dbd61475ebc5e0d8b355da4f4d3c231d804cb2d0939fe4
 *
 */


package config.internal;


import config.DotenvException;

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