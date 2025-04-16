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
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll; 
import org.junit.jupiter.api.BeforeAll; 
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
/**
 *
 * @author ADMIN
 */
public class AddUserTestSuite {
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
            boolean result = services.addUser(user,null);
            assertEquals(expectedOutput, result);
        } catch (SQLException ex) {
            Logger.getLogger(AddUserTestSuite.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Test
    public void testAddUserTransactionWithUserError() throws SQLException{
        User user = new User();
        user.setFirstName("Test");
        user.setLastName("Rollback");
        user.setUsername("rollback_test");
        user.setPassword("1");
        user.setPhone("0999999999");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setAvatarId(1);
        // Giả định trường bắt buộc null để sai khi tạo user
        user.setEmail(null);
        
        
        // Nếu ném ngoại lệ ra thì pass
        assertThrows(SQLException.class, () -> {
            services.addUser(user, null);
        });
        
        // Lấy người dùng mới thêm
        User fetchedUser = services.getUserByUsername("rollback_test");
        
        // L
        assertNull(fetchedUser, "User vẫn tồn tại dù đã rollback -> lỗi transaction!");
    }
    
    @Test
    public void testAddUserTransactionWithImageError() throws SQLException{
        User user = new User();
        user.setFirstName("Test Image");
        user.setLastName("Rollback Image");
        user.setUsername("rollback_test");
        user.setPassword("1");
        user.setEmail("rollback@example.com");
        user.setPhone("0999999999");
        user.setRole(Role.USER);
        user.setActive(true);
        user.setAvatarId(0);
        
        // Giả sử tạo file lỗi
        Image i = new Image();
        // Trường file name không được phép null
        i.setFilename(null);
        i.setPath("image/error.png");
        i.setCreateDate(LocalDateTime.now());
        
        // Nếu ném ngoại lệ ra thì pass
        assertThrows(SQLException.class, () -> {
            services.addUser(user,i);
        });
        
        // Lấy người dùng mới thêm
        User fetchedUser = services.getUserByUsername("rollback_test");
        
        // L
        assertNull(fetchedUser, "User vẫn tồn tại dù đã rollback -> lỗi transaction!");
    }
}
