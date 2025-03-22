package com.hatecode.equipmentma2.Controllers;

import com.hatecode.equipmentma2.App;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    protected void onLoginButtonClick() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simple authentication (temporary)
        if ("admin".equals(username) && "1".equals(password)) {
            try {
                App.switchToHome();
            } catch (IOException e) {
                errorMessageLabel.setText("Không thể mở trang chủ: " + e.getMessage());
            }
        } else {
            errorMessageLabel.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }
}