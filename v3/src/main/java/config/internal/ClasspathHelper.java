/*
 * File:          ClasspathHelper.java
 * Path:          v3\src\main\java\config\internal\ClasspathHelper.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-03-24 23:10:19 +01:00
 *
 * SHA-256: 60a15de638254d0f649b7bfd070cdba648407a1f5fea0a5c56e76d68357b6097
 *
 */


package config.internal;

import config.DotenvException;

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
        return lines.stream();
    }
}