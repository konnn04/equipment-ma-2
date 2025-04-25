package com.hatecode.equipmentma2;

import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import com.hatecode.security.Permission;
import com.hatecode.security.SecurityManager;

public class App extends Application {
    private static User currentUser;
    private static Stage primaryStage;

    public static boolean hasPermission(Permission permission) {
        if (currentUser == null || !currentUser.isActive()) {
            System.out.println("Current user is not active");
            return false;
        }
        return SecurityManager.hasPermission(currentUser, permission);
    }

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        User admin = UserServiceImpl.createSuperUser();
        setCurrentUser(admin);
        primaryStage = stage;

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Equipment Management System");

        Image appIcon = new Image(App.class.getResourceAsStream("/com/hatecode/assets/app-icon.png"));
        stage.getIcons().add(appIcon);

        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(event -> {
            event.consume(); // Prevent default close operation
            showExitConfirmation();
        });

        stage.show();
    }

    public static void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thoát");
        alert.setHeaderText("Bạn có chắc chắn muốn thoát không?");
        alert.setContentText("Mọi thay đổi chưa lưu sẽ bị mất.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    public static void switchToHome() throws IOException {
        // Load the home-view.fxml file
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        primaryStage.setScene(scene);

        primaryStage.setMaximized(true);
    }

    public static void main(String[] args) {
        launch();
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLogin() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.getRole().getName().equals("Admin");
    }
}