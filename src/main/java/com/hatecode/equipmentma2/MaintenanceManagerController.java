package com.hatecode.equipmentma2;

import com.hatecode.pojo.*;
import com.hatecode.services.EquipmentMaintenanceService;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.MaintenanceService;
import com.hatecode.services.UserService;
import com.hatecode.services.impl.EquipmentMaintenanceServiceImpl;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.MaintenanceServiceImpl;
import com.hatecode.services.impl.UserServiceImpl;
import com.hatecode.utils.AlertBox;
import com.hatecode.utils.FormatDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MaintenanceManagerController {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceManagerController.class.getName());
    // Services
    private final MaintenanceService maintenanceService;
    private final EquipmentService equipmentService;
    private final EquipmentMaintenanceService equipmentMaintenanceService;
    private final UserService userService;

    // Cache for frequently accessed data
    private final Map<Integer, Equipment> equipmentCache = new HashMap<>();
    private final Map<Integer, User> userCache = new HashMap<>();

    // Flag to track selected maintenance
    private boolean isCreateOrModify = false;

    // State variables
    private Maintenance selectedMaintenance;
    private EquipmentMaintenance selectedEquipmentMaintenance;

    // FXML components - Maintenance table and filters
    @FXML
    private TableView<Maintenance> maintenanceTable;
    @FXML
    private TextField maintenanceQueryTextField;
    @FXML
    private Button newMaintenanceButton;
    @FXML
    private Button modifyMaintenanceButton;

    // Equipment maintenance table and controls
    @FXML
    private TextField equipmentQueryTextField;
    @FXML
    private Button selectEquipmentButton;
    @FXML
    private TableView<EquipmentMaintenance> equipmentMaintenanceTable;

    // Maintenance details form
    @FXML
    private Text maintenanceIdText;
    @FXML
    private TextField maintenanceTitleTextField;
    @FXML
    private DatePicker maintenanceFromDatePicker;
    @FXML
    private DatePicker maintenanceToDatePicker;
    @FXML
    private TextField maintenanceFromTimeTextField;
    @FXML
    private TextField maintenanceToTimeTextField;
    @FXML
    private TextArea maintenanceDescriptionTextArea;

    // Equipment maintenance details
    @FXML
    private Text equipmentMaintenanceIdText;
    @FXML
    private Text equipmentNameText;
    @FXML
    private Text equipmentCodeText;
    @FXML
    private ComboBox<User> technicianComboBox;

    // Action buttons
    @FXML
    private Button saveMaintenanceButton;
    @FXML
    private Button deleteMaintenanceButton;
    @FXML
    private Button cancelMaintenanceButton;

    public MaintenanceManagerController() {
        this.maintenanceService = new MaintenanceServiceImpl();
        this.equipmentService = new EquipmentServiceImpl();
        this.equipmentMaintenanceService = new EquipmentMaintenanceServiceImpl();
        this.userService = new UserServiceImpl();
    }

    public void refreshData() {

    }

    // Operation types for maintenance operations
    public enum OperationType {
        CREATE, UPDATE, DELETE
    }

    // List of maintenance change listeners
    private final List<MaintenanceChangeListener> maintenanceChangeListeners = new ArrayList<>();

    /**
     * Interface for maintenance change notifications
     */
    @FunctionalInterface
    public interface MaintenanceChangeListener {
        void onMaintenanceChanged(OperationType operationType, Maintenance maintenance);
    }

    /**
     * Add a listener for maintenance changes
     * @param listener The listener to add
     */
    public void addMaintenanceChangeListener(MaintenanceChangeListener listener) {
        if (listener != null) {
            maintenanceChangeListeners.add(listener);
        }
    }

    /**
     * Remove a listener for maintenance changes
     * @param listener The listener to remove
     */
    public void removeMaintenanceChangeListener(MaintenanceChangeListener listener) {
        maintenanceChangeListeners.remove(listener);
    }

    /**
     * Notify all listeners about a maintenance change
     * @param operationType Type of operation (CREATE, UPDATE, DELETE)
     * @param maintenance The maintenance that changed
     */
    private void notifyMaintenanceChange(OperationType operationType, Maintenance maintenance) {
        for (MaintenanceChangeListener listener : maintenanceChangeListeners) {
            listener.onMaintenanceChanged(operationType, maintenance);
        }
        
        // Log the operation
        LOGGER.info("Maintenance " + operationType.name() + " operation completed for ID: " + 
                (maintenance != null ? maintenance.getId() : "null"));
    }

    /**
     * Initialize the controller
     */
    public void init() {
        try {
            setupTables();
            setupEventHandlers();
            loadInitialData();
            setupTimeFieldConstraints();
        } catch (Exception e) {
            handleException(e, "Error initializing maintenance manager");
        }
    }

    /**
     * Set up all tables and their columns
     */
    private void setupTables() {
        setupMaintenanceTable();
        setupEquipmentMaintenanceTable();
        setupComboBox();
        // Disable form fields initially
        setDisableMaintenanceForm(true);
    }

    /**
     * Set up the main maintenance table with columns
     */
    private void setupMaintenanceTable() {
        // ID Column
        TableColumn<Maintenance, String> idColumn = createColumn("ID", "id", 100);

        // Title Column
        TableColumn<Maintenance, String> titleColumn = createColumn("Title", "title", 200);

        // Start Date Column
        TableColumn<Maintenance, String> startDateColumn = new TableColumn<>("Start Date");
        startDateColumn.setPrefWidth(200);
        startDateColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            return new SimpleStringProperty(
                    maintenance.getStartDateTime() != null ? FormatDate.formatDateTime(maintenance.getStartDateTime())
                            : "");
        });

        // End Date Column
        TableColumn<Maintenance, String> endDateColumn = new TableColumn<>("End Date");
        endDateColumn.setPrefWidth(200);
        endDateColumn.setCellValueFactory(cellData -> {
            Maintenance maintenance = cellData.getValue();
            return new SimpleStringProperty(
                    maintenance.getEndDateTime() != null ? FormatDate.formatDateTime(maintenance.getEndDateTime())
                            : "");
        });

        // Add columns to table
        maintenanceTable.getColumns().addAll(idColumn, titleColumn, startDateColumn, endDateColumn);
        maintenanceTable.setPlaceholder(new Label("No maintenance records available"));
        maintenanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Set up the equipment maintenance table with columns
     */
    private void setupEquipmentMaintenanceTable() {
        // ID Column
        TableColumn<EquipmentMaintenance, String> idColumn = createColumn("ID", "id", 100);

        // Equipment Name Column
        TableColumn<EquipmentMaintenance, String> equipmentNameColumn = new TableColumn<>("Equipment Name");
        equipmentNameColumn.setPrefWidth(200);
        equipmentNameColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getEquipmentName()));

        // Equipment Code Column
        TableColumn<EquipmentMaintenance, String> equipmentCodeColumn = new TableColumn<>("Equipment Code");
        equipmentCodeColumn.setPrefWidth(200);
        equipmentCodeColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getEquipmentCode()));

        // Technician Name Column
        TableColumn<EquipmentMaintenance, String> technicianColumn = new TableColumn<>("Technician Name");
        technicianColumn.setPrefWidth(200);
        technicianColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(getTechnicianName(cellData.getValue().getTechnicianId())));

        // Add columns to table
        equipmentMaintenanceTable.getColumns().addAll(
                idColumn, equipmentNameColumn, equipmentCodeColumn, technicianColumn);
        equipmentMaintenanceTable.setPlaceholder(new Label("No equipment maintenance records available"));
    }

    /**
     * Helper method to create standard columns
     */
    private <T, S> TableColumn<T, S> createColumn(String title, String property, double width) {
        TableColumn<T, S> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    /**
     * Set up all event handlers
     */
    private void setupEventHandlers() {
        // Search field listener for maintenance
        maintenanceQueryTextField.textProperty().addListener(
                (observable, oldValue, newValue) -> executeWithErrorHandling(() -> refreshMaintenanceData(newValue)));

        // Maintenance table selection listener
        maintenanceTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedMaintenance = newVal;
                        displayMaintenanceDetails(newVal);
                        try {
                            loadEquipmentMaintenanceForMaintenance(newVal);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

        // Equipment maintenance table selection listener
        equipmentMaintenanceTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        selectedEquipmentMaintenance = newVal;
                        displayEquipmentMaintenanceDetails(newVal);
                    }
                });

        // Add technician change listener
        technicianComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldValue, newValue) -> {
                if (newValue != null && selectedEquipmentMaintenance != null) {
                    // Update the technician ID in the selected equipment maintenance
                    selectedEquipmentMaintenance.setTechnicianId(newValue.getId());
                    
                    // Update the item in the table
                    int selectedIndex = equipmentMaintenanceTable.getSelectionModel().getSelectedIndex();
                    if (selectedIndex >= 0) {
                        equipmentMaintenanceTable.getItems().set(selectedIndex, selectedEquipmentMaintenance);
                    }
                    
                    // Refresh the table to show the changes
                    equipmentMaintenanceTable.refresh();
                    
                    // Log the change
                    LOGGER.info("Updated technician for equipment maintenance ID: " + 
                        selectedEquipmentMaintenance.getId() + " to user ID: " + newValue.getId());
                }
        });

        // Click event to reload loadTechnicians()
        technicianComboBox.setOnMouseClicked(event -> {
            loadTechnicians();
        });

        // Button event handlers
        newMaintenanceButton.setOnAction(event -> handleNewMaintenance());
        modifyMaintenanceButton.setOnAction(event -> handleModifyMaintenance());
        selectEquipmentButton.setOnAction(event -> {
            try {
                handleSelectEquipment();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        saveMaintenanceButton.setOnAction(event -> handleSaveMaintenance());
        deleteMaintenanceButton.setOnAction(event -> handleDeleteMaintenance());
        cancelMaintenanceButton.setOnAction(event -> handleCancelOperation());
    }

    /**
     * Load initial data
     */
    private void loadInitialData() throws SQLException {
        refreshMaintenanceData(null);
    }

    /**
     * Refresh maintenance table data
     */
    private void refreshMaintenanceData(String query) throws SQLException {
        List<Maintenance> maintenances = maintenanceService.getMaintenances(query);
        maintenanceTable.setItems(FXCollections.observableList(maintenances));
    }

    /**
     * Load equipment maintenance data for selected maintenance
     */
    private void loadEquipmentMaintenanceForMaintenance(Maintenance maintenance) throws SQLException {
        equipmentMaintenanceTable.setItems(
                FXCollections
                        .observableList(maintenanceService.getEquipmentMaintenancesByMaintenance(maintenance.getId())));
    }

    /**
     * Display maintenance details in the form
     */
    private void displayMaintenanceDetails(Maintenance maintenance) {
        if (maintenance == null)
            return;
        maintenanceIdText.setText(String.valueOf(maintenance.getId()));
        maintenanceTitleTextField.setText(maintenance.getTitle());
        maintenanceFromDatePicker
                .setValue(maintenance.getStartDateTime() != null ? maintenance.getStartDateTime().toLocalDate() : null);
        maintenanceToDatePicker
                .setValue(maintenance.getEndDateTime() != null ? maintenance.getEndDateTime().toLocalDate() : null);
        maintenanceFromTimeTextField.setText(
                maintenance.getStartDateTime() != null ? maintenance.getStartDateTime().toLocalTime().toString() : "");
        maintenanceToTimeTextField.setText(
                maintenance.getEndDateTime() != null ? maintenance.getEndDateTime().toLocalTime().toString() : "");
        maintenanceDescriptionTextArea.setText(maintenance.getDescription());
    }

    private void setupComboBox() {
        // Clear any existing items first
        technicianComboBox.getItems().clear();

        // Configure cell factory for displaying User objects
        technicianComboBox.setCellFactory(listView -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (user == null || empty) {
                    setText(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName());
                }
            }
        });

        // Configure button cell (what's shown when combo box is closed)
        technicianComboBox.setButtonCell(new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                if (user == null || empty) {
                    setText(null);
                } else {
                    setText(user.getFirstName() + " " + user.getLastName());
                }
            }
        });

        // Add selection change safety
        technicianComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && selectedEquipmentMaintenance != null) {
                try {
                    LOGGER.info("Updated technician for equipment maintenance ID: " +
                            selectedEquipmentMaintenance.getId() + " to user ID: " + newVal.getId());
                    selectedEquipmentMaintenance.setTechnicianId(newVal.getId());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to update technician", e);
                }
            }
        });

        // Load technicians
        loadTechnicians();
    }

    /**
     * Display equipment maintenance details in the form
     */
    private void displayEquipmentMaintenanceDetails(EquipmentMaintenance equipmentMaintenance) {
        if (equipmentMaintenance == null) {
            equipmentMaintenanceIdText.setText("");
            equipmentNameText.setText("");
            equipmentCodeText.setText("");
            technicianComboBox.getSelectionModel().clearSelection();
            return;
        }

        selectedEquipmentMaintenance = equipmentMaintenance;
        equipmentMaintenanceIdText.setText(String.valueOf(equipmentMaintenance.getId()));

        // Get equipment details from cache
        equipmentNameText.setText(equipmentMaintenance.getEquipmentName());
        equipmentCodeText.setText(equipmentMaintenance.getEquipmentCode());

        // Find and select technician in the combobox
        int technicianId = equipmentMaintenance.getTechnicianId();
        if (technicianId > 0) {
            // First ensure items are loaded
            if (technicianComboBox.getItems().isEmpty()) {
                loadTechnicians();
            }

            // Find matching user
            for (User user : technicianComboBox.getItems()) {
                if (user.getId() == technicianId) {
                    technicianComboBox.getSelectionModel().select(user);
                    break;
                }
            }
        } else {
            technicianComboBox.getSelectionModel().clearSelection();
        }
    }

    private Equipment getEquipmentById(int id) {
        if (!equipmentCache.containsKey(id)) {
            try {
                Equipment equipment = equipmentService.getEquipmentById(id);
                if (equipment != null) {
                    equipmentCache.put(id, equipment);
                }
                return equipment;
            } catch (SQLException e) {
                handleException(e, "Error loading equipment data");
                return null;
            }
        }
        return equipmentCache.get(id);
    }

    private String getEquipmentName(int equipmentId) {
        Equipment equipment = getEquipmentById(equipmentId);
        return equipment != null ? equipment.getName() : "";
    }

    private String getEquipmentCode(int equipmentId) {
        Equipment equipment = getEquipmentById(equipmentId);
        return equipment != null ? equipment.getCode() : "";
    }

    private String getTechnicianName(int userId) {
        if (!userCache.containsKey(userId)) {
            try {
                User user = userService.getUserById(userId);
                if (user != null) {
                    userCache.put(userId, user);
                }
                return user != null ? user.toString() : "";
            } catch (SQLException e) {
                handleException(e, "Error loading user data");
                return "";
            }
        }
        User user = userCache.get(userId);
        return user != null ? user.toString() : "";
    }

    // Event handlers for buttons

    private void handleNewMaintenance() {
        resetAllFields();
        // Disable tableview and able form
        maintenanceTable.setDisable(true);
        setDisableMaintenanceForm(false);
        // Clear all item in equipmentMaintenanceTable();
        equipmentMaintenanceTable.getItems().clear();
        // Clear all cache
        equipmentCache.clear();
        // Disable delete maintenance button
        deleteMaintenanceButton.setDisable(true);
    }

    private void handleModifyMaintenance() {
        // Kiểm tra xem đã chọn lịch bảo trì nào chưa
        if (selectedMaintenance == null) {
            AlertBox.showError("Error", "Please select a maintenance schedule to modify");
            return;
        }

        // Kiểm tra xem ngày bắt đầu của lịch bảo trì có trong tương lai không
        if (selectedMaintenance.getStartDateTime().isBefore(java.time.LocalDateTime.now())) {
            AlertBox.showError("Error", "You cannot modify a maintenance schedule that has already started");
            return;
        }

        try {
            // Đánh dấu trạng thái đang sửa đổi (không phải tạo mới)
            isCreateOrModify = true;
            
            // Lưu trữ danh sách thiết bị hiện tại để so sánh sau khi chỉnh sửa
            List<EquipmentMaintenance> originalEquipmentMaintenances = new ArrayList<>(equipmentMaintenanceTable.getItems());
            
            // Đảm bảo các thiết bị của lịch bảo trì đã được tải
            loadEquipmentMaintenanceForMaintenance(selectedMaintenance);
            
            // Hiển thị chi tiết lịch bảo trì lên form
            displayMaintenanceDetails(selectedMaintenance);
            
            // Vô hiệu hóa bảng lịch bảo trì chính và cho phép chỉnh sửa form
            maintenanceTable.setDisable(true);
            setDisableMaintenanceForm(false);
            
            // Bật nút xóa để cho phép xóa lịch bảo trì này nếu cần
            deleteMaintenanceButton.setDisable(false);
            
            LOGGER.log(Level.INFO, "Đang chỉnh sửa lịch bảo trì có ID: " + selectedMaintenance.getId());
            
        } catch (SQLException e) {
            handleException(e, "Lỗi khi tải dữ liệu bảo trì");
        }
    }

    private void handleSelectEquipment() throws IOException {
        Stage equipmentSelectStage = new Stage();
        equipmentSelectStage.setTitle("Chọn Thiết Bị");
        equipmentSelectStage.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("select-equipment-view.fxml"));
        Parent root = loader.load();

        // Lấy controller của cửa sổ chọn thiết bị
        SelectEquipmentPopUpController selectController = loader.getController();

        List<Integer> existingEquipmentIds = equipmentMaintenanceTable.getItems().stream()
                .map(EquipmentMaintenance::getEquipmentId)
                .collect(Collectors.toList());

        selectController.initialize(existingEquipmentIds);

        Scene scene = new Scene(root);
        equipmentSelectStage.setScene(scene);
        equipmentSelectStage.showAndWait();

        List<Equipment> selectedEquipments = selectController.getSelectedEquipments();

        // Danh sách ID thiết bị đã chọn từ popup
        List<Integer> selectedEquipmentIds = selectedEquipments.stream()
                .map(Equipment::getId)
                .collect(Collectors.toList());
        
        // Xóa các thiết bị không còn trong danh sách đã chọn
        equipmentMaintenanceTable.getItems().removeIf(em -> 
                !selectedEquipmentIds.contains(em.getEquipmentId()));
        
        // Thêm thiết bị mới (nếu có)
        if (selectedEquipments != null && !selectedEquipments.isEmpty()) {
            for (Equipment equipment : selectedEquipments) {
                boolean exists = equipmentMaintenanceTable.getItems().stream()
                        .anyMatch(em -> em.getEquipmentId() == equipment.getId());

                if (!exists) {
                    equipmentCache.put(equipment.getId(), equipment);
                    EquipmentMaintenance newMaintenance = new EquipmentMaintenance();
                    newMaintenance.setEquipmentId(equipment.getId());
                    newMaintenance.setEquipmentName(equipment.getName());
                    newMaintenance.setEquipmentCode(equipment.getCode());

                    // Gán kỹ thuật viên mặc định nếu có
                    if (!userCache.isEmpty() && technicianComboBox.getItems().size() > 0) {
                        newMaintenance.setTechnicianId(
                                technicianComboBox.getItems().get(0).getId());
                    }
                    equipmentMaintenanceTable.getItems().add(newMaintenance);
                }
            }
            
            equipmentMaintenanceTable.refresh();
            
            if (!equipmentMaintenanceTable.getItems().isEmpty()) {
                equipmentMaintenanceTable.getSelectionModel().select(0);
                selectedEquipmentMaintenance = equipmentMaintenanceTable.getItems().get(0);
                displayEquipmentMaintenanceDetails(selectedEquipmentMaintenance);
            }
        } else {
            // Nếu không có thiết bị nào được chọn, xóa tất cả
            equipmentMaintenanceTable.getItems().clear();
            equipmentMaintenanceTable.refresh();
            equipmentMaintenanceIdText.setText("");
            equipmentNameText.setText("");
            equipmentCodeText.setText("");
            technicianComboBox.getSelectionModel().clearSelection();
        }
    }

    private void handleSaveMaintenance() {
        try {
            // Check required fields
            if (maintenanceTitleTextField.getText().isEmpty() || maintenanceFromDatePicker.getValue() == null
                    || maintenanceToDatePicker.getValue() == null || maintenanceFromTimeTextField.getText().isEmpty()
                    || maintenanceToTimeTextField.getText().isEmpty()) {
                AlertBox.showError("Error", "Please fill in all required information");
                return;
            }
            
            // Check equipment and technicians
            if (equipmentMaintenanceTable.getItems().isEmpty()) {
                AlertBox.showError("Error", "Please select at least one equipment for maintenance");
                return;
            }
            
            // Check technician for each equipment
            for (EquipmentMaintenance equipmentMaintenance : equipmentMaintenanceTable.getItems()) {
                if (equipmentMaintenance.getTechnicianId() <= 0) {
                    AlertBox.showError("Error", "Please assign a technician for each equipment");
                    return;
                }
            }
            
            // Determine if this is an update or a new creation
            boolean isUpdate = !maintenanceIdText.getText().isEmpty() && isCreateOrModify;
            
            // Create Maintenance object from form data
            Maintenance maintenance = new Maintenance();
            maintenance.setTitle(maintenanceTitleTextField.getText());
            maintenance.setStartDateTime(FormatDate.combineDateAndTime(
                    maintenanceFromDatePicker.getValue(), maintenanceFromTimeTextField.getText()));
            maintenance.setEndDateTime(FormatDate.combineDateAndTime(
                    maintenanceToDatePicker.getValue(), maintenanceToTimeTextField.getText()));
            maintenance.setDescription(maintenanceDescriptionTextArea.getText());
            
            boolean result;
            if (isUpdate) {
                // Update case
                maintenance.setId(Integer.parseInt(maintenanceIdText.getText()));
                
                // Get current equipment list from database
                List<EquipmentMaintenance> existingEquipments = maintenanceService
                        .getEquipmentMaintenancesByMaintenance(maintenance.getId());
                
                // Get equipment IDs from database
                List<Integer> existingIds = existingEquipments.stream()
                        .map(EquipmentMaintenance::getEquipmentId)
                        .collect(Collectors.toList());
                
                // Get current equipment IDs from UI
                List<Integer> currentIds = equipmentMaintenanceTable.getItems().stream()
                        .map(EquipmentMaintenance::getEquipmentId)
                        .collect(Collectors.toList());
                
                // Equipment to delete: exists in database but not in UI
                for (EquipmentMaintenance em : existingEquipments) {
                    if (!currentIds.contains(em.getEquipmentId())) {
                        // Delete equipment maintenance that is no longer selected
                        equipmentMaintenanceService.deleteEquipmentMaintenance(em.getId());
                    }
                }
                
                // Update maintenance schedule
                result = maintenanceService.updateMaintenance(maintenance);
                
                // Add new equipment maintenance or update existing ones
                for (EquipmentMaintenance em : equipmentMaintenanceTable.getItems()) {
                    em.setMaintenanceId(maintenance.getId());
                    
                    if (em.getId() > 0) {
                        // Existing equipment, update
                        equipmentMaintenanceService.updateEquipmentMaintenance(em);
                    } else {
                        // New equipment, add
                        equipmentMaintenanceService.addEquipmentMaintenance(em);
                    }
                }
                
                // Execute callback for successful update
                if (result) {
                    notifyMaintenanceChange(OperationType.UPDATE, maintenance);
                }
            } else {
                // New creation case
                result = maintenanceService.addMaintenance(maintenance, equipmentMaintenanceTable.getItems());
                
                // Execute callback for successful creation
                if (result) {
                    notifyMaintenanceChange(OperationType.CREATE, maintenance);
                }
            }
            
            if (result) {
                AlertBox.showInfo("Success", isUpdate ? "Maintenance schedule updated successfully" : "New maintenance schedule created successfully");
                resetAllFields();
                setDisableMaintenanceForm(true);
                maintenanceTable.setDisable(false);
                isCreateOrModify = false;
                refreshMaintenanceData(null);
            } else {
                AlertBox.showError("Error", "Unable to save maintenance schedule");
            }
        } catch (Exception e) {
            handleException(e, e.getMessage());
        } finally {
            // Clear cache
            equipmentCache.clear();
            userCache.clear();
        }
    }

    private void handleDeleteMaintenance() {
        // Check if a maintenance schedule is selected
        if (selectedMaintenance == null) {
            AlertBox.showError("Error", "Please select a maintenance schedule to delete");
            return;
        }
        
        // Check if the maintenance schedule can be deleted (not yet started)
        if (selectedMaintenance.getStartDateTime().isBefore(java.time.LocalDateTime.now())) {
            AlertBox.showError("Error", "Cannot delete a maintenance schedule that has already started");
            return;
        }
        
        // Confirm deletion with the user
        boolean confirmDelete = AlertBox.showConfirmation("Confirm Deletion", 
            "Are you sure you want to delete this maintenance schedule?\nAll related information will be permanently deleted.");
        
        if (!confirmDelete) {
            return; // User canceled the operation
        }
        
        executeWithErrorHandling(() -> {
            // Proceed with deleting the maintenance schedule
            boolean result = maintenanceService.deleteMaintenanceById(selectedMaintenance.getId());
            
            if (result) {
                // Execute callback for successful deletion
                notifyMaintenanceChange(OperationType.DELETE, selectedMaintenance);
                
                AlertBox.showInfo("Success", "Maintenance schedule deleted successfully");
                
                // Reset the interface and refresh data
                resetAllFields();
                setDisableMaintenanceForm(true);
                maintenanceTable.setDisable(false);
                isCreateOrModify = false;
                refreshMaintenanceData(null);
            } else {
                AlertBox.showError("Error", "Unable to delete the maintenance schedule");
            }
        });
    }

    private void handleCancelOperation() {
        boolean confirm = AlertBox.showConfirmation("Confirmation", "Are you sure you want to cancel?");
        if (confirm) {
            resetAllFields();
            setDisableMaintenanceForm(true);
            maintenanceTable.setDisable(false);
        }
    }

    private void setDisableMaintenanceForm(boolean isDisabled) {
        maintenanceTitleTextField.setDisable(isDisabled);
        maintenanceFromDatePicker.setDisable(isDisabled);
        maintenanceToDatePicker.setDisable(isDisabled);
        maintenanceFromTimeTextField.setDisable(isDisabled);
        maintenanceToTimeTextField.setDisable(isDisabled);
        maintenanceDescriptionTextArea.setDisable(isDisabled);
        selectEquipmentButton.setDisable(isDisabled);
        selectEquipmentButton.setVisible(!isDisabled);
        // Disable equipment maintenance form
        technicianComboBox.setDisable(isDisabled);
        // Disable action buttons
        saveMaintenanceButton.setDisable(isDisabled);
        deleteMaintenanceButton.setDisable(isDisabled);
        cancelMaintenanceButton.setDisable(isDisabled);
        saveMaintenanceButton.setVisible(!isDisabled);
        deleteMaintenanceButton.setVisible(!isDisabled);
        cancelMaintenanceButton.setVisible(!isDisabled);
        // Able new maintenance button and modify maintenance button
        newMaintenanceButton.setDisable(!isDisabled);
        modifyMaintenanceButton.setDisable(!isDisabled);
    }

    private void resetAllFields() {
        maintenanceIdText.setText("");
        maintenanceTitleTextField.clear();
        maintenanceFromDatePicker.setValue(null);
        maintenanceToDatePicker.setValue(null);
        maintenanceFromTimeTextField.clear();
        maintenanceToTimeTextField.clear();
        maintenanceDescriptionTextArea.clear();
        equipmentMaintenanceIdText.setText("");
        equipmentNameText.setText("");
        equipmentCodeText.setText("");
        technicianComboBox.getSelectionModel().clearSelection();
    }

    private void executeWithErrorHandling(SQLOperation operation) {
        try {
            operation.execute();
        } catch (SQLException e) {
            handleException(e, "Database operation failed");
        }
    }

    private void handleException(Exception e, String message) {
        LOGGER.log(Level.SEVERE, message, e);
        AlertBox.showError("Error", e.getMessage());
    }

    private void loadTechnicians() {
        try {
            List<User> technicians = userService.getUsersByRole(Role.TECHNICIAN);
            if (technicians != null && !technicians.isEmpty()) {
                technicianComboBox.setItems(FXCollections.observableArrayList(technicians));
            } else {
                technicianComboBox.setItems(FXCollections.observableArrayList());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load technicians", e);
            technicianComboBox.setItems(FXCollections.observableArrayList());
        }
    }

    private void setupTimeFieldConstraints() {
        setupTimeField(maintenanceFromTimeTextField);
        setupTimeField(maintenanceToTimeTextField);
    }

    private void setupTimeField(TextField timeField) {
        // Set default value if empty
        if (timeField.getText() == null || timeField.getText().isEmpty()) {
            timeField.setText("00:00");
        }

        // Set default when field loses focus
        timeField.focusedProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue && (timeField.getText() == null || timeField.getText().isEmpty())) {
                timeField.setText("00:00");
            }
        });

        // Validate and format input
        timeField.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue.isEmpty()) {
                return; // Will be set to 00:00 on focus lost
            }

            // Remove any non-digit characters except colon
            String filtered = newValue.replaceAll("[^0-9:]", "");

            // Handle formatting
            if (filtered.length() > 5) {
                filtered = filtered.substring(0, 5);
            } else if (filtered.length() == 2 && !filtered.contains(":") && !oldValue.contains(":")) {
                // Auto-add colon after hours
                filtered += ":";
            }

            // Format time properly when we have enough digits
            if (filtered.length() >= 3) {
                String[] parts;
                if (filtered.contains(":")) {
                    parts = filtered.split(":");
                } else {
                    // Handle no colon case
                    parts = new String[] {
                            filtered.substring(0, Math.min(2, filtered.length())),
                            filtered.substring(Math.min(2, filtered.length()))
                    };
                }

                // Validate hours (0-23)
                int hours = 0;
                if (!parts[0].isEmpty()) {
                    hours = Math.min(23, Integer.parseInt(parts[0]));
                }
                String hoursStr = String.format("%02d", hours);

                // Validate minutes (0-59)
                int minutes = 0;
                if (parts.length > 1 && !parts[1].isEmpty()) {
                    minutes = Math.min(59, Integer.parseInt(parts[1]));
                }
                String minutesStr = String.format("%02d", minutes);

                filtered = hoursStr + ":" + minutesStr;
            }

            // Update text field if value changed
            if (!filtered.equals(newValue)) {
                timeField.setText(filtered);
            }
        });
    }

    @FunctionalInterface
    private interface SQLOperation {
        void execute() throws SQLException;
    }
}
