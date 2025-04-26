package com.hatecode.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;

import static com.hatecode.utils.FormatDate.DATE_FORMATTER;

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

    // Email configuration
    public static final String EMAIL_USERNAME = dotenv.get("EMAIL_USERNAME");
    public static final String EMAIL_PASSWORD = dotenv.get("EMAIL_PASSWORD");

    // Number day to warn before maintenance
    public static final int NUMBER_DAY_TO_WARN_BEFORE_MAINTENANCE = 3;
    // Number day to warn before maintenance

    public static final LocalDate MIN_DATE = LocalDate.parse("01/01/2020", DATE_FORMATTER);
    public static final LocalDate MAX_DATE = LocalDate.now().plusYears(1);
}
