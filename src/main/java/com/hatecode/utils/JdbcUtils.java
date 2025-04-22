package com.hatecode.utils;

import com.hatecode.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

public class JdbcUtils {
    // Add connection supplier pattern to match test version
    public static Supplier<Connection> connectionProvider = JdbcUtils::getConnection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    // Keep private implementation
    private static Connection getConnection() {
        try {
            return DriverManager.getConnection(
                    AppConfig.DB_URL,
                    AppConfig.DB_USER,
                    AppConfig.DB_PASS
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    } 
    
    // Add public interface matching test version
    public static Connection getConn() throws SQLException {
        return connectionProvider.get();
    }

    public static void resetDatabase() {
        
    }

    public static void closeConnection() {

    }

    public static Connection getDirectTestConnection() {
        return getConnection();
    }
}