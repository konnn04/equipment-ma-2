/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.JdbcUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class CategoryServices {
    public List<Category> getCategory() throws SQLException{
        List<Category> res = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            String sql = "SELECT * FROM category";
            PreparedStatement stm = conn.prepareCall(sql);
            
            ResultSet rs = stm.executeQuery();
            
            while(rs.next()){
                Category c = new Category(rs.getInt("id"), rs.getString("name"));
                res.add(c);
            }
            return res;
        }
    }
    
    public List<Equipment> getEquipmentByCategory(int id) throws SQLException{
        List<Equipment> res = new ArrayList<>();
        try(Connection conn = JdbcUtils.getConn()){
            String sql = "SELECT * FROM equipment WHERE category = ?";
            PreparedStatement stm = conn.prepareCall(sql);
            stm.setInt(1, id);
            
            ResultSet rs = stm.executeQuery();
            
            while(rs.next()){
                Equipment e = new Equipment(rs.getInt("id"), rs.getString("code"), rs.getString("name"), rs.getDate("import_date"));
                res.add(e);
            }
            return res;
        }
    }
}
