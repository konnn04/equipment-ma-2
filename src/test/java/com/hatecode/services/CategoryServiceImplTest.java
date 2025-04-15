package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.*;


import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryServiceImplTest {
    private static Connection conn;
    private CategoryService categoryService;

    @BeforeAll
    static void setupDatabase() {
        JdbcUtils.connectionProvider = TestDBUtils::getConnection;
        conn = TestDBUtils.getConnection();
    }

    @BeforeEach
    void setupTestData() throws SQLException {
        categoryService = new CategoryServiceImpl();
        conn.setAutoCommit(false);
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        conn.rollback();
        conn.setAutoCommit(true);
    }

    @AfterAll
    static void shutdownDatabase() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    @Test
    public void testAddCategory_Success() throws SQLException {

        // Initialize test data
        Category category1 = new Category("Test 1", true);
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
        // Assert
        assertTrue(result1);
        assertThrows(SQLException.class, () -> {
            categoryService.addCategory(category2);
        });
    }

    @Test
    public void testAddCategory_EmptyName()  {
        // Initialize test data
        Category category = new Category("", true);
        // Act
        assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(category);
        });
    }

//    @Test
//    public void testde() throws SQLException {
//        // Initialize test data
//        boolean r = false;
//        r = categoryService.deleteCategory(10);
//        assertFalse(r);
//    }
}
