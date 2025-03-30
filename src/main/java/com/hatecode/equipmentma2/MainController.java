package com.hatecode.equipmentma2;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.Status;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.services.interfaces.MaintenanceService;
import com.hatecode.services.interfaces.StatusService;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.StatusServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class MainController  implements Initializable {
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
    ComboBox<Status> statusEquipmentComboBox;

    /* Tab Maintenance */
    @FXML
    TableView<Maintenance> maintenanceTable;

    @FXML
    TextField maintenanceQueryTextField;

    private final EquipmentService equipmentService = new EquipmentServiceImpl();
    private final MaintenanceService maintenanceService = new MaintenanceServiceImpl();

    private void loadColumnEquipmentTableView() throws SQLException {
        TableColumn<Equipment, String> equipmentIDColumn = new TableColumn<>("Code");
        equipmentIDColumn.setPrefWidth(100);
        equipmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Equipment, String> equipmentNameColumn = new TableColumn<>("Name");
        equipmentNameColumn.setPrefWidth(200);
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Equipment, String> equipmentTypeColumn = new TableColumn<>("Status");
        equipmentTypeColumn.setPrefWidth(200);
        equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("statusName"));

        equipmentTable.getColumns().addAll(equipmentIDColumn, equipmentNameColumn, equipmentTypeColumn);

        StatusService statusService = new StatusServiceImpl();
        List<Status> statusList = statusService.getStatuses();
        statusEquipmentComboBox.setItems(FXCollections.observableList(statusList));

        maintenanceTable.setPlaceholder(new Label("No data available"));
        maintenanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Equipment selectedEquipment = newSelection;
                praseSelectedEquipmentData(selectedEquipment);
            }
        });
    }

    private void fetchEquipmentTableView() throws SQLException {
        List<Equipment> equipments;
        equipments = equipmentService.getEquipments();
//        equipmentTable.getItems().clear();
        equipmentTable.setItems(FXCollections.observableList(equipments));

        equipmentQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Equipment> filteredEquipments = equipmentService.getEquipments(newValue, 0, 100, null, null);
                equipmentTable.setItems(FXCollections.observableList(filteredEquipments));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }

    private void praseSelectedEquipmentData(Equipment selectedEquipment) {
        equipmentIDTextField.textProperty().set(String.valueOf(selectedEquipment.getId()));
        equipmentCodeTextField.textProperty().set(selectedEquipment.getCode());
        equipmentNameTextField.textProperty().set(selectedEquipment.getName());
        statusEquipmentComboBox.getSelectionModel().select(selectedEquipment.getStatus());
        /* More data to be added */
    }

    /*     * Maintenance Table
     */

    private void loadColumnMaintenanceTableView() {
        TableColumn<Maintenance, String> maintenanceStringTableColumn = new TableColumn<>("ID");
        maintenanceStringTableColumn.setPrefWidth(100);
        maintenanceStringTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Maintenance, String> maintenanceTitleTableColumn = new TableColumn<>("Title");
        maintenanceTitleTableColumn.setPrefWidth(200);
        maintenanceTitleTableColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Maintenance, String> maintenanceStartDateTableColumn = new TableColumn<>("Start Date");
        maintenanceStartDateTableColumn.setPrefWidth(200);
        maintenanceStartDateTableColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            if (maintenance.getStartDatetime() != null) {
                return new SimpleStringProperty(maintenance.getStartDatetime().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        TableColumn<Maintenance, String> maintenanceEndDateTableColumn = new TableColumn<>("End Date");
        maintenanceEndDateTableColumn.setPrefWidth(200);
        maintenanceEndDateTableColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            if (maintenance.getEndDatetime() != null) {
                return new SimpleStringProperty(maintenance.getEndDatetime().toString());
            } else {
                return new SimpleStringProperty("");
            }
        });

        maintenanceTable.getColumns().addAll(maintenanceStringTableColumn, maintenanceTitleTableColumn, maintenanceStartDateTableColumn, maintenanceEndDateTableColumn);

        maintenanceTable.setPlaceholder(new Label("No data available"));
        maintenanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }

    private void fetchMaintenanceTableView() throws SQLException {
        List<Maintenance> maintenances;
        maintenances = maintenanceService.getMaintenances();
        maintenanceTable.setItems(FXCollections.observableList(maintenances));
        maintenanceQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                List<Maintenance> filteredMaintenances = maintenanceService.getMaintenances(newValue, 0, 100, null, null);
                maintenanceTable.setItems(FXCollections.observableList(filteredMaintenances));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadColumnEquipmentTableView();
            fetchEquipmentTableView();
            loadColumnMaintenanceTableView();
            fetchMaintenanceTableView();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
