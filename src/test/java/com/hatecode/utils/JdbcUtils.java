package com.hatecode.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class JdbcUtils {

    // H2 in-memory database configuration
    private static final String DEFAULT_USER = "sa";
    private static final String DEFAULT_PASSWORD = "";
    private static final String DEFAULT_DB_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MYSQL;";

    // Static connection supplier pattern like the main class
    public static Supplier<Connection> connectionProvider = JdbcUtils::getConnection;
    
    // Static connection for tests
    private static Connection testConnection;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;
    private static String dbUrl = DEFAULT_DB_URL;
    public static String fileName = "db";
    
    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get a connection to the database - matches main version's interface
     */
    public static Connection getConn() throws SQLException {
        return connectionProvider.get();
    }
    
    /**
     * Cung cấp kết nối trực tiếp đến database test.
     * Phương thức này tránh đệ quy khi được gọi từ TestDatabaseConfig.
     */
    public static Connection getDirectTestConnection() {
        return getConnection();
    }

    /**
     * Returns a connection to the test database.
     * Creates a new connection if one doesn't exist.
     */
    private static Connection getConnection() {
        try {
            if (testConnection == null || testConnection.isClosed()) {
                testConnection = DriverManager.getConnection(dbUrl, user, password);
                initializeDatabase();
            }
            return testConnection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    /**
     * Resets the database to its initial state.
     */
    public static void resetDatabase() {
        try {
            closeConnection();
            Connection conn = DriverManager.getConnection(dbUrl, user, password);
            testConnection = conn;
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("DROP ALL OBJECTS");
            }
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset database", e);
        }
    }

    /**
     * Initializes the database using the SQL script.
     */
    private static void initializeDatabase() throws SQLException {
        try {
            File sqlFile = new File("src/test/resources/com/hatecode/db/" + fileName +".sql");
            if (!sqlFile.exists()) {
                throw new RuntimeException("SQL file not found: " + sqlFile.getAbsolutePath());
            }
            String sql = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()));
    
            // Split script into individual statements
            String[] statements = sql.split(";");
    
            try (Statement stmt = testConnection.createStatement()) {
                for (String statement : statements) {
                    if (statement.trim().isEmpty()) continue;
    
                    try {
                        stmt.execute(statement.trim());
                    } catch (SQLException e) {
                        // Skip errors about constraints or duplicates
                        if (e.getMessage().contains("already exists") 
                            || e.getMessage().contains("violation")
                            || e.getMessage().contains("duplicate")) {
                            // Xử lý các vi phạm ràng buộc - bỏ qua lỗi
                            continue;
                        }
                        throw e;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    /**
     * Closes the database connection.
     * Should be called when tests are completed.
     */
    public static void closeConnection() {
        try {
            if (testConnection != null && !testConnection.isClosed()) {
                testConnection.close();
                testConnection = null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }
}