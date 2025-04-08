package com.hatecode.equipmentma2;

import com.hatecode.equipmentma2.controllers.*;
import com.hatecode.pojo.*;
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
import java.util.ResourceBundle;

public class MainController implements Initializable {
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


    /* Tab Record new Repair*/
    @FXML
    TableView<Maintenance> recordNewRepairMaintance;

    @FXML
    TextField recordNewRepairSearch;

    @FXML
    TableView<EquipmentMaintenance> recordNewRepairMaintanceEquipments;

    @FXML
    TextField equipmentMaintainanceID;

    @FXML
    TextField equipmentID;

    @FXML
    TextField equipmentMaintainanceTechnician;

    @FXML
    TextArea equipmentMaintainanceDescription;

    @FXML
    DatePicker inspectionDate;

    @FXML
    ComboBox<Result> statusComboBox;

    @FXML
    Button recordNewRepairSaveButton;


    // Tab User Management
    @FXML
    private TableView<User> users;

    @FXML
    private ComboBox<Role> roles;

    @FXML
    private TextField txtSearchUser;

    // Các control cho phần chi tiết users
    @FXML
    private TextField userIdField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<Role> roleComboBox;
    @FXML
    private CheckBox isActiveCheckBox;
    @FXML
    private ImageView avatarImageView;
    @FXML
    private Button changeAvatarButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button newUserButton;
    @FXML
    private Button deleteUserButton;

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

        RecordNewRepairManager recordNewRepairManager = new RecordNewRepairManager(
                recordNewRepairMaintance,
                recordNewRepairSearch,
                recordNewRepairMaintanceEquipments,
                equipmentMaintainanceID,
                equipmentID,
                equipmentMaintainanceTechnician,
                equipmentMaintainanceDescription,
                inspectionDate,
                statusComboBox,
                recordNewRepairSaveButton
        );

        UserManager userManager = new UserManager(
                users,
                roles,
                txtSearchUser,
                userIdField,
                firstNameField,
                lastNameField,
                usernameField,
                passwordField,
                emailField,
                phoneField,
                roleComboBox,
                isActiveCheckBox,
                avatarImageView,
                changeAvatarButton,
                saveButton,
                newUserButton,
                deleteUserButton
        );
        try {
            userManager.loadColumn();
            userManager.setupDetailForm();

            userManager.loadUsers(null, 0);
            userManager.loadRole();
            userManager.setupHandler();


            maintenanceManager.loadColumnMaintenanceTableView();
            maintenanceManager.fetchMaintenanceTableView();

            recordNewRepairManager.loadColumnMaintenance();
            recordNewRepairManager.loadMaintenancesData(null);
            recordNewRepairManager.RecordNewRepairSetupHandler();

            equipmentManager.init();

            initUI();
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

    @FXML
    TableView<Maintenance> maintenancesTableViewTable;

    @FXML
    TableView<EquipmentMaintenance> equipmentsTableViewTable;

    @FXML
    TextField equipmentIdTextField;
    @FXML
    TextField historyEquipmentNameTextField;
    @FXML
    TextField equipmentTechnicianTextField;
    @FXML
    TextField startDateTextField;
    @FXML
    TextField endDateTextField;
    @FXML
    TextArea descriptionTextArea;
    @FXML
    TextField priceTextField;
    @FXML
    TextField maintenanceTypeTextField;
    @FXML
    TextField searchMaintenanceTextField;
    @FXML
    DatePicker fromDatePicker;
    @FXML
    DatePicker toDatePicker;
    @FXML
    TextField searchEquipmentTextField;

    public void openMaintenanceHistoryHandler() {
        MaintenanceHistory maintenanceHistory = new MaintenanceHistory(
                maintenancesTableViewTable,
                equipmentsTableViewTable,
                equipmentIdTextField,
                historyEquipmentNameTextField,
                equipmentTechnicianTextField,
                startDateTextField,
                endDateTextField,
                descriptionTextArea,
                priceTextField,
                maintenanceTypeTextField,
                searchMaintenanceTextField,
                fromDatePicker,
                toDatePicker,
                searchEquipmentTextField
        );
        maintenanceHistory.initMaintenanceHistory();
    }
}
