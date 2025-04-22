/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.user;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.ImageService;
import com.hatecode.services.interfaces.UserService;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static org.junit.jupiter.api.Assertions.*;
/**
 *
 * @author ADMIN
 */
public class DeleteUserTestSuite {
    UserService services = new UserServiceImpl();

    @Test
    public void testDeleteNonExistentUser(){
        try {
            boolean result = services.deleteUser(9999);
            assertFalse(result,"user ID not exist");
        } catch (SQLException ex) {
            Logger.getLogger(DeleteUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Test
    public void testDeleteUserWithCustomImage(){
        try{
            // Với người dùng có avatar khác với avatar mặc định đảm bảo xóa cả 2
            User u = services.getUserById(58);
            boolean result = services.deleteUser(58);
            assertTrue(result);

            ImageService imgService = new ImageServiceImpl();
            Image img = imgService.getImageById(u.getAvatarId());
            assertNull(img,"Image hasn't been deleted yet!!!");
        }
        catch (SQLException ex) {
            Logger.getLogger(DeleteUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testDeleteUserWithDefaultImage(){
        try{
            // Với người dùng có avatar mặc định đảm bảo chỉ xóa người dùng không xóa ảnh
            User u = services.getUserById(59);
            boolean result = services.deleteUser(59);
            assertTrue(result);

            ImageService imgService = new ImageServiceImpl();
            Image img = imgService.getImageById(u.getAvatarId());
            assertNotNull(img,"Image has been deleted!!!");
        }
        catch (SQLException ex) {
            Logger.getLogger(DeleteUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}