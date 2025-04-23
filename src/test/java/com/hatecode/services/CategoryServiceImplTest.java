package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.*;
import com.hatecode.config.TestDatabaseConfig;
import org.junit.jupiter.api.extension.ExtendWith;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(TestDatabaseConfig.class)
public class CategoryServiceImplTest {

    @BeforeEach
    void setupTestData() throws SQLException {
        // Reset database to clean state
        JdbcUtils.resetDatabase();
        // Khởi tạo dữ liệu mẫu
        String sql = """
                INSERT INTO Category (name)
                VALUES ('Category 1'),
                       ('Category 2'),
                       ('Category 3'),
                       ('Category 4'),
                       ('Category 5');
                INSERT INTO `Category` (name, is_active)
                VALUES ('Category non-active', false);

                INSERT INTO Equipment (code, name, status, category_id, image_id, regular_maintenance_day, description)
                VALUES ('C1E1', 'E1', 1, 1, 1, 30, 'Description 1'),
                       ('C1E2', 'E2', 1, 1, 1, 30, 'Description 2'),
                       ('C1E3', 'E3', 1, 1, 1, 30, 'Description 3'),
                       ('C2E1', 'E4', 1, 2, 1, 30, 'Description 4'),
                       ('C2E2', 'E5', 1, 2, 1, 30, 'Description 5'),
                       ('C3E1', 'E6', 1, 3, 1, 30, 'Description 6');
                """;

        try (Connection conn = JdbcUtils.getConn(); // Use getConn() instead of getConnection()
                Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        // Close the test connection
        JdbcUtils.closeConnection();
    }

    @Test
    public void testAddCategory_Success() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category1 = new Category("Test 1", true);
        Category category2 = new Category("Test 2", true);
        // Act
        boolean result1 = categoryService.addCategory(category1);
        boolean result2 = categoryService.addCategory(category2);

        // Assert
        assertTrue(result1);
        assertTrue(result2);

        // Verify
        assertEquals(7, categoryService.getCategories().size());
        assertEquals(category1.getName(), categoryService.getCategoryById(7).getName());
        assertEquals(category2.getName(), categoryService.getCategoryById(8).getName());
    }

    @Test
    public void testAddCategory_DuplicateName() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category1 = new Category("Test Duplicate", true);
        Category category2 = new Category("Test Duplicate", true);
        // Act
        boolean result1 = categoryService.addCategory(category1);
        // Assert
        assertTrue(result1);
        Exception exception = assertThrows(SQLException.class, () -> {
            categoryService.addCategory(category2);
        });
        // Verify
        assertEquals(ExceptionMessage.CATEGORY_NAME_DUPLICATE, exception.getMessage());
        assertEquals(6, categoryService.getCategories().size());
    }

    @Test
    public void testAddCategory_EmptyName() {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category = new Category("", true);
        // Act
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.addCategory(category);
        });
        // Verify
        assertEquals(ExceptionMessage.CATEGORY_NAME_EMPTY, e.getMessage());
    }

    @Test
    public void testUpdateCategory_Success() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category = categoryService.getCategoryById(5);
        // Act
        category.setName("Updated Test 5");
        boolean result = categoryService.updateCategory(category);
        // Assert
        assertTrue(result);
        // Verify
        assertEquals(5, categoryService.getCategories().size());
        assertEquals(category.getName(), categoryService.getCategoryById(5).getName());
    }

    @Test
    public void testUpdateCategory_DuplicateName() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category1 = categoryService.getCategoryById(1);
        // Act
        category1.setName("Category 2"); // Same name as existing category 2
        // Assert
        Exception e = assertThrows(SQLException.class, () -> {
            categoryService.updateCategory(category1);
        });
        // Verify
        assertEquals(ExceptionMessage.CATEGORY_NAME_DUPLICATE, e.getMessage());
    }

    @Test
    public void testGetCategories_Success() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Assert and Act
        assertEquals(5, categoryService.getCategories().size());
    }

    @Test
    public void testGetCategory_AfterAdd() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category = new Category("Test", true);
        // Act
        categoryService.addCategory(category);
        // Assert
        assertEquals(6, categoryService.getCategories().size());
        // Verify
        assertEquals(category.getName(), categoryService.getCategoryById(7).getName());
    }

    @Test
    public void testGetCategories_ByQuery() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Assert
        String query = "Category 1";
        assertEquals(1, categoryService.getCategories(query).size());
    }

    @Test
    public void testGetCategories_ByQuery_NotFound() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Assert
        String query = "Category 100";
        assertEquals(0, categoryService.getCategories(query).size());
    }

    @Test
    public void testGetCategory_ByQuery_Empty() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Assert
        String query = "";
        assertEquals(5, categoryService.getCategories(query).size());
    }

    @Test
    public void testGetCategoryById_NotFound() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Act
        Category category = categoryService.getCategoryById(-1);
        // Assert
        assertNull(category);
    }

    @Test
    public void testGetCategoryById_Success() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Act
        Category result = categoryService.getCategoryById(2);
        // Assert
        assertNotNull(result);
        // Verify
        assertEquals("Category 2", result.getName());
    }

    @Test
    public void testDeleteCategory_Success() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category = categoryService.getCategoryById(1);
        // Act
        boolean result1 = categoryService.deleteCategory(category);
        boolean result2 = categoryService.deleteCategory(2);
        // Assert
        assertTrue(result1);
        assertTrue(result2);
        // Verify
        assertNull(categoryService.getCategoryById(1));
        assertNull(categoryService.getCategoryById(2));
        assertEquals(3, categoryService.getCategories().size());

    }

    @Test
    public void testDeleteCategory_Fail() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        boolean result1 = categoryService.deleteCategory(6); // is_active = false
        boolean result2 = categoryService.deleteCategory(100); // not found
        // Assert
        assertFalse(result1);
        assertFalse(result2);
        // Verify
        assertNull(categoryService.getCategoryById(6));
    }

    @Test
    public void testDeleteCategory_EmptyId() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        Category category = new Category("", true);
        // Act and Assert
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            categoryService.deleteCategory(category);
        });
        // Verify
        assertEquals(ExceptionMessage.CATEGORY_ID_NULL, e.getMessage());
    }

    @Test
    public void testGetEquipmentByCategory() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        int categoryId = 1;
        // Act
        var result = categoryService.getEquipmentByCategory(categoryId);
        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testGetEquipmentByCategory_NotFound() throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        // Initialize
        int categoryId = 100;
        // Act
        var result = categoryService.getEquipmentByCategory(categoryId);
        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
