package com.hatecode.config;

import io.github.cdimascio.dotenv.Dotenv;

public class AppConfig {
    private static final Dotenv dotenv = Dotenv.load();
    public static final Integer PAGE_SIZE = 15;

    // Cloudinary configuration
    public static final String CLOUD_NAME = dotenv.get("CLOUD_NAME");
    public static final String API_KEY = dotenv.get("API_KEY");
    public static final String API_SECRET = dotenv.get("API_SECRET");

    // Database configuration
    public static final String DB_URL = dotenv.get("DB_URL");
    public static final String DB_USER = dotenv.get("DB_USER");
    public static final String DB_PASS = dotenv.get("DB_PASS");
}
