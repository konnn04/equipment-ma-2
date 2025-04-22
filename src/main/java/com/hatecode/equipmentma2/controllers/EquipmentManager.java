package com.hatecode.equipmentma2.controllers;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.pojo.Image;
import com.hatecode.services.CloundinaryService;
import com.hatecode.services.impl.CategoryServiceImpl;
import com.hatecode.services.impl.CloudinaryServiceImpl;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.ImageServiceImpl;
import com.hatecode.services.CategoryService;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.ImageService;
import com.hatecode.utils.AlertBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EquipmentManager {
    private static final EquipmentService equipmentService = new EquipmentServiceImpl();
    private static final CategoryService categoryService = new CategoryServiceImpl();

    private final TableView<Equipment> equipmentTable;
    private final TextField equipmentQueryTextField;
    private final ComboBox<String> typeFilterComboBox;
    private final ComboBox<Object> valueFilterComboBox;

    private final TextField equipmentIDTextField;
    private final TextField equipmentCodeTextField;
    private final TextField equipmentNameTextField;
    private ComboBox<Category> equipmentCategoryComboBox;
    private final Text statusEquipmentText;
    private TextArea equipmentDescriptionTextField;

    private Button addEquipmentButton;
    private Button updateEquipmentButton;
    private Button saveEquipmentButton;
    private Button cancelEquipmentButton;
    private ComboBox<String> modeComboBox;
    private Label modeLabel;
    private Text lastMaintenanceDateTextField;
    private DatePicker nextMaintenanceDatePicker;
    private Button changeEquipmentImageButton;
    private ImageView equipmentImage;
    TextField regularMaintenanceTimeTextField;

    private Equipment selectedEquipment = null;
    private boolean isImageChanged = false;

    public EquipmentManager(
            TableView<Equipment> equipmentTable,
            TextField equipmentQueryTextField,
            ComboBox<String> typeFilterComboBox,
            ComboBox<Object> valueFilterComboBox,
            TextField equipmentIDTextField,
            TextField equipmentCodeTextField,
            TextField equipmentNameTextField,
            ComboBox<Category> equipmentCategoryComboBox,
            Text statusEquipmentText,
            Text lastMaintenanceDateTextField,
            DatePicker nextMaintenanceDatePicker,
            TextArea equipmentDescriptionTextField,
            Button addEquipmentButton,
            Button saveEquipmentButton,
            Button cancelEquipmentButton,
            Button updateEquipmentButton,
            ComboBox<String> modeComboBox,
            Label modeLabel,
            Button changeEquipmentImageButton,
            ImageView equipmentImage,
            TextField regularMaintenanceTimeTextField
    ) {
        this.equipmentTable = equipmentTable;
        this.equipmentQueryTextField = equipmentQueryTextField;
        this.typeFilterComboBox = typeFilterComboBox;
        this.valueFilterComboBox = valueFilterComboBox;
        this.equipmentIDTextField = equipmentIDTextField;
        this.equipmentCodeTextField = equipmentCodeTextField;
        this.equipmentNameTextField = equipmentNameTextField;
        this.equipmentCategoryComboBox = equipmentCategoryComboBox;
        this.statusEquipmentText = statusEquipmentText;
        this.nextMaintenanceDatePicker = nextMaintenanceDatePicker;
        this.lastMaintenanceDateTextField = lastMaintenanceDateTextField;
        this.equipmentDescriptionTextField = equipmentDescriptionTextField;
        this.addEquipmentButton = addEquipmentButton;
        this.updateEquipmentButton = updateEquipmentButton;
        this.saveEquipmentButton = saveEquipmentButton;
        this.cancelEquipmentButton = cancelEquipmentButton;
        this.modeComboBox = modeComboBox;
        this.modeLabel = modeLabel;
        this.changeEquipmentImageButton = changeEquipmentImageButton;
        this.equipmentImage = equipmentImage;
        this.regularMaintenanceTimeTextField = regularMaintenanceTimeTextField;
    }

    public void loadColumnEquipmentTableView() throws SQLException {
        TableColumn<Equipment, String> equipmentIDColumn = new TableColumn<>("Code");
        equipmentIDColumn.setPrefWidth(100);
        equipmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("code"));

        TableColumn<Equipment, String> equipmentNameColumn = new TableColumn<>("Name");
        equipmentNameColumn.setPrefWidth(160);
        equipmentNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Equipment, String> equipmentStatusColumn = new TableColumn<>("Status");
        equipmentStatusColumn.setPrefWidth(180);
        equipmentStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Equipment, String> equipmentCategoryColumn = new TableColumn<>("Category");
        equipmentCategoryColumn.setPrefWidth(180);
        equipmentCategoryColumn.setCellValueFactory(
                cellData -> {
                    Equipment equipment = cellData.getValue();
                    try {
                        Category category = categoryService.getCategoryById(equipment.getCategoryId());
                        if (category != null) {
                            return new SimpleStringProperty(category.getName());
                        } else {
                            return null;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
        );

        TableColumn<Equipment, String> createdDateColumn = new TableColumn<>("Created Date");
        createdDateColumn.setPrefWidth(200);
        createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));

        this.equipmentTable.getColumns().addAll(
                equipmentIDColumn,
                equipmentNameColumn,
                equipmentStatusColumn,
                equipmentCategoryColumn,
                createdDateColumn);
    }

    /* Khởi tạo danh sách lọc trong bảng Equipment */
    public void initEquipmentFilterComboBox() {
        typeFilterComboBox.getItems().addAll("All", "Category", "Status");
        typeFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (newSelection.equals("Category")) {
                    try {
                        List<Category> categories = categoryService.getCategories();
                        List<Object> categoryObjects = new ArrayList<>(categories);
                        valueFilterComboBox.setItems(FXCollections.observableList(categoryObjects));
                        valueFilterComboBox.getSelectionModel().select(0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else if (newSelection.equals("Status")) {
                    try {
                        List<Status> statuses = Status.getAllStatus();
                        List<Object> statusObjects = new ArrayList<>(statuses);
                        valueFilterComboBox.setItems(FXCollections.observableList(statusObjects));
                        valueFilterComboBox.getSelectionModel().select(0);
                    } catch (Exception e) {
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
        String value = "";
        if (key != null && key.equals("Category")) {
            Category category = (Category) valueFilterComboBox.getSelectionModel().getSelectedItem();
            if (category != null) {
                value = String.valueOf(category.getId());
            }
        } else if (key != null && key.equals("Status")) {
            Status status = (Status) valueFilterComboBox.getSelectionModel().getSelectedItem();
            if (status != null) {
                value = String.valueOf(status.getId());
            }
        } else{
            key = null;
            value = null;
        }
        List<Equipment> equipments;
        equipments = equipmentService.getEquipments(query, 0, 100, key, value);
        equipmentTable.setItems(FXCollections.observableList(equipments));
    }

    /* Chuyển dữ liệu từ bảng Equipment sang các trường nhập liệu */
    private void praseSelectedEquipmentData(Equipment selectedEquipment) throws SQLException {
        CategoryService categoryService = new CategoryServiceImpl();
        ImageService imageService = new ImageServiceImpl();

        isImageChanged = false;
        equipmentIDTextField.setText(String.valueOf(selectedEquipment.getId()));
        equipmentCodeTextField.setText(selectedEquipment.getCode());
        equipmentNameTextField.setText(selectedEquipment.getName());
        equipmentCategoryComboBox.getSelectionModel().select(
                categoryService.getCategoryById(selectedEquipment.getCategoryId()));
        statusEquipmentText.setText(selectedEquipment.getStatus().getDescription());
        equipmentDescriptionTextField.setText(selectedEquipment.getDescription());
        lastMaintenanceDateTextField.setText(selectedEquipment.getLastMaintenanceTime().toString());
        regularMaintenanceTimeTextField.setText(selectedEquipment.getRegularMaintenanceDay() + "");

        Image image = imageService.getImageById(selectedEquipment.getImageId());
        if (image != null) {
            equipmentImage.setImage(new ImageView(image.getPath()).getImage());
        } else {
            equipmentImage.setImage(null);
        }
    }

    /*Thêm thiết bị mới*/
    public void init() throws SQLException {
        initEquipmentFilterComboBox();
        loadColumnEquipmentTableView();
        fetchEquipmentTableView();

        /* Bắt sự kiện khi chọn 1 dòng trong bảng Equipment */
        equipmentTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedEquipment = newSelection;
                try {
                    praseSelectedEquipmentData(newSelection);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
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

        /* Khởi tạo comboBox category */
        equipmentCategoryComboBox.getItems().addAll(
                FXCollections.observableList(categoryService.getCategories())
        );

        updateEquipmentButton.setVisible(false);
        saveEquipmentButton.setVisible(false);
        changeEquipmentImageButton.setVisible(false);
        modeComboBox.getItems().addAll("Viewing", "Editing");
        modeComboBox.getSelectionModel().select(0);
        saveEquipmentButton.setVisible(false);
        cancelEquipmentButton.setVisible(false);
        regularMaintenanceTimeTextField.setDisable(true);
        equipmentCategoryComboBox.setDisable(true);
        nextMaintenanceDatePicker.setDisable(true);

        switchMode(true);

        /* Bắt sự kiện khi chọn chế độ xem hoặc chỉnh sửa */
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
            isImageChanged = false;
            selectedEquipment = null;
            addEquipmentButton.setDisable(true);
            cancelEquipmentButton.setVisible(true);
            saveEquipmentButton.setVisible(true);
            modeComboBox.setVisible(false);
            modeLabel.setText("Adding ");
            equipmentIDTextField.clear();
            equipmentCodeTextField.clear();
            equipmentCodeTextField.setDisable(false);
            equipmentNameTextField.clear();
            equipmentNameTextField.setDisable(false);
            statusEquipmentText.setText(Status.ACTIVE.getDescription());
            equipmentDescriptionTextField.clear();
            equipmentDescriptionTextField.setDisable(false);
            changeEquipmentImageButton.setVisible(true);
            equipmentImage.setImage(null);
            lastMaintenanceDateTextField.setText("None");
            regularMaintenanceTimeTextField.setDisable(false);
            regularMaintenanceTimeTextField.clear();
            equipmentCategoryComboBox.setDisable(false);
            nextMaintenanceDatePicker.setDisable(true);
        });

        /* Bắt sự kiện huỷ  thêm thiết bị */
        cancelEquipmentButton.setOnAction(event -> {
            selectedEquipment = null;
            isImageChanged = false;

            addEquipmentButton.setDisable(false);
            cancelEquipmentButton.setVisible(false);
            saveEquipmentButton.setVisible(false);
            modeComboBox.setVisible(true);
            equipmentCodeTextField.clear();
            equipmentNameTextField.clear();
            equipmentDescriptionTextField.clear();
            statusEquipmentText.setText("None");
            equipmentImage.setImage(null);
            switchMode(true);
        });

        /* Bắt sự kiện khi click vào nút đổi ảnh cho thiết bị */
        changeEquipmentImageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            fileChooser.setTitle("Select Equipment Image");
            java.io.File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                isImageChanged = true;
                equipmentImage.setImage(new ImageView(file.toURI().toString()).getImage());
            }
        });

        /* Bắt sự kiện khi click vào nút lưu thiết bị mới*/
        saveEquipmentButton.setOnAction(event -> {
            try{
                Image uploadImage = null;
                if (equipmentImage.getImage() != null) {
                    CloundinaryService cloundinaryService = new CloudinaryServiceImpl();
                    File file = new File( equipmentImage.getImage().getUrl().replace("file:", ""));
                    uploadImage = new Image(
                            file.getName(),
                            cloundinaryService.uploadImage(file)
                    );
                    ImageService imageService = new ImageServiceImpl();
                    imageService.addImage(uploadImage);
                }

                assert uploadImage != null;
                Equipment newEquipment = new Equipment(
                        equipmentCodeTextField.getText(),
                        equipmentNameTextField.getText(),
                        Status.ACTIVE,
                        equipmentCategoryComboBox.getSelectionModel().getSelectedItem().getId(),
                        uploadImage.getId(),
                        Integer.parseInt(regularMaintenanceTimeTextField.getText()),
                        equipmentDescriptionTextField.getText()
                );
                equipmentService.addEquipment(newEquipment);
                AlertBox.showConfirmation(
                        "Add Equipment",
                        "Equipment added successfully!"
                );
            }catch (Exception e){
                AlertBox.showError(
                        "Add Equipment",
                        "Failed to add equipment: " + e.getMessage()
                );
                e.printStackTrace();
            }
        });

        /* Bắt sự kiện khi click vào nút cập nhật thiết bị*/
        updateEquipmentButton.setOnAction(event -> {
            if (selectedEquipment != null) {
                try {
                    /* Kiểm tra ảnh có cập nhật hay không */
                    if (isImageChanged) {
                        CloundinaryService cloundinaryService = new CloudinaryServiceImpl();
                        File file  = new File(equipmentImage.getImage().getUrl());
                        Image image = new Image(
                                file.getName(),
                                cloundinaryService.uploadImage(file)
                        );
                        selectedEquipment.setImageId(image.getId());
                    }

                    EquipmentService equipmentService = new EquipmentServiceImpl();
                    selectedEquipment.setCode(equipmentCodeTextField.getText());
                    selectedEquipment.setName(equipmentNameTextField.getText());
                    selectedEquipment.setDescription(equipmentDescriptionTextField.getText());
                    selectedEquipment.setCategoryId(equipmentCategoryComboBox.getSelectionModel().getSelectedItem().getId());
                    selectedEquipment.setRegularMaintenanceDay(Integer.parseInt(regularMaintenanceTimeTextField.getText()));

                    equipmentService.updateEquipment(selectedEquipment);
                    switchMode(true);
                    modeComboBox.getSelectionModel().select(0);
                    fetchEquipmentTableView();
                    equipmentTable.getSelectionModel().select(selectedEquipment);
                    equipmentTable.scrollTo(selectedEquipment);
                    praseSelectedEquipmentData(selectedEquipment);
                    AlertBox.showConfirmation(
                            "Update Equipment",
                            "Equipment updated successfully!"
                    );
                    switchMode(true);
                } catch (SQLException e) {
                    AlertBox.showError(
                            "Update Equipment",
                            "Failed to update equipment: " + e.getMessage()
                    );
                    e.printStackTrace();
                }
            }else{
                AlertBox.showError(
                        "Update Equipment",
                        "Please select an equipment to update!"
                );
            }
        });

        /* Bắt sự kiện khi thay đổi regularMaintenanceTimeTextField */
        regularMaintenanceTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int value = Integer.parseInt(newValue);
                if (value < 0) {
                    regularMaintenanceTimeTextField.setText("0");
                }
                if (selectedEquipment != null) {
//                    nextMaintenanceDatePicker.setValue(selectedEquipment.getLastMaintenanceTime().plusDays(value).toLocalDate());
                }
            } catch (NumberFormatException e) {
                regularMaintenanceTimeTextField.setText("0");
            }
        });
    }


    private void switchMode(Boolean isViewing) {
        if (isViewing) {
            modeLabel.setText("Viewing");
            updateEquipmentButton.setVisible(false);
            addEquipmentButton.setDisable(false);
            equipmentCodeTextField.setDisable(true);
            equipmentNameTextField.setDisable(true);
            equipmentDescriptionTextField.setDisable(true);
            changeEquipmentImageButton.setVisible(false);
            regularMaintenanceTimeTextField.setDisable(true);
            equipmentCategoryComboBox.setDisable(true);
            nextMaintenanceDatePicker.setDisable(true);
        } else {
            modeLabel.setText("Editing");
            updateEquipmentButton.setVisible(true);
            addEquipmentButton.setDisable(true);
            equipmentCodeTextField.setDisable(false);
            equipmentNameTextField.setDisable(false);
            equipmentDescriptionTextField.setDisable(false);
            changeEquipmentImageButton.setVisible(true);
            regularMaintenanceTimeTextField.setDisable(false);
            equipmentCategoryComboBox.setDisable(false);
            nextMaintenanceDatePicker.setDisable(false);
        }
    }


}
