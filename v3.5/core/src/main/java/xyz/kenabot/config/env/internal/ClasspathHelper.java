/*
 * File:          ClasspathHelper.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\internal\ClasspathHelper.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 0efeddc0f7cd4788f5666e952165803702ed4437fe6e434738688a02e97eb12a
 *
 */


package xyz.kenabot.config.env.internal;

import xyz.kenabot.config.env.DotenvException;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Classpath helper
 */
public class ClasspathHelper {
    static Stream<String> loadFileFromClasspath(String location) {
        Class<ClasspathHelper> loader = ClasspathHelper.class;
        InputStream inputStream = loader.getResourceAsStream(location);
        if (inputStream == null) {
            inputStream = loader.getResourceAsStream(location);
        }
        if (inputStream == null) {
            inputStream = ClassLoader.getSystemResourceAsStream(location);
        }

        if (inputStream == null) {
            throw new DotenvException("Could not find "+location+" on the classpath");
        }
        Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8);
        List<String> lines = new ArrayList<>();
        while (scanner.hasNext()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();

        return lines.stream();
    }
}