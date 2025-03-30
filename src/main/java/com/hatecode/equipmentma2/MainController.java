package com.hatecode.equipmentma2;

import com.hatecode.equipmentma2.controllers.EquipmentManager;
import com.hatecode.equipmentma2.controllers.MaintenanceManager;
import com.hatecode.pojo.*;
import com.hatecode.services.interfaces.MaintenanceService;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MainController  implements Initializable {
    /* UI */
    @FXML
    Label UIUsernameTextField;

    @FXML
    Label UIRoleTextField;

    /* Tab Equipment */
    @FXML
    TableView<Equipment> equipmentTable;

    @FXML
    TextField equipmentQueryTextField;

    @FXML
    ComboBox<String> typeFilterComboBox;

    @FXML
    ComboBox<BaseObject> valueFilterComboBox;

    @FXML
    TextField equipmentIDTextField;

    @FXML
    TextField equipmentCodeTextField;

    @FXML
    TextField equipmentNameTextField;

    @FXML
    ComboBox<Status> statusEquipmentComboBox;

    @FXML
    TextArea equipmentDescriptionTextField;

    @FXML
    Button addEquipmentButton;

    /* Tab Maintenance */
    @FXML
    TableView<Maintenance> maintenanceTable;

    @FXML
    TextField maintenanceQueryTextField;

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
        EquipmentManager equipmentManager = new EquipmentManager(
                equipmentTable,
                equipmentQueryTextField,
                typeFilterComboBox,
                valueFilterComboBox,
                equipmentIDTextField,
                equipmentCodeTextField,
                equipmentNameTextField,
                statusEquipmentComboBox,
                equipmentDescriptionTextField,
                addEquipmentButton
        );

        MaintenanceManager maintenanceManager = new MaintenanceManager(
                maintenanceTable,
                maintenanceQueryTextField
        );
        try {
            initUI();
            equipmentManager.init();

            maintenanceManager.loadColumnMaintenanceTableView();
            maintenanceManager.fetchMaintenanceTableView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void openPopup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addEquipment-view.fxml"));
            VBox root = loader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Popup Window");
            popupStage.setScene(new Scene(root));

            AddEquipmentController addEquipmentController = loader.getController();
            addEquipmentController.setStage(popupStage);

            popupStage.showAndWait();

            ObservableList<Equipment> result = addEquipmentController.getResult();
            if (result != null) {
                System.out.println(result.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
