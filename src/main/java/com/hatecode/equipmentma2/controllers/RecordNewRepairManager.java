package com.hatecode.equipmentma2.controllers;

import com.hatecode.pojo.*;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.services.interfaces.EquipmentMaintenanceService;
import com.hatecode.services.interfaces.MaintenanceService;
import com.hatecode.services.interfaces.UserService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class RecordNewRepairManager {
    private final TableView<Maintenance> recordNewRepairMaintenance;
    private final TextField recordNewRepairSearch;
    private final TableView<EquipmentMaintenance> recordNewRepairMaintenceEquipments;
    private final TextField EquipmentMaintenanceID;
    private final TextField equipmentID;
    private final TextField EquipmentMaintenanceTechnician;
    private final TextArea EquipmentMaintenanceDescription;
    private final DatePicker inspectionDate;
    private final ComboBox<Result> statusComboBox;
    private final Button recordNewRepairSaveButton;

    private Maintenance currentMaintenance;
    private EquipmentMaintenance currentEquipmentMaintenance;



    private MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    private EquipmentMaintenanceService EquipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();

    public RecordNewRepairManager(
            TableView<Maintenance> recordNewRepairMaintenance,
            TextField recordNewRepairSearch,
            TableView<EquipmentMaintenance> recordNewRepairMaintenceEquipments,
            TextField EquipmentMaintenanceID,
            TextField equipmentID,
            TextField EquipmentMaintenanceTechnician,
            TextArea EquipmentMaintenanceDescription,
            DatePicker inspectionDate,
            ComboBox<Result> statusComboBox,
            Button recordNewRepairSaveButton) {
        this.recordNewRepairMaintenance = recordNewRepairMaintenance;
        this.recordNewRepairSearch = recordNewRepairSearch;
        this.recordNewRepairMaintenceEquipments = recordNewRepairMaintenceEquipments;
        this.EquipmentMaintenanceID = EquipmentMaintenanceID;
        this.equipmentID = equipmentID;
        this.EquipmentMaintenanceTechnician = EquipmentMaintenanceTechnician;
        this.EquipmentMaintenanceDescription = EquipmentMaintenanceDescription;
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
                new SimpleStringProperty(cellData.getValue().getStartDateTime().toString())
        );

        TableColumn<Maintenance, String> colPlanEndDate = (TableColumn<Maintenance, String>) recordNewRepairMaintenance.getColumns().get(3);
        colPlanEndDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEndDateTime().toString())
        );
    }

    public void loadColumnEquipmentMaintenance() throws SQLException {
        TableColumn<EquipmentMaintenance, ?> colECodeId = recordNewRepairMaintenceEquipments.getColumns().get(0);
        colECodeId.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn<EquipmentMaintenance, ?> colDescrtion = recordNewRepairMaintenceEquipments.getColumns().get(1);
        colDescrtion.setCellValueFactory(new PropertyValueFactory("description"));

        TableColumn<EquipmentMaintenance, ?> colResult = recordNewRepairMaintenceEquipments.getColumns().get(2);
        colResult.setCellValueFactory(new PropertyValueFactory("result"));

        statusComboBox.getSelectionModel().clearSelection();

        // Get results and ensure no null values
        List<Result> results = Result.getAllResults();
        // Set items after ensuring list is valid
        statusComboBox.setItems(FXCollections.observableArrayList(results));
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
        this.recordNewRepairMaintenceEquipments.getSelectionModel().selectedItemProperty().addListener((obs,oldval,newVal)->{
            if(newVal!=null){
                // code here
                try {
                    showMaintenceEquipmentsDetails(newVal);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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
        List<EquipmentMaintenance> res = EquipmentMaintenanceService.getEquipmentMaintenance(m);
        this.recordNewRepairMaintenceEquipments.setItems(FXCollections.observableList(res));
    }

    public void showMaintenceEquipmentsDetails(EquipmentMaintenance e) throws SQLException {
        UserService userService = new UserServiceImpl();
        this.currentEquipmentMaintenance = e;
        this.EquipmentMaintenanceID.setText(String.valueOf(e.getId()));
        this.equipmentID.setText(String.valueOf(e.getEquipmentId()));
        User technician = userService.getUserById(e.getTechnicianId());
        this.EquipmentMaintenanceTechnician.setText(String.valueOf(technician.getLastName() + " " + technician.getFirstName()));
        // Xử lý java.sql.Date
        LocalDateTime sqlDate = e.getInspectionDate();
        if (sqlDate != null) {
            // Chuyển đổi java.sql.Timestamp ->  LocalDateTime
            LocalDate localDateTime = sqlDate.toLocalDate();
            this.inspectionDate.setValue(localDateTime);
        } else {
            this.inspectionDate.setValue(null); // hoặc set ngày mặc định nếu cần
        }
        this.EquipmentMaintenanceDescription.setText(String.valueOf(e.getDescription()));

        // Thiết lập giá trị status trong comboBox
        if (e.getResult() != null) {
            statusComboBox.getSelectionModel().select(e.getResult());
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
        currentEquipmentMaintenance.setDescription(EquipmentMaintenanceDescription.getText());

        // Cập nhật ngày kiểm tra
        if (inspectionDate.getValue() != null) {
            LocalDate localDate = inspectionDate.getValue();
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
            currentEquipmentMaintenance.setInspectionDate(
                    LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
            );
        } else {
            currentEquipmentMaintenance.setInspectionDate(null);
        }

        // Cập nhật trạng thái
        Result selectedStatus = statusComboBox.getSelectionModel().getSelectedItem();
        currentEquipmentMaintenance.setResult(
                selectedStatus != null ? selectedStatus : null
        );

        // Gọi service để cập nhật vào CSDL
        boolean success = EquipmentMaintenanceService.updateEquipmentMaintenance(currentEquipmentMaintenance);

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
            List<EquipmentMaintenance> updatedList =
                    EquipmentMaintenanceService.getEquipmentMaintenance(currentMaintenance);
            recordNewRepairMaintenceEquipments.setItems(FXCollections.observableList(updatedList));

            // Làm mới danh sách bảo trì
            loadMaintenancesData(recordNewRepairSearch.getText());
        }
    }
}
