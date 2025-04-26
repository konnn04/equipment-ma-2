package com.hatecode.equipmentma2;

import com.hatecode.pojo.*;
import com.hatecode.security.Permission;

import com.hatecode.utils.AlertBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {
    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());
    private final Map<String, Object> controllers = new HashMap<>();
    private final Map<String, Boolean> loadedTabs = new HashMap<>();

    @FXML private TabPane tabPane;
    @FXML private Tab equipmentTab;
    @FXML private Tab maintenanceTab;
    @FXML private Tab maintenanceHistoryTab;
    @FXML private Tab recordNewRepairTab;
    @FXML private Tab userManagerTab;
    @FXML private Tab reportTab;
    @FXML private Tab notificationTab;
    @FXML private Label UIUsernameTextField;
    @FXML private Label UIRoleTextField;
    @FXML private Button logoutButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initializeTabs();
            setupLazyLoading();
            initUI();
            initEvent();
            // Kiểm tra nếu không phải admin thì ẩn tab quản lý người dùng
            userManagerTab.setDisable(!App.hasPermission(Permission.USER_VIEW));
            // Load tab đầu tiên khi ứng dụng khởi động
            loadTab(tabPane.getSelectionModel().getSelectedItem());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing application", e);
            showErrorDialog("Application Error", 
                "Failed to initialize the application", 
                "Please check the logs or contact support: " + e.getMessage());
        }
    }
    
    private void initializeTabs() {
        // Khai báo các tab và đánh dấu là chưa được load
        loadedTabs.put(equipmentTab.getId(), false);
        loadedTabs.put(maintenanceTab.getId(), false);
        loadedTabs.put(maintenanceHistoryTab.getId(), false);
        loadedTabs.put(recordNewRepairTab.getId(), false);
        loadedTabs.put(userManagerTab.getId(), false);
    }

    private void setupLazyLoading() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                try {
                    // Chỉ load tab nếu chưa được load trước đó
                    loadTab(newTab);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error loading tab: " + newTab.getId(), e);
                    showErrorDialog("Tab Loading Error", 
                        "Failed to load the tab content", 
                        "Error: " + e.getMessage());
                }
                
                LOGGER.info("Tab changed to: " + newTab.getId());
            }
        });
    }
    
    private void loadTab(Tab tab) throws IOException, SQLException {
        String tabId = tab.getId();
        
        // Bỏ qua nếu tab đã được load
        if (Boolean.TRUE.equals(loadedTabs.get(tabId))) {
            return;
        }
        
        LOGGER.info("Loading tab content for: " + tabId);
        
        switch (tabId) {
            case "equipmentTab":
                EquipmentManagerController equipmentController = (EquipmentManagerController) loadTabContent(
                    tab, "equipment-tab-view.fxml");
                equipmentController.init();
                break;
                
            case "maintenanceTab":
                MaintenanceManagerController maintenanceController = (MaintenanceManagerController) loadTabContent(
                    tab, "maintenance-tab-view.fxml");

                // Create and register the email notifier
                MaintenanceEmailNotifier emailNotifier = new MaintenanceEmailNotifier();
                maintenanceController.addMaintenanceChangeListener(emailNotifier);
                maintenanceController.init();
                break;
                
            case "maintenanceHistoryTab":
                MaintenanceHistoryController historyController = (MaintenanceHistoryController) loadTabContent(
                    tab, "maintenance-history-tab-view.fxml");
                historyController.initMaintenanceHistory();
                break;
                
            case "recordNewRepairTab":
                RecordNewRepairManagerController repairController = (RecordNewRepairManagerController) loadTabContent(
                    tab, "record-manager-tab-view.fxml");
                repairController.loadColumnMaintenance();
                repairController.loadMaintenancesData(null);
                repairController.RecordNewRepairSetupHandler();
                break;
                
            case "userManagerTab":
                UserManagerController userController = (UserManagerController) loadTabContent(
                    tab, "user-manager-tab-view.fxml");
                userController.loadColumn();
                userController.setupDetailForm();
                userController.loadUsers(null, 0);
                userController.loadRole();
                userController.setupHandler();
                break;
                
            default:
                LOGGER.warning("Unknown tab ID: " + tabId);
                return;
        }
        
        // Đánh dấu là đã load tab này
        loadedTabs.put(tabId, true);
    }

    private Object loadTabContent(Tab tab, String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
            getClass().getResource(fxmlFile)));
        Pane content = loader.load();
        tab.setContent(content);
        
        Object controller = loader.getController();
        controllers.put(tab.getId(), controller);
        return controller;
    }

    @FXML
    private void initUI() {
        User currentUser = App.getCurrentUser();
        if (currentUser != null) {
            UIUsernameTextField.setText(currentUser.getUsername());
            UIRoleTextField.setText(currentUser.getRole().getName());
        } else {
            UIUsernameTextField.setText("Unknown");
            UIRoleTextField.setText("Unknown");
        }
    }

    private void showErrorDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Lấy controller của tab theo ID
    public Object getController(String tabId) {
        return controllers.get(tabId);
    }

    private void initEvent() {
        // Sự kiện click nút đăng xuất
        logoutButton.setOnAction(event -> {
            boolean confirm = AlertBox.showConfirmation("Are you sure you want to log out?",
                "You will be redirected to the login screen.");
            App.switchToLogin();
        });
    }
}