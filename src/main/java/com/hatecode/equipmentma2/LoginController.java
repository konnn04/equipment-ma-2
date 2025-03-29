package com.hatecode.equipmentma2;

import com.hatecode.services.interfaces.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.sql.SQLException;
public class LoginController {
    @FXML
    TextField usernameField;

    @FXML
     PasswordField passwordField;

    @FXML
    Label errorMessageLabel;

    final UserService userService = new UserServiceImpl();

    @FXML
    protected void handleLogin() {
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
            System.out.println(e.getMessage());
        }
    }

//    Khi enter vào trường đăng nhập
    @FXML
    protected void onEnterLogin(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            handleLogin();
        }
    }
//    Khi click vào nút đăng nhập
    @FXML
    protected void onClickLogin(ActionEvent event) {
        handleLogin();
    }
}