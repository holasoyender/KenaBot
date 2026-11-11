/*
 * File:          DotenvBuilder.java
 * Path:          v3.5\core\src\main\java\xyz\kenabot\config\env\DotenvBuilder.java
 * ============================================================
 *
 * Copyright (c) 2026 holasoyender
 * All rights reserved.
 *
 * Last modified: 2023-10-08 19:28:27 +02:00
 *
 * SHA-256: 899097f47dc0da3a64bdcb4fc4b8173143d5eddee1c22810365a16475356570d
 *
 */


package xyz.kenabot.config.env;

import xyz.kenabot.config.env.internal.DotenvParser;
import xyz.kenabot.config.env.internal.DotenvReader;

import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Builds and loads and {@link Dotenv} instance.
 * @see Dotenv#configure()
 */
public class DotenvBuilder {
    private String filename = ".env";
    private boolean throwIfMissing = true;
    private boolean throwIfMalformed = true;

    /**
     * Sets the name of the .env file. The default is .env.
     * @param name the filename
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder filename(String name) {
        filename = name;
        return this;
    }

    /**
     * Does not throw an exception when .env is missing.
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder ignoreIfMissing() {
        throwIfMissing = false;
        return this;
    }

    /**
     * Does not throw an exception when .env is malformed.
     * @return this {@link DotenvBuilder}
     */
    public DotenvBuilder ignoreIfMalformed() {
        throwIfMalformed = false;
        return this;
    }

    /**
     * Load the contents of .env into the virtual environment.
     * @return a new {@link Dotenv} instance
     * @throws DotenvException when an error occurs
     */
    public Dotenv load() throws DotenvException {
        String directoryPath = "./";
        DotenvParser reader = new DotenvParser(
                new DotenvReader(directoryPath, filename),
                throwIfMissing,
                throwIfMalformed);
        List<DotenvEntry> env = reader.parse();
        return new DotenvImpl(env);
    }

    static class DotenvImpl implements Dotenv {
        private final Map<String, String> envVars;

        public DotenvImpl(List<DotenvEntry> envVars) {
            Map<String, String> envVarsInFile = envVars.stream()
                    .collect(toMap(DotenvEntry::key, DotenvEntry::value));
            this.envVars = new HashMap<>(envVarsInFile);
            this.envVars.putAll(System.getenv());

            Set<DotenvEntry> set = this.envVars.entrySet().stream()
                    .map(it -> new DotenvEntry(it.getKey(), it.getValue()))
                    .collect(collectingAndThen(toSet(), Collections::unmodifiableSet));

        }

        @Override
        public String get(String key) {
            String value = System.getenv(key);
            return value != null ? value : envVars.get(key);
        }

        @Override
        public String get(String key, String defaultValue) {
            String value = this.get(key);
            return value != null ? value : defaultValue;
        }
    }
}