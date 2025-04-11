package com.hatecode.services.category;

import com.hatecode.pojo.Category;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.services.interfaces.CategoryService;
import com.hatecode.utils.JdbcUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class addCategory {
    static Connection conn = null;
    CategoryService categoryService = new CategoryServiceImpl();

    @BeforeAll
    public static void setUp() throws SQLException {
        conn = JdbcUtils.getConn();
        conn.setAutoCommit(false);
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        conn.setAutoCommit(true);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "addCate.csv", numLinesToSkip = 1)
    public void testAddCategory(String name, boolean isActive, boolean expected) {
        try{
            assertEquals(expected,categoryService.addCategory(new Category(name, isActive)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "addCate.csv", numLinesToSkip = 1)
    public void testAddCategoryDub(String name, boolean isActive, boolean expected) {
        assertThrows(SQLIntegrityConstraintViolationException.class, () -> {
            categoryService.addCategory(new Category(name, isActive));
        });
    }



}
