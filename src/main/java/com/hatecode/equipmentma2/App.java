package com.hatecode.equipmentma2;

import com.hatecode.pojo.Role;
import com.hatecode.pojo.User;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.utils.MaintenanceCheckScheduler;
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
import com.hatecode.utils.MaintenanceStatusScheduler;

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
    public void start(Stage stage) {
        try {
            UserService userService = new UserServiceImpl();
            User admin = userService.createSuperUser();
            // bypass login
            setCurrentUser(admin);
            primaryStage = stage;

            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Equipment Management System");
            //        stage.setResizable(false);

            Image appIcon = new Image(App.class.getResourceAsStream("/com/hatecode/assets/app-icon.png"));
            stage.getIcons().add(appIcon);

            stage.setScene(scene);
            stage.show();

            stage.setOnCloseRequest(event -> {
                event.consume(); // Prevent default close operation
                showExitConfirmation();
            });

            stage.show();

            // Start the maintenance status scheduler
            MaintenanceStatusScheduler.getInstance().start();

            // Start maintenance check scheduler
            MaintenanceCheckScheduler.getInstance().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        try {
            // Shutdown schedulers gracefully
            MaintenanceCheckScheduler.getInstance().shutdown();
            MaintenanceStatusScheduler.getInstance().shutdown();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showExitConfirmation() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        alert.setContentText("OK to exit, Cancel to stay in the application.");

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

    public static void switchToLogin() {
        try {
            currentUser = null;
            FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("login-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
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