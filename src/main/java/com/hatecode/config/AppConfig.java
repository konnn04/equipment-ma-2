package com.hatecode.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.time.LocalDate;

import static com.hatecode.utils.FormatDate.DATE_FORMATTER;

public class AppConfig {
    private static final Dotenv dotenv = Dotenv.load();

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

    // Number day to warn before maintenance
    public static final LocalDate MIN_DATE = LocalDate.parse("01/01/2020", DATE_FORMATTER);
    public static final LocalDate MAX_DATE = LocalDate.now().plusYears(1);

    // Search delay for input
    public static final int SEARCH_DELAY_MS = 500;

    // Window size
    public static final int DEFAULT_WINDOW_WIDTH = 800;
    public static final int DEFAULT_WINDOW_HEIGHT = 600;

    // Maintenance status check interval
    public static final long MAINTENANCE_STATUS_CHECK_INTERVAL = 900000; // 15 minutes
    // Maintenance check interval: 1 hour
    public static final long MAINTENANCE_CHECK_INTERVAL = 3600000; // 1 hour 

    // Maintenance duration limits
    public static final int MAX_MAINTENANCE_DURATION_DAYS = 30;
    public static final int DEFAULT_MAINTENANCE_INTERVAL_DAYS = 30; // // Default interval for maintenance scheduling

    // Login attempts
    public static final int MAX_LOGIN_ATTEMPTS = 5;

    // Notification refresh interval
    public static final long NOTIFICATION_REFRESH_INTERVAL = 300000; // 5 minutes

    // Email configuration
    public static final String EMAIL_SMTP_HOST = "smtp.gmail.com";
    public static final int EMAIL_SMTP_PORT = 587;
    public static final String EMAIL_FROM_NAME = "Equipment Management System";

    public static final int MIN_DAY_MAINTENANCE = 1;
    public static final int MAX_DAY_MAINTENANCE = 30;

    public static final int WARNING_TIME_BEFORE_MAIL_MAINTENANCE = 24; // 24 hours
}
