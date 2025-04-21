package com.hatecode.utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class TestDBUtils {

    private static final String USER = "sa";
    private static final String PASS = "";

    public static Connection createIsolatedConnection() throws SQLException {
        String dbName = "testdb_" + UUID.randomUUID();
        String dbUrl = "jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1";
        Connection conn = DriverManager.getConnection(dbUrl, USER, PASS);
        initializeDatabase(conn);
        return conn;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            File sqlFile = new File("src/test/resources/com/hatecode/db/db.sql");
            if (!sqlFile.exists()) {
                throw new RuntimeException("SQL file not found: " + sqlFile.getAbsolutePath());
            }
            String sql = new String(java.nio.file.Files.readAllBytes(sqlFile.toPath()));
            stmt.execute(sql);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}