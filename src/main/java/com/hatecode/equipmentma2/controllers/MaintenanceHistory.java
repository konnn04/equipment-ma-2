package com.hatecode.equipmentma2.controllers;

import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.impl.EquipmentMaintainanceServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.interfaces.EquipmentMaintainanceService;
import com.hatecode.services.interfaces.MaintenanceService;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

import static com.hatecode.config.AppConfig.PAGE_SIZE;

public class MaintenanceHistory {
    MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    EquipmentMaintainanceService equipmentMaintainanceService = new EquipmentMaintainanceServiceImpl();


    TableView<Maintenance> maintenancesTableViewTable;
    TableView<EquipmentMaintainance> equipmentsTableViewTable;

    TextField equipmentIdTextField;
    TextField equipmentNameTextField;
    TextField equipmentTechnicianTextField;
    TextField startDateTextField;
    TextField endDateTextField;
    TextArea descriptionTextArea;
    TextField priceTextField;
    TextField maintenanceTypeTextField;
    TextField searchMaintenanceTextField;
    DatePicker fromDatePicker;
    DatePicker toDatePicker;
    TextField searchEquipmentTextField;

    public MaintenanceHistory(TableView<Maintenance> maintenancesTableViewTable, TableView<EquipmentMaintainance> equipmentsTableViewTable, TextField equipmentIdTextField, TextField equipmentNameTextField, TextField equipmentTechnicianTextField, TextField startDateTextField, TextField endDateTextField, TextArea descriptionTextArea, TextField priceTextField, TextField maintenanceTypeTextField, TextField searchMaintenanceTextField, DatePicker fromDatePicker, DatePicker toDatePicker, TextField searchEquipmentTextField) {
        this.maintenancesTableViewTable = maintenancesTableViewTable;
        this.equipmentsTableViewTable = equipmentsTableViewTable;
        this.equipmentIdTextField = equipmentIdTextField;
        this.equipmentNameTextField = equipmentNameTextField;
        this.equipmentTechnicianTextField = equipmentTechnicianTextField;
        this.startDateTextField = startDateTextField;
        this.endDateTextField = endDateTextField;
        this.descriptionTextArea = descriptionTextArea;
        this.priceTextField = priceTextField;
        this.maintenanceTypeTextField = maintenanceTypeTextField;
        this.searchMaintenanceTextField = searchMaintenanceTextField;
        this.fromDatePicker = fromDatePicker;
        this.toDatePicker = toDatePicker;
        this.searchEquipmentTextField = searchEquipmentTextField;
    }

    private void initTable() {
        TableColumn<Maintenance, Integer> maintenanceIDColumn = new TableColumn<>("ID");
        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Maintenance, String> maintenanceTitleColumn = new TableColumn<>("Tile");
        maintenanceTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Maintenance, String> maintenanceStartDateColumn = new TableColumn<>("Start Date");

        maintenanceStartDateColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            if (maintenance.getStartDatetime() != null) {
                return new SimpleStringProperty(maintenance.getStartDatetime().toString());
            } else {
                return new SimpleStringProperty("Not found");
            }
        });

        this.maintenancesTableViewTable.getColumns().addAll(maintenanceIDColumn, maintenanceTitleColumn, maintenanceStartDateColumn);

        // Maintenance Equipments  Table
        TableColumn<EquipmentMaintainance, String> equipmentIDColumn = new TableColumn<>("E.ID");
        equipmentIDColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEquipment() != null) {
                return new SimpleStringProperty(String.valueOf(cellData.getValue().getEquipment().getId()));
            } else {
                return new SimpleStringProperty("Not found");
            }
        });

        TableColumn<EquipmentMaintainance, String> equipmentNameColumn = new TableColumn<>("E.Name");
        equipmentNameColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEquipment() != null) {
                return new SimpleStringProperty(cellData.getValue().getEquipment().getName());
            } else {
                return new SimpleStringProperty("Not found");
            }
        });

        this.equipmentsTableViewTable.getColumns().addAll(equipmentIDColumn, equipmentNameColumn);
    }

    private void initSearchFields(){
        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(500));

        this.searchMaintenanceTextField.setOnKeyTyped(keyEvent -> {
            pause.setOnFinished(e -> {
                fetchMaintenanceHistory(searchMaintenanceTextField.getText());
                System.out.println("Search: " + searchMaintenanceTextField.getText());
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

        this.searchEquipmentTextField.setOnKeyTyped(keyEvent -> {
            pause.setOnFinished(e -> {
                try {
                    fetchEquipmentByEMId(maintenancesTableViewTable.getSelectionModel().getSelectedItems().getFirst().getId() ,searchEquipmentTextField.getText());
                    System.out.println("Search: " + searchEquipmentTextField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

    };

    public void fetchMaintenanceHistory(String kw) {
        try {
            List<Maintenance> maintenances;

            if (kw != null && !kw.isEmpty())
                maintenances = this.maintenanceService.getMaintenances(kw, 1, PAGE_SIZE, null, null);
            else
                maintenances = this.maintenanceService.getMaintenances("", 1, PAGE_SIZE, null, null);

            this.maintenancesTableViewTable.setItems(FXCollections.observableList(maintenances));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void fetchEquipmentByEMId(int id, String kw) throws SQLException {
        List<EquipmentMaintainance> equipments;
        if (kw != null && !kw.isEmpty())
            equipments = this.equipmentMaintainanceService.getEquipmentsMaintenanceByEMId(kw, id, 1, PAGE_SIZE);
        else
            equipments = this.equipmentMaintainanceService.getEquipmentsMaintenanceByEMId(null, id, 1, PAGE_SIZE);
        this.equipmentsTableViewTable.setItems(FXCollections.observableList(equipments));
    }

    public void fillEquipmentInfo(EquipmentMaintainance equipmentMaintainance) {

        this.equipmentIdTextField.setText(String.valueOf(equipmentMaintainance.getEquipment().getId()));
        this.equipmentNameTextField.setText(equipmentMaintainance.getEquipment().getName());
        this.equipmentTechnicianTextField.setText(this.maintenancesTableViewTable.getSelectionModel().getSelectedItems().getFirst().getTechnician().getFirstName() + " " + this.maintenancesTableViewTable.getSelectionModel().getSelectedItems().getFirst().getTechnician().getLastName());

        this.startDateTextField.setText(this.maintenancesTableViewTable.getSelectionModel().getSelectedItems().getFirst().getStartDatetime().toString());
        this.endDateTextField.setText(this.maintenancesTableViewTable.getSelectionModel().getSelectedItems().getFirst().getEndDatetime().toString());

        this.priceTextField.setText(String.valueOf(equipmentMaintainance.getPrice()));
        this.descriptionTextArea.setText(equipmentMaintainance.getDescription());


        this.maintenanceTypeTextField.setText(equipmentMaintainance.getMaintenanceType().toString());
    }

    public void initMaintenanceHistory() {
        initSearchFields();
        initTable();
        fetchMaintenanceHistory("");

        this.maintenancesTableViewTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    fetchEquipmentByEMId(newSelection.getId(), null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Selected: " + newSelection.getId());
            }
        });

        this.equipmentsTableViewTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    System.out.println("Selected: " + newSelection.getId());
                    fillEquipmentInfo(newSelection);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });



    }
}
