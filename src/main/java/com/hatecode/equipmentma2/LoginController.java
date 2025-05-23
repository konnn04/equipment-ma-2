package com.hatecode.equipmentma2;

import com.hatecode.pojo.User;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {

    @FXML
    TextField usernameField;

    @FXML
    PasswordField passwordField;

    @FXML
    Label errorMessageLabel;

    @FXML
    AnchorPane leftPane;

    @FXML
    ImageView backgroundImageView;

    @FXML
    ImageView loginGraphicImageView;


    private final UserService userService = new UserServiceImpl();

    @FXML
    public void initialize() {
        backgroundImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/background.png"))));
        backgroundImageView.setSmooth(true);
        backgroundImageView.setPreserveRatio(false);
        backgroundImageView.fitWidthProperty().bind(leftPane.widthProperty());
        backgroundImageView.fitHeightProperty().bind(leftPane.heightProperty());
        backgroundImageView.setManaged(false);

        loginGraphicImageView.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/login_graphic.png"))));
        loginGraphicImageView.setPreserveRatio(true);
        loginGraphicImageView.fitWidthProperty().bind(leftPane.widthProperty().multiply(0.7));
        loginGraphicImageView.fitHeightProperty().bind(leftPane.heightProperty().multiply(0.7));
    }

    @FXML
    protected void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessageLabel.setText("Please enter username and password");
            return;
        }

        try {
            User user = userService.authenticateUser(username, password);
            if (user != null) {
                App.setCurrentUser(user);
                App.switchToHome();
            } else {
                errorMessageLabel.setText("Username or password is incorrect");
            }
        } catch (SQLException e) {
            errorMessageLabel.setText("Error connecting to database: " + e.getMessage());
        }
        catch (IOException e) {
            errorMessageLabel.setText("Cannot load home page: " + e.getMessage());
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