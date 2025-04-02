package com.hatecode.equipmentma2.controllers;

import com.hatecode.models.Maintenance;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.interfaces.MaintenanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class MaintenanceManager {
    private final MaintenanceService maintenanceService = new MaintenanceServiceImpl();

    TableView<Maintenance> maintenanceTable;
    TextField maintenanceQueryTextField;

    public MaintenanceManager(
            TableView<Maintenance> maintenanceTable,
            TextField maintenanceQueryTextField
    ) {
        this.maintenanceTable = maintenanceTable;
        this.maintenanceQueryTextField = maintenanceQueryTextField;
    }

    public void fetchMaintenanceTableView() throws SQLException {
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

    public void loadColumnMaintenanceTableView() {
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
}
