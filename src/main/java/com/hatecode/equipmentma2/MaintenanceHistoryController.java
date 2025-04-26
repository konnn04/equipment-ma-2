package com.hatecode.equipmentma2;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.EquipmentMaintenanceService;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.MaintenanceService;
import com.hatecode.services.UserService;
import com.hatecode.utils.AlertBox;
import com.hatecode.utils.FormatDate;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static com.hatecode.config.AppConfig.MAX_DATE;
import static com.hatecode.config.AppConfig.MIN_DATE;
import static com.hatecode.utils.FormatDate.DATE_FORMATTER;

public class MaintenanceHistoryController {
    MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    EquipmentMaintenanceService equipmentMaintainanceService = new EquipmentMaintenanceServiceImpl();

    Maintenance currentMaintenance;

    @FXML
    TableView<Maintenance> maintenancesTableViewTable;
    @FXML
    TableView<EquipmentMaintenance> equipmentsTableViewTable;
    @FXML
    TextField equipmentIdTextField;
    @FXML
    TextField equipmentNameTextField;
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
    TextField totalPriceTextField;
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

    private void initTable() {
        TableColumn<Maintenance, Integer> maintenanceIDColumn = new TableColumn<>("ID");
        maintenanceIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Maintenance, String> maintenanceTitleColumn = new TableColumn<>("Tile");
        maintenanceTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Maintenance, String> maintenanceStartDateColumn = new TableColumn<>("Start Date");

        maintenanceStartDateColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            if (maintenance.getStartDateTime() != null) {
                return new SimpleStringProperty(DATE_FORMATTER.format(maintenance.getStartDateTime()));
            } else {
                return new SimpleStringProperty("Not found");
            }
        });

        this.maintenancesTableViewTable.getColumns().addAll(maintenanceIDColumn, maintenanceTitleColumn, maintenanceStartDateColumn);

        // Maintenance Equipments  Table
        TableColumn<EquipmentMaintenance, String> equipmentIDColumn = new TableColumn<>("E.ID");
        equipmentIDColumn.setCellValueFactory(
                new PropertyValueFactory<>("equipmentId"));
        equipmentIDColumn.prefWidthProperty().bind(equipmentsTableViewTable.widthProperty().multiply(0.5));

        TableColumn<EquipmentMaintenance, String> equipmentNameColumn = new TableColumn<>("E.Name");
        equipmentNameColumn.prefWidthProperty().bind(equipmentsTableViewTable.widthProperty().multiply(0.5));

