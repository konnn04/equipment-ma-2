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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ADMIN
 */
public class UpdateUserTestSuite {

    private UserService services = new UserServiceImpl();

    private ImageService imageService = new ImageServiceImpl();

    private User sampleUser;


    @BeforeEach
    public void setUp() throws SQLException {
        sampleUser = new User();
        sampleUser.setId(1);
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setUsername("johndoe");
        sampleUser.setPassword("123456");
        sampleUser.setEmail("john.doe@example.com");
        sampleUser.setPhone("0909123456");
        sampleUser.setRole(Role.fromId(1));
        sampleUser.setActive(true);
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
        img.setCreatedAt(LocalDateTime.now());
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
        img.setCreatedAt(LocalDateTime.now());
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

