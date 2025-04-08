package com.hatecode.equipmentma2;

import com.hatecode.pojo.*;
import com.hatecode.equipmentma2.controllers.EquipmentManager;
import com.hatecode.equipmentma2.controllers.MaintenanceManager;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
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
    ComboBox<Object> valueFilterComboBox;

    @FXML
    TextField equipmentIDTextField;

    @FXML
    TextField equipmentCodeTextField;

    @FXML
    TextField equipmentNameTextField;

    @FXML
    ComboBox<Category> equipmentCategoryComboBox;

    @FXML
    Text statusEquipmentText;

    @FXML
    TextArea equipmentDescriptionTextField;

    @FXML
    Button addEquipmentButton;

    @FXML
    Button saveEquipmentButton;

    @FXML
    Button cancelEquipmentButton;

    @FXML
    Button updateEquipmentButton;

    @FXML
    ComboBox<String> modeComboBox;

    @FXML
    Label modeLabel;

    @FXML
    Button changeEquipmentImageButton;

    @FXML
    ImageView equipmentImage;

    @FXML
    TextField regularMaintenanceTimeTextField;

    /* Tab Maintenance */
    @FXML
    TableView<Maintenance> maintenanceTable;

    @FXML
    TextField maintenanceQueryTextField;

    @FXML
    Text lastMaintenanceDateTextField;

    @FXML
    DatePicker nextMaintenanceDatePicker;


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
                equipmentCategoryComboBox,
                statusEquipmentText,
                lastMaintenanceDateTextField,
                nextMaintenanceDatePicker,
                equipmentDescriptionTextField,
                addEquipmentButton,
                saveEquipmentButton,
                cancelEquipmentButton,
                updateEquipmentButton,
                modeComboBox,
                modeLabel,
                changeEquipmentImageButton,
                equipmentImage,
                regularMaintenanceTimeTextField
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
