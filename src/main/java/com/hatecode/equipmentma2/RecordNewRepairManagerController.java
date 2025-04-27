package com.hatecode.equipmentma2;

import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.Result;
import com.hatecode.pojo.User;
import com.hatecode.services.EquipmentMaintenanceService;
import com.hatecode.services.MaintenanceService;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

public class RecordNewRepairManagerController {
    @FXML
    TableView<Maintenance> recordNewRepairMaintenance;
    @FXML
    TextField recordNewRepairSearch;
    @FXML
    TableView<EquipmentMaintenance> recordNewRepairMaintenceEquipments;
    @FXML
    TextField equipmentMaintenanceID;
    @FXML
    TextField equipmentID;
    @FXML
    TextField equipmentMaintenanceTechnician;
    @FXML
    TextField equipmentMaintenancePrice;
    @FXML
    TextArea equipmentMaintenanceDescription;
    @FXML
    DatePicker inspectionDate;
    @FXML
    ComboBox<Result> statusComboBox;
    @FXML
    Button recordNewRepairSaveButton;

    private Maintenance currentMaintenance;
    private EquipmentMaintenance currentEquipmentMaintenance;

    private MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    private EquipmentMaintenanceService EquipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();

    public void loadColumnMaintenance() {
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

        TableColumn<EquipmentMaintenance, ?> colEName = recordNewRepairMaintenceEquipments.getColumns().get(1);
        colEName.setCellValueFactory(new PropertyValueFactory("equipmentName"));

        TableColumn<EquipmentMaintenance, ?> colResult = recordNewRepairMaintenceEquipments.getColumns().get(2);
        colResult.setCellValueFactory(new PropertyValueFactory("result"));

        TableColumn<EquipmentMaintenance, ?> colDescrtion = recordNewRepairMaintenceEquipments.getColumns().get(3);
        colDescrtion.setCellValueFactory(new PropertyValueFactory("description"));

        statusComboBox.getSelectionModel().clearSelection();

        // Get results and ensure no null values
        List<Result> results = Result.getAllResults();
        // Set items after ensuring list is valid
        statusComboBox.setItems(FXCollections.observableArrayList(results));
    }

    public void loadMaintenancesData(String query) throws SQLException {
        List<Maintenance> res = maintenanceService.getCurrentMaintenances(query);
        this.recordNewRepairMaintenance.setItems(FXCollections.observableList(res));
    }

    public void RecordNewRepairSetupHandler() {
        // Tìm kiếm lịch bảo trì
        this.recordNewRepairSearch.textProperty().addListener(e -> {
            try {
                loadMaintenancesData(this.recordNewRepairSearch.getText());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        // Hiển thị chi tiết các thiết bị trong plan
        this.recordNewRepairMaintenance.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newval) -> {
            if (newval != null) {
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
        this.recordNewRepairMaintenceEquipments.getSelectionModel().selectedItemProperty().addListener((obs, oldval, newVal) -> {
            if (newVal != null) {
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

        // Xử lý thay đổi trạng thái
        this.statusComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (newVal.getName().equals("Need repair")) {
                    equipmentMaintenancePrice.clear();
                    equipmentMaintenancePrice.setDisable(false);
                } else {
                    equipmentMaintenancePrice.setDisable(true);
                    equipmentMaintenancePrice.setText("0");
                }
            } else {
                equipmentMaintenancePrice.setDisable(true);
                equipmentMaintenancePrice.setText("0");
            }
        });

    }

    public void showMaintenanceDetails(Maintenance m) throws SQLException {
        this.currentMaintenance = m;
        User currentUser = App.getCurrentUser();
        List<EquipmentMaintenance> res = EquipmentMaintenanceService.getEquipmentMaintenance(m,currentUser);
        this.recordNewRepairMaintenceEquipments.setItems(FXCollections.observableList(res));
    }

    public void showMaintenceEquipmentsDetails(EquipmentMaintenance e) throws SQLException {
        UserService userService = new UserServiceImpl();
        this.currentEquipmentMaintenance = e;
        this.equipmentMaintenanceID.setText(String.valueOf(e.getId()));
        this.equipmentID.setText(String.valueOf(e.getEquipmentId()));
        User technician = userService.getUserById(e.getTechnicianId());
        if(technician != null){
            System.out.println(technician.getId());
        }
        else{
            System.out.println("No user found");
        }
        this.equipmentMaintenanceTechnician.setText(String.valueOf(technician.getLastName() + " " + technician.getFirstName()));
        // Xử lý
        LocalDateTime localDateTime = e.getInspectionDate();
        if (localDateTime != null) {
            this.inspectionDate.setValue(LocalDate.from(localDateTime));
        } else {
            this.inspectionDate.setValue(null); // hoặc set ngày mặc định nếu cần
        }
        this.equipmentMaintenanceDescription.setText(String.valueOf(e.getDescription()));

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
        currentEquipmentMaintenance.setDescription(equipmentMaintenanceDescription.getText());
        if(currentEquipmentMaintenance.getResult().equals(Result.NEED_REPAIR)){
            currentEquipmentMaintenance.setRepairPrice(Float.parseFloat(equipmentMaintenancePrice.getText()));
        }

        // Cập nhật ngày kiểm tra
        if (inspectionDate.getValue() != null) {
            LocalDate localDate = inspectionDate.getValue();
            Instant instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
//            currentEquipmentMaintenance.setInspectionDate(
//                    LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
//            );
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
            alert.setTitle("Success");
            alert.setHeaderText("Updated successfully");
            alert.setContentText("Maintenance equipment information was updated successfully.");
            alert.showAndWait();

            // Làm mới dữ liệu
            refreshData();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred while updating the maintenance equipment.");
            alert.setContentText("");
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
