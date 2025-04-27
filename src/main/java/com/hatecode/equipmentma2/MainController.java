package com.hatecode.equipmentma2;

import com.hatecode.pojo.*;
import com.hatecode.security.Permission;
import com.hatecode.services.NotificationService;
import com.hatecode.services.impl.NotificationServiceImpl;
import com.hatecode.utils.AlertBox;
import com.hatecode.utils.MaintenanceCheckScheduler;
import com.hatecode.utils.MaintenanceStatusScheduler;

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
    @FXML private Button updateStatusesButton;
    @FXML private Button notificationButton;
    @FXML private Label notificationCountLabel;

    private NotificationService notificationService;
    private final Map<String, Object> controllers = new HashMap<>();
    private final Map<String, Boolean> loadedTabs = new HashMap<>();

    private void initTabs(){
        if (!App.hasPermission(Permission.REPORT_VIEW))
            tabPane.getTabs().remove(reportTab);

        if (!App.hasPermission(Permission.USER_VIEW))
            tabPane.getTabs().remove(userManagerTab);

        if (!App.hasPermission(Permission.MAINTENANCE_SCHEDULE))
            tabPane.getTabs().remove(maintenanceTab);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initializeTabs();
            setupTabChangeListener();
            initUI();
            initEvent();

            // Kiểm tra nếu không phải admin thì ẩn tab quản lý người dùng
            userManagerTab.setDisable(!App.hasPermission(Permission.USER_VIEW));

            // Initialize notification service
            notificationService = new NotificationServiceImpl();

            // Setup notification button
            updateNotificationCount();

            initTabs();
//            userManagerTab.setDisable(!App.hasPermission(Permission.USER_VIEW));

            // Load tab đầu tiên khi ứng dụng khởi động
            loadTab(tabPane.getSelectionModel().getSelectedItem());

            updateStatusesButton.setOnAction(event -> {
                try {
                    // Show a confirmation dialog
                    boolean confirm = AlertBox.showConfirmation(
                        "Update Maintenance Statuses",
                        "Do you want to update all maintenance and equipment statuses now?"
                    );

                    if (confirm) {
                        // Run the status update immediately
                        MaintenanceStatusScheduler.getInstance().runUpdate();
                        MaintenanceCheckScheduler.getInstance().runCheck();

                        // Show success message
                        AlertBox.showInfo(
                            "Status Update",
                            "Maintenance and equipment statuses have been updated successfully."
                        );

                        // Update notification count after status update
                        updateNotificationCount();
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error updating statuses", e);
                    AlertBox.showError("Error", "Failed to update statuses: " + e.getMessage());
                }
            });

            // Set action for notification button
            notificationButton.setOnAction(event -> {
                tabPane.getSelectionModel().select(notificationTab);
            });

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing main controller", e);
            AlertBox.showError("Initialization Error", e.getMessage());
        }
    }

    private void initializeTabs() {
        // Initialize the loadedTabs map with all tabs as not loaded
        loadedTabs.put(equipmentTab.getId(), false);
        loadedTabs.put(maintenanceTab.getId(), false);
        loadedTabs.put(maintenanceHistoryTab.getId(), false);
        loadedTabs.put(recordNewRepairTab.getId(), false);
        loadedTabs.put(userManagerTab.getId(), false);
        loadedTabs.put(reportTab.getId(), false);
        loadedTabs.put(notificationTab.getId(), false);
    }

    private void setupTabChangeListener() {
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                try {
                    // First ensure the tab is loaded initially
                    if (!Boolean.TRUE.equals(loadedTabs.get(newTab.getId()))) {
                        loadTab(newTab);
                    } else {
                        // Tab is already loaded, just refresh its data
                        refreshTabData(newTab);
                    }

                    LOGGER.info("Tab changed to: " + newTab.getId());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error loading tab: " + newTab.getId(), e);
                    AlertBox.showError("Tab Loading Error",
                        "Failed to load the tab content: " + e.getMessage());
                }
            }
        });
    }

    private void loadTab(Tab tab) throws IOException, SQLException {
        LOGGER.info("Loading tab: " + tab.getId());

        switch (tab.getId()) {
            case "equipmentTab":
                EquipmentManagerController e = (EquipmentManagerController) loadTabContent(tab, "equipment-tab-view.fxml");
                e.init();
                break;
            case "maintenanceTab":
                MaintenanceManagerController m = (MaintenanceManagerController) loadTabContent(tab, "maintenance-tab-view.fxml");
                m.init();
                break;
            case "maintenanceHistoryTab":
                MaintenanceHistoryController mh = (MaintenanceHistoryController) loadTabContent(tab, "maintenance-history-tab-view.fxml");
                mh.init();
                break;
            case "recordNewRepairTab":
                RecordNewRepairManagerController r = (RecordNewRepairManagerController) loadTabContent(tab, "record-manager-tab-view.fxml");
                r.init();
                break;
            case "userManagerTab":
                UserManagerController u = (UserManagerController) loadTabContent(tab, "user-manager-tab-view.fxml");
                u.init();
                break;
            case "reportTab":
//                loadTabContent(tab, "report-tab-view.fxml");
                break;
            case "notificationTab":
                NotificationController n = (NotificationController) loadTabContent(tab, "notification-view.fxml");
                n.init();
                break;
        }

        loadedTabs.put(tab.getId(), true);
    }

    private void refreshTabData(Tab tab) throws SQLException, IOException {
        String tabId = tab.getId();
        
        LOGGER.info("Refreshing data for tab: " + tabId);
        
        switch (tabId) {
            case "equipmentTab":
                EquipmentManagerController equipmentController =
                    (EquipmentManagerController) controllers.get(tabId);
                if (equipmentController != null) {
                    equipmentController.refreshData();
                }
                break;
                
            case "maintenanceTab":
                MaintenanceManagerController maintenanceController =
                    (MaintenanceManagerController) controllers.get(tabId);
                if (maintenanceController != null) {
                    maintenanceController.refreshData();
                }
                break;
                
            case "maintenanceHistoryTab":
                MaintenanceHistoryController historyController =
                    (MaintenanceHistoryController) controllers.get(tabId);
                if (historyController != null) {
                    historyController.refreshData();
                }
                break;
                
            case "recordNewRepairTab":
                RecordNewRepairManagerController repairController =
                    (RecordNewRepairManagerController) controllers.get(tabId);
                if (repairController != null) {
                    repairController.refreshData();
                }
                break;
                
            case "userManagerTab":
                UserManagerController userController =
                    (UserManagerController) controllers.get(tabId);
                if (userController != null) {
                    userController.refreshData();
                }
                break;

            case "notificationTab":
                NotificationController notificationController =
                    (NotificationController) controllers.get(tabId);
                if (notificationController != null) {
                    notificationController.refreshData();
                }
                break;
        }
        
        // Update notification count after tab refresh
        updateNotificationCount();
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

    private void initEvent() {
        logoutButton.setOnAction(event -> {
            App.switchToLogin();
        });
    }

    private void updateNotificationCount() {
        try {
            int count = notificationService.countUnreadNotifications();
            if (count > 0) {
                notificationCountLabel.setText(String.valueOf(count));
                notificationCountLabel.setVisible(true);
            } else {
                notificationCountLabel.setVisible(false);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting notifications", e);
        }
    }
}