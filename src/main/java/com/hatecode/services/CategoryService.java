/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface CategoryService {
    // CRUD
    List<Category> getCategories() throws SQLException;
    List<Category> getCategories(String query) throws SQLException;
    Category getCategoryById(int id) throws SQLException;
    List<Equipment> getEquipmentByCategory(int id) throws SQLException;
    boolean addCategory(Category cate) throws SQLException;
    boolean updateCategory(Category cate) throws SQLException;
    boolean deleteCategory(int id) throws SQLException;
}
