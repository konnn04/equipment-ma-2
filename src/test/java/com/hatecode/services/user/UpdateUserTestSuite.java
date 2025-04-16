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
public class UpdateUserTestSuite {
    UserService services = new UserServiceImpl();

    @BeforeAll
    public static void setUp() throws SQLException {
        System.out.println("SET UP");
    }

    @AfterAll
    public static void tearDown() throws SQLException {
        System.out.println("TEAR DOWN");
    }

    @ParameterizedTest
    @CsvFileSource(resources = "updateUser.csv", numLinesToSkip = 1)
    public void testUpdateUser(int userID,
                               String firstName,
                               String lastname,
                               String username,
                               String password,
                               String email,
                               String phone,
                               int roleId,
                               boolean isActive,
                               boolean expectedOutput){
        User u = new User();
        u.setId(userID);
        u.setFirstName(firstName);
        u.setLastName(lastname);
        u.setUsername(username);
        u.setPassword(password);
        u.setEmail(email);
        u.setPhone(phone);
        u.setActive(isActive);

        try {
            // Bắt ngoại lệ nếu roleId không hợp lệ
            Role role = Role.fromId(roleId);
            u.setRole(role);

            boolean result = services.updateUser(u);
            assertEquals(expectedOutput, result);
        } catch (IllegalArgumentException ex) {
            // Nếu expectedOutput là false, thì hợp lý khi ném ra lỗi
            assertFalse(expectedOutput, "Expected failure due to invalid roleId: " + roleId);
        } catch (SQLException ex) {
            Logger.getLogger(UpdateUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testConcurrentUpdateUser() throws InterruptedException {
        User u1 = new User();
        u1.setId(1);
        u1.setFirstName("thread 1");
        u1.setLastName("Doe");
        u1.setUsername("johndoe");
        u1.setPassword("123456");
        u1.setEmail("john.doe@example.com");
        u1.setPhone("0909123456");
        u1.setRole(Role.fromId(1));
        u1.setActive(true);

        User u2 = new User();
        u2.setId(1);
        u2.setFirstName("thread 2");
        u2.setLastName("Doe");
        u2.setUsername("johndoe");
        u2.setPassword("123456");
        u2.setEmail("john.doe@example.com");
        u2.setPhone("0909123456");
        u2.setRole(Role.fromId(1));
        u2.setActive(true);

        Runnable updateTask1 = () -> {
          try{
              boolean result = services.updateUser(u1,null);
              assertTrue(result,"First thread failed to update user.");
          } catch (SQLException e) {
              throw new RuntimeException(e);
          }
        };

        Runnable updateTask2 = () -> {
            try{
                boolean result = services.updateUser(u2,null);
                assertTrue(result, "Second thread failed to update user.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        };

        Thread thread1 = new Thread(updateTask1);
        Thread thread2 = new Thread(updateTask2);

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();

        try{
            User updateUser = services.getUserById(1);
            assertNotNull(updateUser,"User was not updated successfully");
            assertTrue(updateUser.getFirstName().equals("thread 1") || updateUser.getFirstName().equals("thread 2"),"Update failed");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Test
    public void testTransactionalRollbackWhenUserUpdateFails(){
        User u1 = new User();
        u1.setId(31);
        u1.setFirstName(null); // gây ra lỗi
        u1.setLastName("Doe");
        u1.setUsername("johndoe");
        u1.setPassword("123456");
        u1.setEmail("john.doe@example.com");
        u1.setPhone("0909123456");
        u1.setRole(Role.fromId(1));
        u1.setActive(true);
        
        
        Image img = new Image();
        img.setId(0);
        img.setFilename("test.png");
        img.setCreateDate(LocalDateTime.now());
        img.setPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
                
        assertThrows(SQLException.class,() -> {
           services.updateUser(u1,img);
        });
        
        ImageService imageService = new ImageServiceImpl();
        Image storedImage;
        try {
            storedImage = imageService.getImageByPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
            assertNull(storedImage,"Image not exist due to rollback");
        } catch (SQLException ex) {
            Logger.getLogger(UpdateUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testTransactionalRollbackWhenImageUpdateFails(){
        User u1 = new User();
        u1.setId(31);
        u1.setFirstName("John");
        u1.setLastName("Doe");
        u1.setUsername("johndoe");
        u1.setPassword("123456");
        u1.setEmail("john.doe@example.com");
        u1.setPhone("0909123456");
        u1.setRole(Role.fromId(1));
        u1.setActive(true);
        
        
        Image img = new Image();
        img.setId(0);
        img.setFilename(null); // gây ra lỗi
        img.setCreateDate(LocalDateTime.now());
        img.setPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
                
        assertThrows(SQLException.class,() -> {
           services.updateUser(u1,img);
        });
        
        ImageService imageService = new ImageServiceImpl();
        Image storedImage;
        try {
            storedImage = imageService.getImageByPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
            assertNull(storedImage,"Image not exist due to rollback");
        } catch (SQLException ex) {
            Logger.getLogger(UpdateUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

