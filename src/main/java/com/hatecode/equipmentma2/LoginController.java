package com.hatecode.equipmentma2;

import com.hatecode.services.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    private final UserService userService = new UserServiceImpl();

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Vui lòng nhập tên đăng nhập và mật khẩu");
            return;
        }

        try {
            if ("admin".equals(username) && "1".equals(password)) {
                App.switchToHome();
            } else if (userService.authenticateUser(username, password)) {
                // Fallback to hardcoded admin user for development
                App.switchToHome();
            } else {
                errorMessageLabel.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
        } catch (SQLException e) {
            errorMessageLabel.setText("Lỗi kết nối cơ sở dữ liệu: " + e.getMessage());
        } catch (IOException e) {
            errorMessageLabel.setText("Không thể mở trang chủ: " + e.getMessage());
        }
    }

}