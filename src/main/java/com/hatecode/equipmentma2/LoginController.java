package com.hatecode.equipmentma2;

import com.hatecode.equipmentma2.App;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.CloundinaryServicesImpl;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.scene.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

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
