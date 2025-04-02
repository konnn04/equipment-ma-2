package com.hatecode.equipmentma2.controllers;

import com.hatecode.equipmentma2.MainController;
import com.hatecode.models.BaseObject;
import com.hatecode.models.Category;
import com.hatecode.models.Equipment;
import com.hatecode.models.Status;
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
import javafx.scene.text.Text;

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
    private Button updateEquipmentButton;
    private Button saveEquipmentButton;
    private Button cancelEquipmentButton;
    private ComboBox<String> modeComboBox;
    private Label modeLabel;
    private Text lastMaintenanceDateTextField;
    private DatePicker nextMaintenanceDatePicker;

//    private boolean isCreateEquipment = false;

    public EquipmentManager(
            TableView<Equipment> equipmentTable,
            TextField equipmentQueryTextField,
            ComboBox<String> typeFilterComboBox,
            ComboBox<BaseObject> valueFilterComboBox,
            TextField equipmentIDTextField,
            TextField equipmentCodeTextField,
            TextField equipmentNameTextField,
            ComboBox<Status> statusEquipmentComboBox,
            Text lastMaintenanceDateTextField,
            DatePicker nextMaintenanceDatePicker,
            TextArea equipmentDescriptionTextField,
            Button addEquipmentButton,
            Button saveEquipmentButton,
            Button cancelEquipmentButton,
            Button updateEquipmentButton,
            ComboBox<String> modeComboBox,
            Label modeLabel
    ) {
        this.equipmentTable = equipmentTable;
        this.equipmentQueryTextField = equipmentQueryTextField;
        this.typeFilterComboBox = typeFilterComboBox;
        this.valueFilterComboBox = valueFilterComboBox;
        this.equipmentIDTextField = equipmentIDTextField;
        this.equipmentCodeTextField = equipmentCodeTextField;
        this.equipmentNameTextField = equipmentNameTextField;
        this.statusEquipmentComboBox = statusEquipmentComboBox;
        this.nextMaintenanceDatePicker = nextMaintenanceDatePicker;
        this.lastMaintenanceDateTextField = lastMaintenanceDateTextField;
        this.equipmentDescriptionTextField = equipmentDescriptionTextField;
        this.addEquipmentButton = addEquipmentButton;
        this.updateEquipmentButton = updateEquipmentButton;
        this.saveEquipmentButton = saveEquipmentButton;
        this.cancelEquipmentButton = cancelEquipmentButton;
        this.modeComboBox = modeComboBox;
        this.modeLabel = modeLabel;

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
        equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Equipment, String> equipmentCategoryColumn = new TableColumn<>("Category");
        equipmentCategoryColumn.setPrefWidth(300);
        equipmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

        TableColumn<Equipment, String> importDateColumn = new TableColumn<>("Import Date");
        importDateColumn.setPrefWidth(200);
        importDateColumn.setCellValueFactory(new PropertyValueFactory<>("importDate"));

        this.equipmentTable.getColumns().addAll(
                equipmentIDColumn,
                equipmentNameColumn,
                equipmentTypeColumn,
                equipmentCategoryColumn,
                importDateColumn);
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

        updateEquipmentButton.setVisible(false);
        saveEquipmentButton.setVisible(false);
        modeComboBox.getItems().addAll("Viewing", "Editing");
        modeComboBox.getSelectionModel().select(0);
        saveEquipmentButton.setVisible(false);
        cancelEquipmentButton.setVisible(false);
        switchMode(true);

        modeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (newSelection.equals("Editing")) {
                    switchMode(false);
                } else {
                    switchMode(true);
                }
            }
        });

        /* Bắt sự kiện khi click vào nút thêm thiết bị */
        addEquipmentButton.setOnAction(event -> {
            addEquipmentButton.setDisable(true);
            cancelEquipmentButton.setVisible(true);
            saveEquipmentButton.setVisible(true);
            modeComboBox.setVisible(false);

            modeLabel.setText("Adding ");
            equipmentCodeTextField.clear();
            equipmentCodeTextField.setDisable(false);
            equipmentNameTextField.clear();
            equipmentNameTextField.setDisable(false);
            statusEquipmentComboBox.getSelectionModel().select(0);
            statusEquipmentComboBox.setDisable(false);
            equipmentDescriptionTextField.clear();
            equipmentDescriptionTextField.setDisable(false);
        });

        /* Bắt sự kiện huỷ  thêm thiết bị */
        cancelEquipmentButton.setOnAction(event -> {
            addEquipmentButton.setDisable(false);
            cancelEquipmentButton.setVisible(false);
            saveEquipmentButton.setVisible(false);
            modeComboBox.setVisible(true);

            equipmentCodeTextField.clear();
            equipmentNameTextField.clear();
            statusEquipmentComboBox.getSelectionModel().select(0);
            equipmentDescriptionTextField.clear();
            switchMode(true);
        });

    }

    private void switchMode(Boolean isViewing) {
        if (isViewing) {
            modeLabel.setText("Viewing");
            updateEquipmentButton.setVisible(false);
            addEquipmentButton.setDisable(false);
            equipmentCodeTextField.setDisable(true);
            equipmentNameTextField.setDisable(true);
            statusEquipmentComboBox.setDisable(true);
            equipmentDescriptionTextField.setDisable(true);
        } else {
            modeLabel.setText("Editing");
            updateEquipmentButton.setVisible(true);
            addEquipmentButton.setDisable(true);
            equipmentCodeTextField.setDisable(false);
            equipmentNameTextField.setDisable(false);
            statusEquipmentComboBox.setDisable(false);
            equipmentDescriptionTextField.setDisable(false);

        }
    }
}
