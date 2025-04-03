package com.hatecode.equipmentma2.controllers;

import com.hatecode.pojo.EquipmentMaintainance;
import com.hatecode.pojo.EquipmentMaintenanceStatus;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.impl.EquipmentMaintainanceServiceImpl;
import com.hatecode.services.impl.EquipmentMaintenanceStatusServicesImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.interfaces.EquipmentMaintainanceService;
import com.hatecode.services.interfaces.EquipmentMaintenanceStatusServices;
import com.hatecode.services.interfaces.MaintenanceService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class RecordNewRepairManager {
    private final TableView<Maintenance> recordNewRepairMaintenance;
    private final TextField recordNewRepairSearch;
    private final TableView<EquipmentMaintainance> recordNewRepairMaintanceEquipments;
    private final TextField equipmentMaintainanceID;
    private final TextField equipmentID;
    private final TextField equipmentMaintainanceTechnician;
    private final TextArea equipmentMaintainanceDescription;
    private final DatePicker inspectionDate;
    private final ComboBox<EquipmentMaintenanceStatus> statusComboBox;
    private final Button recordNewRepairSaveButton;

    private Maintenance currentMaintenance;
    private EquipmentMaintainance currentEquipmentMaintenance;



    private MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    private EquipmentMaintainanceService equipmentMaintainanceService = new EquipmentMaintainanceServiceImpl();
    private EquipmentMaintenanceStatusServices equipmentMaintenanceStatusServices = new EquipmentMaintenanceStatusServicesImpl();

    public RecordNewRepairManager(TableView<Maintenance> recordNewRepairMaintenance, TextField recordNewRepairSearch, TableView<EquipmentMaintainance> recordNewRepairMaintanceEquipments, TextField equipmentMaintainanceID, TextField equipmentID, TextField equipmentMaintainanceTechnician, TextArea equipmentMaintainanceDescription, DatePicker inspectionDate, ComboBox<EquipmentMaintenanceStatus> statusComboBox, Button recordNewRepairSaveButton) {
        this.recordNewRepairMaintenance = recordNewRepairMaintenance;
        this.recordNewRepairSearch = recordNewRepairSearch;
        this.recordNewRepairMaintanceEquipments = recordNewRepairMaintanceEquipments;
        this.equipmentMaintainanceID = equipmentMaintainanceID;
        this.equipmentID = equipmentID;
        this.equipmentMaintainanceTechnician = equipmentMaintainanceTechnician;
        this.equipmentMaintainanceDescription = equipmentMaintainanceDescription;
        this.inspectionDate = inspectionDate;
        this.statusComboBox = statusComboBox;
        this.recordNewRepairSaveButton = recordNewRepairSaveButton;
    }

    public void loadColumnMaintenance(){
        TableColumn colPlanId = recordNewRepairMaintenance.getColumns().get(0);
        colPlanId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colPlanName = recordNewRepairMaintenance.getColumns().get(1);
        colPlanName.setCellValueFactory(new PropertyValueFactory("title"));

        TableColumn<Maintenance, String> colPlanStartDate = (TableColumn<Maintenance, String>) recordNewRepairMaintenance.getColumns().get(2);
        colPlanStartDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getStartDatetime().toString())
        );

        TableColumn<Maintenance, String> colPlanEndDate = (TableColumn<Maintenance, String>) recordNewRepairMaintenance.getColumns().get(3);
        colPlanEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDatetime().toString())
        );
    }

    public void loadColumnEquipmentMaintenance() throws SQLException {
        TableColumn colECodeId = recordNewRepairMaintanceEquipments.getColumns().get(0);
        colECodeId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn colDescrtion = recordNewRepairMaintanceEquipments.getColumns().get(1);
        colDescrtion.setCellValueFactory(new PropertyValueFactory("description"));

        TableColumn<EquipmentMaintainance, String> colStatus = (TableColumn<EquipmentMaintainance, String>)recordNewRepairMaintanceEquipments.getColumns().get(2);
        colStatus.setCellValueFactory(cellData ->
            new SimpleStringProperty(cellData.getValue().getEquipmentMaintenanceStatus().getName())
        );
        List<EquipmentMaintenanceStatus> statusData = equipmentMaintenanceStatusServices.getEquipmentMaintenanceStatus();
        this.statusComboBox.setItems(FXCollections.observableList(statusData));
    }

    public void loadMaintenancesData(String query) throws SQLException {
        List<Maintenance> res = maintenanceService.getMaintenances(query);
        this.recordNewRepairMaintenance.setItems(FXCollections.observableList(res));
    }

    public void RecordNewRepairSetupHandler(){
        // Tìm kiếm lịch bảo trì
        this.recordNewRepairSearch.textProperty().addListener(e -> {
            try {
                loadMaintenancesData(this.recordNewRepairSearch.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Hiển thị chi tiết các thiết bị trong plan
        this.recordNewRepairMaintenance.getSelectionModel().selectedItemProperty().addListener((obs,oldVal,newval)->{
            if(newval!=null){
                // Hiển thị bảng thiết bị trong plan
                try {
                    loadColumnEquipmentMaintenance();
                    this.showMaintenanceDetails(newval);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Hiển thị chi EquipmentMaintenance
        this.recordNewRepairMaintanceEquipments.getSelectionModel().selectedItemProperty().addListener((obs,oldval,newVal)->{
            if(newVal!=null){
                // code here
                showMaintanceEquipmentsDetails(newVal);
            }
        });

        // Xử lý sự kiện khi nhấn nút save
        this.recordNewRepairSaveButton.setOnAction(event -> {
            try {
                saveEquipmentMaintenance();
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Hiển thị thông báo lỗi cho người dùng
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Lỗi");
                alert.setHeaderText("Không thể lưu thông tin");
                alert.setContentText("Đã xảy ra lỗi khi lưu thông tin bảo trì: " + ex.getMessage());
                alert.showAndWait();
            }
        });

    }

    public void showMaintenanceDetails(Maintenance m) throws SQLException {
        this.currentMaintenance = m;
        List<EquipmentMaintainance> res = equipmentMaintainanceService.getEquipmentMaintainance(m);
        this.recordNewRepairMaintanceEquipments.setItems(FXCollections.observableList(res));
    }

    public void showMaintanceEquipmentsDetails(EquipmentMaintainance e){
        this.currentEquipmentMaintenance = e;
        this.equipmentMaintainanceID.setText(String.valueOf(e.getId()));
        this.equipmentID.setText(String.valueOf(e.getEquipmentId()));
        this.equipmentMaintainanceTechnician.setText(String.valueOf(e.getTechnician().getUsername()));
        // Xử lý java.sql.Date
        Date sqlDate = e.getInspectionDate();
        if (sqlDate != null) {
            // Chuyển đổi java.sql.Date -> java.util.Date -> LocalDate
            java.util.Date utilDate = new java.util.Date(sqlDate.getTime());
            LocalDate localDate = utilDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            this.inspectionDate.setValue(localDate);
        } else {
            this.inspectionDate.setValue(null); // hoặc set ngày mặc định nếu cần
        }
        this.equipmentMaintainanceDescription.setText(String.valueOf(e.getDescription()));

        // Thiết lập giá trị status trong comboBox
        if (e.getEquipmentMaintenanceStatus() != null) {
            statusComboBox.getSelectionModel().select(e.getEquipmentMaintenanceStatus());
        } else {
            statusComboBox.getSelectionModel().clearSelection();
        }
    }

    public void saveEquipmentMaintenance() throws SQLException {
        if (currentEquipmentMaintenance == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Cảnh báo");
            alert.setHeaderText("Không có thiết bị được chọn");
            alert.setContentText("Vui lòng chọn một thiết bị để cập nhật thông tin");
            alert.showAndWait();
            return;
        }

        // Cập nhật thông tin từ các control vào đối tượng hiện tại
        currentEquipmentMaintenance.setDescription(equipmentMaintainanceDescription.getText());

        // Cập nhật ngày kiểm tra
        if (inspectionDate.getValue() != null) {
            LocalDate localDate = inspectionDate.getValue();
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            currentEquipmentMaintenance.setInspectionDate(Date.from(instant));
        } else {
            currentEquipmentMaintenance.setInspectionDate(null);
        }

        // Cập nhật trạng thái
        EquipmentMaintenanceStatus selectedStatus = statusComboBox.getSelectionModel().getSelectedItem();
        currentEquipmentMaintenance.setEquipmentMaintenanceStatus(selectedStatus);

        // Gọi service để cập nhật vào CSDL
        boolean success = equipmentMaintainanceService.updateEquipmentMaintainance(currentEquipmentMaintenance);

        if (success) {
            // Hiển thị thông báo thành công
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công");
            alert.setHeaderText("Cập nhật thành công");
            alert.setContentText("Thông tin bảo trì đã được cập nhật");
            alert.showAndWait();

            // Làm mới dữ liệu
            refreshData();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể cập nhật");
            alert.setContentText("Đã xảy ra lỗi khi cập nhật thông tin bảo trì");
            alert.showAndWait();
        }
    }

    private void refreshData() throws SQLException {
        if (currentMaintenance != null) {
            // Làm mới danh sách thiết bị bảo trì
            List<EquipmentMaintainance> updatedList =
                    equipmentMaintainanceService.getEquipmentMaintainance(currentMaintenance);
            recordNewRepairMaintanceEquipments.setItems(FXCollections.observableList(updatedList));

            // Làm mới danh sách bảo trì
            loadMaintenancesData(recordNewRepairSearch.getText());
        }
    }
}
