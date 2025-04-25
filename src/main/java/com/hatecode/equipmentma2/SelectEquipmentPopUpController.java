package com.hatecode.equipmentma2;

import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.utils.AlertBox;
import com.hatecode.utils.FormatDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectEquipmentPopUpController {
    @FXML private TableView<Equipment> availableEquipmentTableView;
    @FXML private TableView<Equipment> selectedEquipmentTableView;
    @FXML private TextField availableEquipmentQueryTextField;
    @FXML private TextField selectedEquipmentQueryTextField;
    @FXML private Button addEquipmentButton;
    @FXML private Button removeEquipmentButton;
    @FXML private Button addAllEquipmentButton;
    @FXML private Button removeAllEquipmentButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private final EquipmentService equipmentService = new EquipmentServiceImpl();
    private ObservableList<Equipment> allAvailableEquipments;
    private ObservableList<Equipment> selectedEquipments;
    private FilteredList<Equipment> filteredAvailableEquipments;
    private FilteredList<Equipment> filteredSelectedEquipments;
    
    public void initialize(List<Integer> existingEquipmentIds) {
        // Khởi tạo danh sách
        allAvailableEquipments = FXCollections.observableArrayList();
        selectedEquipments = FXCollections.observableArrayList();
        
        // Cài đặt filtered lists
        filteredAvailableEquipments = new FilteredList<>(allAvailableEquipments, p -> true);
        filteredSelectedEquipments = new FilteredList<>(selectedEquipments, p -> true);
        
        // Thiết lập cột cho bảng thiết bị có sẵn
        setupTableColumns(availableEquipmentTableView);
        
        // Thiết lập cột cho bảng thiết bị đã chọn
        setupTableColumns(selectedEquipmentTableView);
        
        // Gắn danh sách vào TableView
        availableEquipmentTableView.setItems(filteredAvailableEquipments);
        selectedEquipmentTableView.setItems(filteredSelectedEquipments);
        
        // Xử lý tìm kiếm
        setupSearch();
        
        // Xử lý sự kiện nút
        setupButtons();
        
        // Tải danh sách thiết bị
        loadAvailableEquipments(existingEquipmentIds);

        // Tải danh sách thiết bị đã chọn
        loadSelectedEquipments(existingEquipmentIds);
    }
    
    private void loadSelectedEquipments(List<Integer> existingEquipmentIds) {
        try {
            // Lấy danh sách thiết bị đã chọn từ dịch vụ
            List<Equipment> selectedEquipmentsList = equipmentService.getEquipments().stream()
                .filter(equipment -> existingEquipmentIds.contains(equipment.getId()))
                .collect(Collectors.toList());
            
            // Thêm vào danh sách thiết bị đã chọn
            selectedEquipments.addAll(selectedEquipmentsList);
        } catch (SQLException e) {
            AlertBox.showError("Error", "Unable to load selected equipment list: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns(TableView<Equipment> tableView) {
        // Tạo cột ID
        TableColumn<Equipment, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(60);
        
        // Tạo cột Code
        TableColumn<Equipment, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        codeColumn.setPrefWidth(120);
        
        // Tạo cột Name
        TableColumn<Equipment, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(200);
        
        // Tạo cột Status
        TableColumn<Equipment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> {
            Status status = cellData.getValue().getStatus();
            return new SimpleStringProperty(status != null ? status.getDescription() : "Unknown");
        });
        statusColumn.setPrefWidth(120);
        
        // Tạo cột Last Maintenance
        TableColumn<Equipment, String> lastMaintenanceColumn = new TableColumn<>("Last Maintenance");
        lastMaintenanceColumn.setCellValueFactory(cellData -> {
            LocalDateTime lastMaint = cellData.getValue().getLastMaintenanceTime();
            if (lastMaint != null) {
                return new SimpleStringProperty(FormatDate.formatDateTime(lastMaint));
            } else {
                return new SimpleStringProperty("N/A");
            }
        });
        lastMaintenanceColumn.setPrefWidth(180);
        
        // Thêm các cột vào bảng
        tableView.getColumns().addAll(idColumn, codeColumn, nameColumn, statusColumn, lastMaintenanceColumn);
    }
    
    private void setupSearch() {
        // Tìm kiếm trong danh sách thiết bị có sẵn
        availableEquipmentQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredAvailableEquipments.setPredicate(equipment -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }
                return equipment.getName().toLowerCase().contains(searchText) || 
                       equipment.getCode().toLowerCase().contains(searchText);
            });
        });
        
        // Tìm kiếm trong danh sách thiết bị đã chọn
        selectedEquipmentQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = newValue.toLowerCase();
            filteredSelectedEquipments.setPredicate(equipment -> {
                if (searchText == null || searchText.isEmpty()) {
                    return true;
                }
                return equipment.getName().toLowerCase().contains(searchText) || 
                       equipment.getCode().toLowerCase().contains(searchText);
            });
        });
    }
    
    private void setupButtons() {
        // Xử lý nút thêm một thiết bị
        addEquipmentButton.setOnAction(event -> {
            Equipment selectedEquipment = availableEquipmentTableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                allAvailableEquipments.remove(selectedEquipment);
                selectedEquipments.add(selectedEquipment);
            }
        });
        
        // Xử lý nút thêm tất cả thiết bị
        addAllEquipmentButton.setOnAction(event -> {
            List<Equipment> visibleItems = new ArrayList<>(filteredAvailableEquipments);
            selectedEquipments.addAll(visibleItems);
            allAvailableEquipments.removeAll(visibleItems);
        });
        
        // Xử lý nút xóa một thiết bị
        removeEquipmentButton.setOnAction(event -> {
            Equipment selectedEquipment = selectedEquipmentTableView.getSelectionModel().getSelectedItem();
            if (selectedEquipment != null) {
                selectedEquipments.remove(selectedEquipment);
                allAvailableEquipments.add(selectedEquipment);
            }
        });
        
        // Xử lý nút xóa tất cả thiết bị
        removeAllEquipmentButton.setOnAction(event -> {
            List<Equipment> visibleItems = new ArrayList<>(filteredSelectedEquipments);
            allAvailableEquipments.addAll(visibleItems);
            selectedEquipments.removeAll(visibleItems);
        });
        
        // Xử lý nút lưu
        saveButton.setOnAction(event -> handleSave());
        
        // Xử lý nút hủy
        cancelButton.setOnAction(event -> handleCancel());
    }
    
    private void loadAvailableEquipments(List<Integer> existingEquipmentIds) {
        try {
            // Lấy tất cả thiết bị có trạng thái không phải đang bảo trì hoặc đã thanh lý
            List<Equipment> equipmentList = equipmentService.getEquipments().stream()
                .filter(equipment -> {
                    // Lọc theo trạng thái: không phải đang bảo trì hoặc đã thanh lý
                    Status status = equipment.getStatus();
                    return status != Status.LIQUIDATED && status != Status.UNDER_MAINTENANCE;
                })
                .filter(equipment -> !existingEquipmentIds.contains(equipment.getId()))
                .collect(Collectors.toList());
            
            allAvailableEquipments.addAll(equipmentList);
        } catch (SQLException e) {
            AlertBox.showError("Error", "Unable to load equipment list: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @FXML
    public void handleSave() {
        // Đóng cửa sổ
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    public void handleCancel() {
        // Xóa lựa chọn và đóng cửa sổ
        selectedEquipments.clear();
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Lấy danh sách thiết bị đã được chọn
     */
    public List<Equipment> getSelectedEquipments() {
        return new ArrayList<>(selectedEquipments);
    }
}
