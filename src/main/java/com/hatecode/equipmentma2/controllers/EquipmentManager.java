package com.hatecode.equipmentma2.controllers;

import com.hatecode.equipmentma2.MainController;
import com.hatecode.pojo.BaseObject;
import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.StatusServiceImpl;
import com.hatecode.services.interfaces.CategoryService;
import com.hatecode.services.interfaces.EquipmentService;
import com.hatecode.services.interfaces.StatusService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EquipmentManager {
    private static final EquipmentService equipmentService = new EquipmentServiceImpl();
    private static final StatusService statusService = new StatusServiceImpl();
    private static final CategoryService categoryService = new CategoryServiceImpl();

    private final TableView<Equipment> equipmentTable;
    private final TextField equipmentQueryTextField;
    private final ComboBox<String> typeFilterComboBox;
    private final ComboBox<BaseObject> valueFilterComboBox;

    private final TextField equipmentIDTextField;
    private final TextField equipmentCodeTextField;
    private final TextField equipmentNameTextField;
    private final ComboBox<Status> statusEquipmentComboBox;
    private TextArea equipmentDescriptionTextField;

    private Button addEquipmentButton;

    public EquipmentManager(
            TableView<Equipment> equipmentTable,
            TextField equipmentQueryTextField,
            ComboBox<String> typeFilterComboBox,
            ComboBox<BaseObject> valueFilterComboBox,
            TextField equipmentIDTextField,
            TextField equipmentCodeTextField,
            TextField equipmentNameTextField,
            ComboBox<Status> statusEquipmentComboBox,
            TextArea equipmentDescriptionTextField,
            Button addEquipmentButton
    ) {
        this.equipmentTable = equipmentTable;
        this.equipmentQueryTextField = equipmentQueryTextField;
        this.typeFilterComboBox = typeFilterComboBox;
        this.valueFilterComboBox = valueFilterComboBox;
        this.equipmentIDTextField = equipmentIDTextField;
        this.equipmentCodeTextField = equipmentCodeTextField;
        this.equipmentNameTextField = equipmentNameTextField;
        this.statusEquipmentComboBox = statusEquipmentComboBox;
        this.equipmentDescriptionTextField = equipmentDescriptionTextField;
        this.addEquipmentButton = addEquipmentButton;
    }

    public void loadColumnEquipmentTableView() throws SQLException {
        TableColumn<Equipment, String> equipmentIDColumn = new TableColumn<>("Code");
        equipmentIDColumn.setPrefWidth(100);
        equipmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Equipment, String> equipmentNameColumn = new TableColumn<>("Name");
        equipmentNameColumn.setPrefWidth(200);
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Equipment, String> equipmentTypeColumn = new TableColumn<>("Status");
        equipmentTypeColumn.setPrefWidth(200);
        equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("statusName"));

        this.equipmentTable.getColumns().addAll(equipmentIDColumn, equipmentNameColumn, equipmentTypeColumn);
    }

    /* Khởi tạo danh sách lọc trong bảng Equipment */
    public void initEquipmentFilterComboBox() {
        typeFilterComboBox.getItems().addAll("All", "Category", "Status");
        typeFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (newSelection.equals("Category")) {
                    try {
                        List<Category> categories = categoryService.getCategories();
                        List<BaseObject> categoryObjects = new ArrayList<>(categories);
                        valueFilterComboBox.setItems(FXCollections.observableList(categoryObjects));
                        valueFilterComboBox.getSelectionModel().select(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (newSelection.equals("Status")) {
                    try {
                        List<Status> statuses = statusService.getStatuses();
                        List<BaseObject> statusObjects = new ArrayList<>(statuses);
                        valueFilterComboBox.setItems(FXCollections.observableList(statusObjects));
                        valueFilterComboBox.getSelectionModel().select(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    valueFilterComboBox.getItems().clear();
                }
            }
        });
        valueFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            try {
                fetchEquipmentTableView();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /* Lấy danh sách thiết bị từ database và hiển thị lên bảng Equipment */
    public void fetchEquipmentTableView() throws SQLException {
        String query = equipmentQueryTextField.getText();
        String key = typeFilterComboBox.getSelectionModel().getSelectedItem();
        String value = valueFilterComboBox.getSelectionModel().getSelectedItem() != null ? String.valueOf(valueFilterComboBox.getSelectionModel().getSelectedItem().getId()) : null;
        List<Equipment> equipments;
        equipments = equipmentService.getEquipments(query, 0, 100, key, value);
        equipmentTable.setItems(FXCollections.observableList(equipments));
    }

    /* Chuyển dữ liệu từ bảng Equipment sang các trường nhập liệu */
    private void praseSelectedEquipmentData(Equipment selectedEquipment) {
        equipmentIDTextField.textProperty().set(String.valueOf(selectedEquipment.getId()));
        equipmentCodeTextField.textProperty().set(selectedEquipment.getCode());
        equipmentNameTextField.textProperty().set(selectedEquipment.getName());
        statusEquipmentComboBox.getSelectionModel().select(selectedEquipment.getStatus());
        equipmentDescriptionTextField.textProperty().set(selectedEquipment.getDescription());
    }

    /*Thêm thiết bị mới*/
    public void init() throws SQLException {
        initEquipmentFilterComboBox();
        loadColumnEquipmentTableView();
        fetchEquipmentTableView();

        /* Lấy danh sách trạng thái từ database */
        List<Status> statusList = statusService.getStatuses();
        statusEquipmentComboBox.setItems(FXCollections.observableList(statusList));

        /* Bắt sự kiện khi chọn 1 dòng trong bảng Equipment */
        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                praseSelectedEquipmentData(newSelection);
            }
        });
        /* Bắt sự kiện khi click vào nút tìm kiếm */
        equipmentQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                fetchEquipmentTableView();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        /* Bắt sự kiện khi click vào nút thêm thiết bị */
//        public void setupAddEquipmentButton() {
//
//        }
    }
}
