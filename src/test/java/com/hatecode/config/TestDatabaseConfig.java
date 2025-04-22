package com.hatecode.config;

import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;


public class TestDatabaseConfig implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) {
        JdbcUtils.connectionProvider = () -> {
            try {
                return JdbcUtils.getDirectTestConnection();
            } catch (Exception e) {
                throw new RuntimeException("Không thể kết nối đến cơ sở dữ liệu test", e);
            }
        };
    }
}