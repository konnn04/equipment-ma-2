/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.user;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.UserService;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.TestDBUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author ADMIN
 */
public class AddUserTestSuite {
    private UserService services;
    private Connection conn;
    @BeforeEach
    public void setUp() throws SQLException {
        conn = TestDBUtils.createIsolatedConnection();
        services = new UserServiceImpl();
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            JdbcUtils.resetTestConnection();
            conn.close();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "addUser.csv", numLinesToSkip = 1)
    public void TestAddUser(String firstName, String lastName, String username,
            String password, String email, String phone,
            int roleId, boolean isActive, int avatarId, boolean expectedOutput) {
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setPhone(phone);
        try {
            user.setRole(Role.fromId(roleId));
        } catch (IllegalArgumentException ex) {
            assertFalse(expectedOutput, ExceptionMessage.ROLE_NOT_EXIST); // Nếu không tìm thấy role, ta mong muốn kết quả là false
            return;
        }
        user.setActive(isActive);
        user.setAvatarId(avatarId);

        try {
            boolean result = services.addUser(conn,user, null);
            assertEquals(expectedOutput, result);
        } catch (SQLException ex) {
            Logger.getLogger(AddUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Test
    public void testAddUserTransactionWithUserError() throws SQLException {
        // User hợp lệ về mặt Role, nhưng thiếu email => addUser sẽ return false chứ không throw exception
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Rollback");
        user.setUsername("rollback_user_error");
        user.setPassword("1");
        user.setPhone("0999999999");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setAvatarId(1);
        user.setEmail(null);
        
        boolean result = services.addUser(conn, user, null);
        assertFalse(result, "Hàm addUser phải trả về false nếu thiếu thông tin bắt buộc");

        // Kiểm tra trong DB không có user với username này
        User fetchedUser = services.getUserByUsername("rollback_user_error");
        assertNull(fetchedUser, "User vẫn tồn tại dù đã return false -> lỗi transaction hoặc logic kiểm tra!");
    }

    @Test
    public void testAddUserTransactionWithImageError() throws SQLException {
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Rollback");
        user.setUsername("rollback_user_error");
        user.setPassword("1");
        user.setPhone("0999999999");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setAvatarId(1);
        user.setEmail("testUser@gmail.com");

        Image testImage = new Image(null,LocalDateTime.now(),"error.png");

        boolean result = services.addUser(conn, user, testImage);
        assertFalse(result, "Hàm addUser phải trả về false nếu thiếu thông tin bắt buộc");

        User fetchedUser = services.getUserByUsername("rollback_user_error");
        assertNull(fetchedUser, "User vẫn tồn tại dù đã return false -> lỗi transaction hoặc logic kiểm tra!");
    }
}
