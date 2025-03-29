package com.hatecode.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import com.hatecode.config.DBConfig;

public class JdbcUtils {
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(
                DBConfig.DB_URL,
                DBConfig.USER,
                DBConfig.PASS
        );
    }
}