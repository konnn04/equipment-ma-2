package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceImplTest {
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

    @Test
    public void testAddCategory_Success() throws SQLException {
        // Initialize test data
        Category category1 = new Category("Test 1");
        Category category2 = new Category("Test 2", false);
        // Act
        boolean result1 = categoryService.addCategory(category1);
        boolean result2 = categoryService.addCategory(category2);
        // Assert
        assertTrue(result1);
        assertTrue(result2);

    }

    @Test
    public void testAddCategory_DuplicateName() throws SQLException {
        // Initialize test data
        Category category1 = new Category("Test 3", true);
        Category category2 = new Category("Test 3", false);
        // Act
        boolean result1 = categoryService.addCategory(category1);
        boolean result2 = categoryService.addCategory(category2);
        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    public void testAddCategory_EmptyName() throws SQLException {
        // Initialize test data
        Category category = new Category("", true);
        // Act
        boolean result = categoryService.addCategory(category);
        // Assert
        assertFalse(result);
    }

    @Test
    public void testde() throws SQLException {
        // Initialize test data
        boolean r = false;
        r = categoryService.deleteCategory(10);
        assertFalse(r);
    }
}
