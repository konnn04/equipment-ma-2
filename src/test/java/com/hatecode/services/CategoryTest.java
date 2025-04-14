package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.services.interfaces.CategoryService;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.OperationResult;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryTest {
    static Connection conn = null;
    CategoryService categoryService = new CategoryServiceImpl();

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = JdbcUtils.getConn();
        try (PreparedStatement stm = conn.prepareStatement(
                "DELETE FROM category WHERE name LIKE 'Test%'")) {
            stm.executeUpdate();
        }
        conn.setAutoCommit(false);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
//        conn.rollback();
        conn.setAutoCommit(true);
        conn.close();
    }

//    @AfterEach
//    public void rollbackAfterTest() throws SQLException {
//        conn.rollback();
//    }

    @Test
    public void testAddCategory_Success() throws SQLException {
        // Initialize test data
        Category category1 = new Category("Test 1", true);
        Category category2 = new Category("Test 2", false);
        // Act
        OperationResult result1 = categoryService.addCategory(category1);
        OperationResult result2 = categoryService.addCategory(category2);
        // Assert
        assertTrue(result1.isSuccess());
        assertTrue(result2.isSuccess());

    }

    @Test
    public void testAddCategory_DuplicateName() throws SQLException {
        // Initialize test data
        Category category1 = new Category("Test 3", true);
        Category category2 = new Category("Test 3", false);
        // Act
        OperationResult result1 = categoryService.addCategory(category1);
        OperationResult result2 = categoryService.addCategory(category2);
        // Assert
        assertTrue(result1.isSuccess() );
        assertFalse(result2.isSuccess());
    }

    @Test
    public void testAddCategory_EmptyName() throws SQLException {
        // Initialize test data
        Category category = new Category("", true);
        // Act
        OperationResult result = categoryService.addCategory(category);
        // Assert
        assertFalse(result.isSuccess());
    }
}
