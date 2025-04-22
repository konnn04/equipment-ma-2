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

    // Default test database configuration 
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "Abc@123";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/equipmentma2?allowMultiQueries=true";
    
    // Static connection supplier pattern like the main class
    public static Supplier<Connection> connectionProvider = JdbcUtils::getConnection;
    
    // Static connection for tests
    private static Connection testConnection;
    private static String user = DEFAULT_USER;
    private static String password = DEFAULT_PASSWORD;
    private static String dbUrl = DEFAULT_DB_URL;
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get a connection to the database
     */
    public static Connection getConn() throws SQLException {
        return connectionProvider.get();
    }

    /**
     * Configure test database connection parameters
     */
    public static void configureTestDatabase(String testUser, String testPassword, String testDbUrl) {
        user = testUser;
        password = testPassword;
        dbUrl = testDbUrl;
        // Reset connection to ensure new settings are used
        closeConnection();
    }

    /**
     * Reset to default test configuration
     */
    public static void resetConfiguration() {
        user = DEFAULT_USER;
        password = DEFAULT_PASSWORD;
        dbUrl = DEFAULT_DB_URL;
        closeConnection();
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
            Connection conn = getConnection();
            try (Statement stmt = conn.createStatement()) {
                // Disable foreign key checks temporarily
                stmt.execute("SET FOREIGN_KEY_CHECKS = 0");

                // Get all table names and drop them
                List<String> tables = getAllTables(conn);
                for (String tableName : tables) {
                    stmt.execute("DROP TABLE IF EXISTS `" + tableName + "`");
                }

                // Re-enable foreign key checks
                stmt.execute("SET FOREIGN_KEY_CHECKS = 1");
            }

            // Then reinitialize
            initializeDatabase();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to reset database", e);
        }
    }

    /**
     * Gets a list of all tables in the current database.
     */
    private static List<String> getAllTables(Connection conn) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            java.sql.ResultSet rs = stmt.executeQuery(
                    "SELECT TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA = DATABASE()");
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    /**
     * Initializes the database using the SQL script.
     */
    private static void initializeDatabase() throws SQLException {
        try (Statement stmt = testConnection.createStatement()) {
            File sqlFile = new File("src/test/resources/com/hatecode/db/db.sql");
            if (!sqlFile.exists()) {
                throw new RuntimeException("SQL file not found: " + sqlFile.getAbsolutePath());
            }
            String sql = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()));
            stmt.execute(sql);
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