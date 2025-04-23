package com.hatecode.equipmentma2;

import com.hatecode.pojo.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    TabPane tabPane;

    @FXML
    Tab equipmentTab;

    @FXML
    Tab maintenanceTab;

    @FXML
    Tab maintenanceHistoryTab;

    @FXML
    Tab recordNewRepairTab;

    @FXML
    Tab userManagerTab;
    /* UI */
    @FXML
    Label UIUsernameTextField;

    @FXML
    Label UIRoleTextField;

    @FXML
    private void initUI() {
        User currentUser = App.getCurrentUser();
        System.out.println(currentUser);
        if (currentUser != null) {
            UIUsernameTextField.setText(currentUser.getUsername());
            UIRoleTextField.setText(currentUser.getRole().getName());
        } else {
            UIUsernameTextField.setText("Unknown");
            UIRoleTextField.setText("Unknown");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Nạp content cho các tab
        try {
            // Nạp content cho tab Equipment
            FXMLLoader equipmentLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("equipment-tab-view.fxml")));
            equipmentTab.setContent(equipmentLoader.load());
            EquipmentManagerController equipmentManagerController = equipmentLoader.getController();
            equipmentManagerController.init();
            // Nạp content cho tab Maintenance
            FXMLLoader maintenanceLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("maintenance-tab-view.fxml")));
            maintenanceTab.setContent(maintenanceLoader.load());
            MaintenanceManagerController maintenanceManagerController = maintenanceLoader.getController();
            maintenanceManagerController.fetchMaintenanceTableView();
            maintenanceManagerController.loadColumnMaintenanceTableView();
            // Nạp content cho tab Maintenance History
            FXMLLoader maintenanceHistoryLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("maintenance-history-tab-view.fxml")));
            maintenanceHistoryTab.setContent(maintenanceHistoryLoader.load());
            MaintenanceHistoryController maintenanceHistoryController = maintenanceHistoryLoader.getController();
            maintenanceHistoryController.initMaintenanceHistory();
            // Nạp content cho tab Record new Repair
            FXMLLoader recordNewRepairLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("record-manager-tab-view.fxml")));
            recordNewRepairTab.setContent(recordNewRepairLoader.load());
            RecordNewRepairManagerController recordNewRepairManagerController = recordNewRepairLoader.getController();
            recordNewRepairManagerController.loadColumnMaintenance();
            recordNewRepairManagerController.loadMaintenancesData(null);
            recordNewRepairManagerController.RecordNewRepairSetupHandler();
            // Nạp content cho tab User Management
            FXMLLoader userManagerLoader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("user-manager-tab-view.fxml")));
            userManagerTab.setContent(userManagerLoader.load());
            UserManagerController userManagerController = userManagerLoader.getController();
            userManagerController.loadColumn();
            userManagerController.setupDetailForm();
            userManagerController.loadUsers(null, 0);
            userManagerController.loadRole();
            userManagerController.setupHandler();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
        initUI();
    }
}
