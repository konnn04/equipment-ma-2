package com.hatecode.utils;

import com.hatecode.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcUtils {
    private static Connection testConnection;

    public static void overrideConnectionForTesting(Connection conn) {
        testConnection = conn;
    }

    public static void resetTestConnection() {
        testConnection = null;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConn() throws SQLException {
        if(testConnection != null){
            return testConnection;
        }
        return DriverManager.getConnection(
                AppConfig.DB_URL,
                AppConfig.DB_USER,
                AppConfig.DB_PASS
        );
    }
}