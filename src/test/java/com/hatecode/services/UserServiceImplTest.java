package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.Image;
import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.utils.PasswordUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(TestDatabaseConfig.class)
public class UserServiceImplTest {

    User sampleUser;

    @BeforeEach
    void setupTestData() throws SQLException {
        // Reset database to clean state
        JdbcUtils.resetDatabase();
        // Set up sample user for tests
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

        String sql = """
INSERT INTO Image (filename, path)
VALUES ('default_avatar.png','/images/default.png'),
       ('test_avatar.png', '/images/test_avatar.png');

INSERT INTO `User` (first_name, last_name, username, password, email, phone, role,avatar_id)
VALUES ('Default', 'Image', 'defaultimg', 'password123', 'default@example.com', '0123456789', 1,1);

INSERT INTO `User` (first_name, last_name, username, password, email, phone, role, avatar_id)
VALUES ('Custom', 'Image', 'customimg', 'password123', 'custom@example.com', '0123456781', 1, 2);
""";
        try (Connection conn = JdbcUtils.getConn();
             Statement statement = conn.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    @AfterEach
    void clearTestChanges() throws SQLException {
        JdbcUtils.closeConnection();
    }

    /*
     * =============================================================================
     * Add User Tests
     * ===========================================================================
     */

    @ParameterizedTest
    @CsvFileSource(resources = "user_add.csv", numLinesToSkip = 1)
    void testAddUser(String firstName, String lastName, String username, String password, String email, String phone, int roleId, boolean isActive, int avatarId, boolean expectedOutput) {
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
            assertFalse(expectedOutput, ExceptionMessage.ROLE_NOT_EXIST);
            return;
        }

        user.setActive(isActive);
        user.setAvatarId(avatarId);

        UserService userService = new UserServiceImpl();
        try {
            boolean result = userService.addUser(user, null);
            assertTrue(userService.getCount() < 4);
            assertEquals(expectedOutput, result);
        } catch (SQLException ex) {
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    @Test
    void testAddUserTransactionWithUserError() throws SQLException {
        UserService userService = new UserServiceImpl();
        // User hợp lệ về mặt Role, nhưng thiếu email => addUser sẽ return false chứ
        // không throw exception
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Rollback");
        user.setUsername("rollback_user_error");
        user.setPassword("1");
        user.setPhone("0999999999");
        user.setRole(Role.TECHNICIAN);
        user.setActive(true);
        user.setAvatarId(1);
        user.setEmail(null);

        boolean result = userService.addUser(user, null);
        assertFalse(result, "Hàm addUser phải trả về false nếu thiếu thông tin bắt buộc");

        // Kiểm tra trong DB không có user với username này
        User fetchedUser = userService.getUserByUsername("rollback_user_error");
        assertNull(fetchedUser, "User vẫn tồn tại dù đã return false -> lỗi transaction hoặc logic kiểm tra!");
    }

    @Test
    void testAddUserTransactionWithImageError() throws SQLException {
        UserService userService = new UserServiceImpl();

        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Rollback");
        user.setUsername("rollback_user_error");
        user.setPassword("1");
        user.setPhone("0999999999");
        user.setRole(Role.TECHNICIAN);
        user.setActive(true);
        user.setAvatarId(1);
        user.setEmail("testUser@gmail.com");

        Image testImage = new Image(null, LocalDateTime.now(), "error.png");

        boolean result = userService.addUser(user, testImage);
        assertFalse(result, "Hàm addUser phải trả về false nếu thiếu thông tin bắt buộc");

        User fetchedUser = userService.getUserByUsername("rollback_user_error");
        assertNull(fetchedUser, "User vẫn tồn tại dù đã return false -> lỗi transaction hoặc logic kiểm tra!");
    }

    /*
     * =============================================================================
     * Update User Tests
     * ===========================================================================
     */

    @ParameterizedTest
    @CsvFileSource(resources = "user_update.csv", numLinesToSkip = 1)
    void testUpdateUser(int userID, String firstName, String lastname, String username, String password, String email, String phone, int roleId, boolean isActive, boolean expectedOutput) {
        UserService userService = new UserServiceImpl();

        User u = new User();
        u.setId(userID);
        u.setFirstName(firstName);
        u.setLastName(lastname);
        u.setUsername(username);
        u.setEmail(email);
        u.setPhone(phone);
        u.setActive(isActive);

        try {
            // Bắt ngoại lệ nếu roleId không hợp lệ
            Role role = Role.fromId(roleId);
            u.setRole(role);

            boolean result = userService.updateUser(u,null,null);
            assertEquals(expectedOutput, result);
        } catch (IllegalArgumentException ex) {
            // Nếu expectedOutput là false, thì hợp lý khi ném ra lỗi
            assertFalse(expectedOutput, "Expected failure due to invalid roleId: " + roleId);
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    @Test
    void testTransactionalRollbackWhenUserUpdateFails() {
        UserService userService = new UserServiceImpl();
        ImageService imageService = new ImageServiceImpl();

        User u1 = new User();
        u1.setId(1);
        u1.setFirstName(null); // gây ra lỗi
        u1.setLastName("Doe");
        u1.setUsername("johndoe");
        u1.setEmail("john.doe@example.com");
        u1.setPhone("0909123456");
        u1.setRole(Role.fromId(1));
        u1.setActive(true);

        Image img = new Image();
        img.setId(0);
        img.setFilename("test.png");
        img.setCreatedAt(LocalDateTime.now());
        img.setPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");

        try {
            boolean result = userService.updateUser(u1,null,img);
            assertFalse(result);
        } catch (SQLException e) {
            fail("SQLException occurred during verification: " + e.getMessage());
        }

        try {
            Image storedImage = imageService.getImageByPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
            assertNull(storedImage, "Image not exist due to rollback");
        } catch (SQLException ex) {
            fail("SQLException occurred during verification: " + ex.getMessage());
        }
    }

    @Test
    void testTransactionalRollbackWhenImageUpdateFails() {
        UserService userService = new UserServiceImpl();
        ImageService imageService = new ImageServiceImpl();

        User u1 = new User();
        u1.setId(1);
        u1.setFirstName("John");
        u1.setLastName("Doe");
        u1.setUsername("johndoe");
        u1.setEmail("john.doe@example.com");
        u1.setPhone("0909123456");
        u1.setRole(Role.fromId(1));
        u1.setActive(true);

        Image img = new Image();
        img.setId(0);
        img.setFilename(null); // gây ra lỗi
        img.setCreatedAt(LocalDateTime.now());
        img.setPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");

        try {
            boolean result = userService.updateUser(u1,null,img);
            assertFalse(result);
        } catch (SQLException e) {
            fail("SQLException occurred during verification: " + e.getMessage());
        }

        try {
            Image storedImage = imageService.getImageByPath("https://res.cloudinary.com/dg66aou8q/image/upload/v1744607866/OIP_awr3kj.jpg");
            assertNull(storedImage, "Image not exist due to rollback");
        } catch (SQLException ex) {
            fail("SQLException occurred during verification: " + ex.getMessage());
        }
    }

    /*
     * =============================================================================
     * Delete User Tests
     * ===========================================================================
     */

    @Test
    void testDeleteNonExistentUser() {
        UserService userService = new UserServiceImpl();
        try {
            boolean result = userService.deleteUser(9999);
            assertFalse(result, "Deleting non-existent user should return false");
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    @Test
    void testDeleteUserWithCustomImage() {
        UserService userService = new UserServiceImpl();
        ImageService imageService = new ImageServiceImpl();

        try {
            // Với người dùng có avatar khác với avatar mặc định đảm bảo xóa cả 2
            boolean result = userService.deleteUser(2);
            assertTrue(result);

            Image img = imageService.getImageById(2);
//            System.out.println(u.getAvatarId());
            assertNull(img, "Image hasn't been deleted yet!!!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    @Test
    void testDeleteUserWithDefaultImage() {
        UserService userService = new UserServiceImpl();
        ImageService imageService = new ImageServiceImpl();

        try {
            // Với người dùng có avatar mặc định đảm bảo chỉ xóa người dùng không xóa ảnh
            User u = userService.getUserById(1);
            boolean result = userService.deleteUser(1);
            assertTrue(result);

            Image img = imageService.getImageById(u.getAvatarId());
            assertNotNull(img, "Image has been deleted!!!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    /*
     * =============================================================================
     * Authentication Tests
     * ===========================================================================
     */

    // Separate setup/teardown for auth tests since they need special handling
    private Connection authConn;
    private final String testUsername = "testuser";
    private final String testPassword = "1";

    @Test
    void testAuthenticateUser_Success() {
        UserService userService = new UserServiceImpl();

        try {
            // Setup
            setupTestUser();

            // Test
            User user = userService.authenticateUser(testUsername, testPassword);

            // Verify
            assertNotNull(user);
//            assertEquals(testUsername, user.getUsername());

            // Cleanup
            cleanupTestUser();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception occurred during authentication test: " + ex.getMessage());
        }
    }

    @Test
    void testAuthenticateUser_WrongPassword() {
        UserService userService = new UserServiceImpl();

        try {
            // Setup
            setupTestUser();

            // Test
            User user = userService.authenticateUser(testUsername, "wrongpass");

            // Verify
            assertNull(user);

            // Cleanup
            cleanupTestUser();
        } catch (Exception ex) {
            fail("Exception occurred during authentication test: " + ex.getMessage());
        }
    }

    @Test
    void testAuthenticateUser_UsernameNotFound() {
        UserService userService = new UserServiceImpl();

        try {
            User user = userService.authenticateUser("notfound", testPassword);
            assertNull(user);
        } catch (SQLException ex) {
            fail("SQLException occurred during test: " + ex.getMessage());
        }
    }

    // Helper methods for authentication tests
    private void setupTestUser() throws Exception {
//        authConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/equipmentma2", "root", "123456");
//
//        // Cleanup any existing test user
//        try (Statement statement = authConn.createStatement()) {
//            statement.executeUpdate("DELETE FROM User WHERE username = '" + testUsername + "'");
//        }
//
//        // Create test user
//        String hashedPassword = PasswordUtils.hashPassword(testPassword);
//        String insertSQL = "INSERT INTO User (first_name, last_name, username, password, email, phone, role, is_active, avatar_id) " + "VALUES ('Test', 'User', ?, ?, 'test@example.com', '0123456789', 1, 1, 1)";
//
//        try (PreparedStatement pstmt = authConn.prepareStatement(insertSQL)) {
//            pstmt.setString(1, testUsername);
//            pstmt.setString(2, hashedPassword);
//            pstmt.executeUpdate();
//        }
        String hashedPassword = PasswordUtils.hashPassword("1");
        String sql = """            
            INSERT INTO `User` (first_name, last_name, username, password, email, phone, role,avatar_id)
            VALUES ('Test', 'User', 'testuser', ?, 'testuser@gmail.com', '0123432423', 1,1);
            """;
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stm = conn.prepareStatement(sql)) {
            stm.setString(1,hashedPassword);
            stm.executeUpdate();
        }
    }

    private void cleanupTestUser() throws Exception {
        if (authConn != null) {
            try (Statement statement = authConn.createStatement()) {
                statement.executeUpdate("DELETE FROM User WHERE username = 'testuser'");
            }
            authConn.close();
        }
    }
}