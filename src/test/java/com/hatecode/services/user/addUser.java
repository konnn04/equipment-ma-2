/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.user;
import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.UserService;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterAll; 
import org.junit.jupiter.api.BeforeAll; 
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
/**
 *
 * @author ADMIN
 */
public class addUser {
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
    @CsvFileSource(resources = "addUser.csv", numLinesToSkip = 1)
    public void TestAddUser(String firstName, String lastName, String username,
                            String password, String email, String phone,
                            int roleId, boolean isActive, int avatarId,boolean expectedOutput){
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(Role.fromId(roleId));
        user.setActive(isActive);
        user.setAvatarId(avatarId);
        
        try {
            boolean result = services.addUser(user);
            assertEquals(expectedOutput, result);
        } catch (SQLException ex) {
            Logger.getLogger(addUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