//        equipmentNameColumn.setCellValueFactory(cellData -> {
//            EquipmentService equipmentService = new EquipmentServiceImpl();
//            Equipment equipment = null;
//            try {
//                equipment = equipmentService.getEquipmentById(cellData.getValue().getEquipmentId());
//                if (equipment!= null) {
//                    return new SimpleStringProperty(equipment.getDescription());
//                } else {
//                    return new SimpleStringProperty("Not found");
//                }
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//
//        });
        equipmentNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        this.equipmentsTableViewTable.getColumns().addAll(equipmentIDColumn, equipmentNameColumn);
    }

    private void initSearchFields(){

        PauseTransition pause = new PauseTransition(javafx.util.Duration.millis(1000));

        this.searchMaintenanceTextField.setOnKeyTyped(keyEvent -> {
            pause.setOnFinished(e -> {
                fetchMaintenanceHistory(searchMaintenanceTextField.getText());
                System.out.println("Search: " + searchMaintenanceTextField.getText());
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

        this.searchEquipmentTextField.setOnKeyTyped(keyEvent -> {
            pause.setOnFinished(e -> {
                if (currentMaintenance == null) {
                    Platform.runLater(() -> AlertBox.showError("Choose a maintenance first", "Please choose a maintenance first"));
                    return;
                }

                try {
                    fetchEquipmentByEMId(currentMaintenance.getId() ,searchEquipmentTextField.getText());
                    System.out.println("Search: " + searchEquipmentTextField.getText());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

        // Init date picker
        fromDatePicker.setConverter(new javafx.util.StringConverter<>() {

            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FORMATTER.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, DATE_FORMATTER) : null;
            }
        });

        toDatePicker.setConverter(new javafx.util.StringConverter<>() {

            @Override
            public String toString(LocalDate date) {
                return date != null ? DATE_FORMATTER.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                return string != null && !string.isEmpty() ? LocalDate.parse(string, DATE_FORMATTER) : null;
            }
        });

        this.startDateTextField.setText(LocalDate.now().toString());
        this.endDateTextField.setText(LocalDate.now().toString());

        this.fromDatePicker.setOnAction(keyEvent -> {
            pause.setOnFinished(e -> {
                fetchMaintenanceHistory(searchMaintenanceTextField.getText());
                System.out.println("Search: " + searchMaintenanceTextField.getText());
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

        this.toDatePicker.setOnAction(keyEvent -> {
            pause.setOnFinished(e -> {
                fetchMaintenanceHistory(searchMaintenanceTextField.getText());
                System.out.println("Search: " + searchMaintenanceTextField.getText());
            });
            pause.playFromStart(); // Reset thời gian mỗi khi có sự kiện mới
        });

    };

    public void fetchMaintenanceHistory(String kw) {
        try {
            List<Maintenance> maintenances;
            kw = kw == null ? "" : kw.trim();
            if (kw.length() > 255){
                Platform.runLater(() -> AlertBox.showError("Keyword too long", "Please enter a keyword less than 255 characters"));
                return;
            }

            LocalDate fromDate = this.fromDatePicker.getValue() != null ? this.fromDatePicker.getValue() : null;
            LocalDate toDate = this.toDatePicker.getValue() != null ? this.toDatePicker.getValue().plusDays(1) : null;

            if (fromDate != null && toDate == null ) {
                toDate = LocalDate.now().plusDays(1);
            }

            if (fromDate != null && toDate != null) {
                if (fromDate.isBefore(MIN_DATE) || toDate.isAfter(MAX_DATE) ||fromDate.isAfter(toDate)) {
                    Platform.runLater(() -> AlertBox.showError("Date out of range", "Please choose a date between " + MIN_DATE + " and " + MAX_DATE));
                    return;
                }
            }

            maintenances = this.maintenanceService.getMaintenances(kw, fromDate, toDate);

            this.maintenancesTableViewTable.setItems(FXCollections.observableList(maintenances));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void fetchEquipmentByEMId(int id, String kw) throws SQLException {
        List<EquipmentMaintenance> equipments;
        kw = kw == null ? "" : kw.trim();

        if (kw.length() > 255){
            Platform.runLater(() -> AlertBox.showError("Keyword too long", "Please enter a keyword less than 255 characters"));
            return;
        }

        if (kw != null && !kw.isEmpty())
            equipments = this.equipmentMaintainanceService.getEquipmentsMaintenanceByEMId(kw, id);
        else
            equipments = this.equipmentMaintainanceService.getEquipmentsMaintenanceByEMId(null, id);

        this.equipmentsTableViewTable.setItems(FXCollections.observableList(equipments));
    }

    public void fillEquipmentInfo(EquipmentMaintenance equipmentMaintainance) throws SQLException {
        EquipmentService equipmentService = new EquipmentServiceImpl();
        Equipment equipment =  equipmentService.getEquipmentById(equipmentMaintainance.getEquipmentId());
        UserService userService = new UserServiceImpl();

        this.equipmentIdTextField.setText(String.valueOf(equipment.getId()));
        this.equipmentNameTextField.setText(equipment.getName());
        this.equipmentTechnicianTextField.setText(userService.getUserById(equipmentMaintainance.getTechnicianId()).getLastName());

        this.startDateTextField.setText(DATE_FORMATTER.format(currentMaintenance.getStartDateTime()));
        this.endDateTextField.setText(DATE_FORMATTER.format(currentMaintenance.getEndDateTime()));

        this.priceTextField.setText(String.valueOf(equipmentMaintainance.getRepairPrice()));
        this.descriptionTextArea.setText(equipmentMaintainance.getDescription());


        this.maintenanceTypeTextField.setText(equipmentMaintainance.getResult().getName());
    }

    public void initMaintenanceHistory() {
        initSearchFields();
        initTable();
        fetchMaintenanceHistory("");

        this.maintenancesTableViewTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                try {
                    currentMaintenance = newSelection;
                    fetchEquipmentByEMId(newSelection.getId(), null);

                    totalPriceTextField.setText(String.valueOf(equipmentMaintainanceService.getTotalPriceEquipmentMaintenance(currentMaintenance.getId())));

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

    public void refreshData() {

    }

    public void init() {
        initMaintenanceHistory();
        initSearchFields();
        fetchMaintenanceHistory("");
        this.maintenancesTableViewTable.getSelectionModel().selectFirst();
        if (this.maintenancesTableViewTable.getSelectionModel().getSelectedItem() != null) {
            try {
                fetchEquipmentByEMId(this.maintenancesTableViewTable.getSelectionModel().getSelectedItem().getId(), null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
