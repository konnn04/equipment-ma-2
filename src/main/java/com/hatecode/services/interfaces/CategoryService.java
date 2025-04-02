/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.interfaces;

import com.hatecode.models.Category;
import com.hatecode.models.Equipment;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface CategoryService {
    List<Category> getCategories() throws SQLException;
    List<Equipment> getEquipmentByCategory(int id) throws SQLException;
    boolean addCategory(Category cate) throws SQLException;
    boolean updateCategory(Category cate) throws SQLException;
    boolean deleteCategory(int id) throws SQLException;
}
