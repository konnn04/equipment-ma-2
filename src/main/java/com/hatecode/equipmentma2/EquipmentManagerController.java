package com.hatecode.equipmentma2;

import com.hatecode.pojo.Category;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Status;
import com.hatecode.pojo.Image;
import com.hatecode.services.CloundinaryService;
import com.hatecode.services.impl.*;
import com.hatecode.services.CategoryService;
import com.hatecode.services.EquipmentService;
import com.hatecode.services.ImageService;
import com.hatecode.utils.AlertBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EquipmentManagerController {
    // Services
    private static final EquipmentService equipmentService = new EquipmentServiceImpl();
    private static final CategoryService categoryService = new CategoryServiceImpl();
    private static final ImageService imageService = new ImageServiceImpl();
    private static final CloundinaryService cloudinaryService = new CloudinaryServiceImpl();

    // State variables
    private Equipment selectedEquipment = null;
    private boolean isImageChanged = false;

    // FXML UI components
    @FXML private TableView<Equipment> equipmentTable;
    @FXML private TextField equipmentQueryTextField;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private ComboBox<Object> valueFilterComboBox;
    @FXML private TextField equipmentIDTextField;
    @FXML private TextField equipmentCodeTextField;
    @FXML private TextField equipmentNameTextField;
    @FXML private ComboBox<Category> equipmentCategoryComboBox;
    @FXML private Text statusEquipmentText;
    @FXML private TextArea equipmentDescriptionTextField;
    @FXML private Button addEquipmentButton;
    @FXML private Button updateEquipmentButton;

    @FXML private ComboBox<String> modeComboBox;
    @FXML private Label modeLabel;
    @FXML private Text lastMaintenanceDateTextField;
    @FXML private DatePicker nextMaintenanceDatePicker;
    @FXML private Button changeEquipmentImageButton;
    @FXML private ImageView equipmentImage;
    @FXML private TextField regularMaintenanceTimeTextField;

    @FXML private Button saveEquipmentButton;
    @FXML private Button cancelEquipmentButton;
    @FXML private Button deleteEquipmentButton;

    // Add these as class fields
    private Timeline searchDebounceTimer;
    private final int SEARCH_DELAY_MS = 500; // 500ms delay
    private static final Logger LOGGER = Logger.getLogger(EquipmentManagerController.class.getName());

    /**
     * Initialize the controller
     */
    public void init() throws SQLException {
        setupTableColumns();
        setupFilters();
        setupUIDefaults();
        setupEventHandlers();
        
        // Load initial data
        loadCategories();
        refreshEquipmentData();
    }

    /**
     * Set up table columns with appropriate cell value factories
     */
    private void setupTableColumns() {
        // Create columns
        TableColumn<Equipment, String> codeColumn = createColumn("Code", "code", 100);
        TableColumn<Equipment, String> nameColumn = createColumn("Name", "name", 160);
        TableColumn<Equipment, String> statusColumn = createColumn("Status", "status", 180);
        TableColumn<Equipment, String> categoryColumn = createCategoryColumn();
        TableColumn<Equipment, String> dateColumn = createColumn("Created Date", "createdAt", 200);

        // Add columns to table
        equipmentTable.getColumns().addAll(codeColumn, nameColumn, statusColumn, categoryColumn, dateColumn);
    }

    /**
     * Create a standard table column with PropertyValueFactory
     */
    private <T> TableColumn<Equipment, T> createColumn(String title, String property, double width) {
        TableColumn<Equipment, T> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        return column;
    }

    /**
     * Create specialized category column with custom value factory
     */
    private TableColumn<Equipment, String> createCategoryColumn() {
        TableColumn<Equipment, String> column = new TableColumn<>("Category");
        column.setPrefWidth(180);
        column.setCellValueFactory(cellData -> {
            try {
                Equipment equipment = cellData.getValue();
                Category category = categoryService.getCategoryById(equipment.getCategoryId());
                return category != null ? new SimpleStringProperty(category.getName()) : null;
            } catch (SQLException e) {
                handleException(e, "Error loading category");
                return null;
            }
        });
        return column;
    }

    /**
     * Set up filter comboboxes and their listeners
     */
    private void setupFilters() {
        // Type filter setup
        typeFilterComboBox.getItems().addAll("All", "Category", "Status");
        typeFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;
            
            try {
                if ("Category".equals(newVal)) {
                    List<Category> categories = categoryService.getCategories();
                    valueFilterComboBox.setItems(FXCollections.observableList(new ArrayList<>(categories)));
                    valueFilterComboBox.getSelectionModel().select(0);
                } else if ("Status".equals(newVal)) {
                    List<Status> statuses = Status.getAllStatus();
                    valueFilterComboBox.setItems(FXCollections.observableList(new ArrayList<>(statuses)));
                    valueFilterComboBox.getSelectionModel().select(0);
                } else {
                    valueFilterComboBox.getItems().clear();
                }
            } catch (Exception e) {
                handleException(e, "Error setting up filters");
            }
        });

        // Value filter change listener
        valueFilterComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> safeExecute(this::refreshEquipmentData));
    }

    /**
     * Set up default UI state
     */
    private void setupUIDefaults() {
        // Initial button states
        updateEquipmentButton.setVisible(false);
        saveEquipmentButton.setVisible(false);
        changeEquipmentImageButton.setVisible(false);
        cancelEquipmentButton.setVisible(false);
        deleteEquipmentButton.setVisible(false);
        
        // Combobox setup
        modeComboBox.getItems().addAll("Viewing", "Editing");
        modeComboBox.getSelectionModel().select(0);
        
        // Form defaults
        regularMaintenanceTimeTextField.setDisable(true);
        equipmentCategoryComboBox.setDisable(true);
        nextMaintenanceDatePicker.setDisable(true);
        
        setViewMode(true);
    }

    /**
     * Set up all event handlers with debouncing for search
     */
    private void setupEventHandlers() {
        // Initialize the search debounce timer
        searchDebounceTimer = new Timeline(
            new KeyFrame(Duration.millis(SEARCH_DELAY_MS), event -> {
                safeExecute(this::refreshEquipmentData);
            })
        );
        searchDebounceTimer.setCycleCount(1);

        // Search field listener with debounce
        equipmentQueryTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Reset the timer every time the text changes
            searchDebounceTimer.stop();
            // Start the timer again
            searchDebounceTimer.play();
        });

        // Table selection listener
        equipmentTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedEquipment = newSelection;
                    safeExecute(() -> displayEquipmentDetails(newSelection));
                }
            });

        // Search field listener
        equipmentQueryTextField.textProperty().addListener(
            (observable, oldValue, newValue) -> safeExecute(this::refreshEquipmentData));

        // Mode selection listener
        modeComboBox.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    setViewMode("Viewing".equals(newVal));
                }
            });

        // Button click handlers
        addEquipmentButton.setOnAction(event -> handleAddEquipmentAction());
        cancelEquipmentButton.setOnAction(event -> handleCancelAction());
        changeEquipmentImageButton.setOnAction(event -> handleChangeImageAction());
        saveEquipmentButton.setOnAction(event -> handleSaveEquipmentAction());
        updateEquipmentButton.setOnAction(event -> handleUpdateEquipmentAction());
        deleteEquipmentButton.setOnAction(event -> handleDeleteEquipment());

        // Maintenance time field listener
        setupMaintenanceTimeListener();
    }

    private void handleDeleteEquipment() {
        if (selectedEquipment == null) {
            AlertBox.showError("Delete Equipment", "Please select an equipment to delete!");
            return;
        }

        boolean confirmation = AlertBox.showConfirmation("Delete Equipment", "Are you sure you want to delete this equipment?");
        if (!confirmation) {
            return;
        }

        try {
            boolean check = equipmentService.deleteEquipment(selectedEquipment.getId());
            if (!check) {
                AlertBox.showError("Delete Equipment", "Failed to delete equipment");
                return;
            }
            AlertBox.showConfirmation("Delete Equipment", "Equipment deleted successfully!");
            refreshEquipmentData();
            handleCancelAction();
        } catch (SQLException e) {
            handleException(e, "Failed to delete equipment");
        }
    }

    /**
     * Set up regular maintenance time field listener
     */
    private void setupMaintenanceTimeListener() {
        regularMaintenanceTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                int value = Integer.parseInt(newValue);
                if (value < 0) {
                    regularMaintenanceTimeTextField.setText("0");
                }
                if (selectedEquipment != null) {
                    nextMaintenanceDatePicker.setValue(
                        selectedEquipment.getLastMaintenanceTime().plusDays(value).toLocalDate());
                }
            } catch (NumberFormatException e) {
                regularMaintenanceTimeTextField.setText("0");
            }
        });
    }

    /**
     * Load categories into combobox
     */
    private void loadCategories() throws SQLException {
        equipmentCategoryComboBox.getItems().addAll(
            FXCollections.observableList(categoryService.getCategories()));
    }

    /**
     * Handle the add equipment button action
     */
    private void handleAddEquipmentAction() {
        isImageChanged = false;
        selectedEquipment = null;
        
        // Update UI state
        addEquipmentButton.setDisable(true);
        cancelEquipmentButton.setVisible(true);
        deleteEquipmentButton.setVisible(false);
        saveEquipmentButton.setVisible(true);
        modeComboBox.setVisible(false);
        changeEquipmentImageButton.setVisible(true);
        modeLabel.setText("Adding ");
        
        // Clear form fields
        clearFormFields();
        
        // Enable editable fields
        setFieldsEditable(true);
        
        // Set defaults
        statusEquipmentText.setText(Status.ACTIVE.getDescription());
        lastMaintenanceDateTextField.setText("None");
    }

    /**
     * Handle the cancel button action
     */
    private void handleCancelAction() {
        selectedEquipment = null;
        isImageChanged = false;

        // Update UI state
        addEquipmentButton.setDisable(false);
        cancelEquipmentButton.setVisible(false);
        saveEquipmentButton.setVisible(false);
        modeComboBox.setVisible(true);
        deleteEquipmentButton.setVisible(true);
        
        // Clear form fields
        clearFormFields();
        
        // Reset to view mode
        setViewMode(true);
    }

    /**
     * Handle the change image button action
     */
    private void handleChangeImageAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        fileChooser.setTitle("Select Equipment Image");
        
        java.io.File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            isImageChanged = true;
            equipmentImage.setImage(new ImageView(file.toURI().toString()).getImage());
        }
    }

    /**
     * Handle the save equipment button action
     */
    private void handleSaveEquipmentAction() {
        try {
            // Upload image if provided
            Image uploadImage = uploadImageIfChanged();
            if (equipmentImage.getImage() != null && uploadImage == null) {
                AlertBox.showError("Add Equipment", "Failed to upload image");
                return;
            }

            // Validate required fields
            if (!validateForm()) {
                AlertBox.showError("Add Equipment", "Please fill all required fields");
                return;
            }
            Equipment newEquipment = new Equipment(
                    equipmentCodeTextField.getText(),
                    equipmentNameTextField.getText(),
                    Status.ACTIVE,
                    equipmentCategoryComboBox.getSelectionModel().getSelectedItem().getId(),
                    Integer.parseInt(regularMaintenanceTimeTextField.getText()),
                    equipmentDescriptionTextField.getText()
            );
            // Create new equipment
            if (uploadImage != null) {
                equipmentService.addEquipment(newEquipment, uploadImage);
            } else {
                equipmentService.addEquipment(newEquipment);
            }

            // Show success and refresh
            AlertBox.showConfirmation("Add Equipment", "Equipment added successfully!");
            refreshEquipmentData();
            handleCancelAction();
            
        } catch (Exception e) {
            handleException(e, "Failed to add equipment");
        }
    }

    /**
     * Handle the update equipment button action
     */
    private void handleUpdateEquipmentAction() {
        if (selectedEquipment == null) {
            AlertBox.showError("Update Equipment", "Please select an equipment to update!");
            return;
        }

        try {
            // Upload image if changed
            if (isImageChanged) {
                Image image = uploadImageIfChanged();
                selectedEquipment.setImageId(image.getId());
            }

            // Update equipment details
            selectedEquipment.setCode(equipmentCodeTextField.getText());
            selectedEquipment.setName(equipmentNameTextField.getText());
            selectedEquipment.setDescription(equipmentDescriptionTextField.getText());
            selectedEquipment.setCategoryId(
                equipmentCategoryComboBox.getSelectionModel().getSelectedItem().getId());
            selectedEquipment.setRegularMaintenanceDay(
                Integer.parseInt(regularMaintenanceTimeTextField.getText()));

            // Save changes
            equipmentService.updateEquipment(selectedEquipment);
            
            // Update UI
            setViewMode(true);
            modeComboBox.getSelectionModel().select(0);
            refreshEquipmentData();
            selectEquipmentInTable(selectedEquipment);
            
            // Show success message
            AlertBox.showConfirmation("Update Equipment", "Equipment updated successfully!");
            
        } catch (Exception e) {
            handleException(e, "Failed to update equipment");
        }
    }

    /**
     * Clear all form fields
     */
    private void clearFormFields() {
        equipmentIDTextField.clear();
        equipmentCodeTextField.clear();
        equipmentNameTextField.clear();
        equipmentDescriptionTextField.clear();
        statusEquipmentText.setText("None");
        equipmentImage.setImage(null);
        regularMaintenanceTimeTextField.clear();
    }

    /**
     * Toggle view/edit mode
     */
    private void setViewMode(boolean isViewing) {
        modeLabel.setText(isViewing ? "Viewing" : "Editing");
        updateEquipmentButton.setVisible(!isViewing);
        addEquipmentButton.setDisable(!isViewing);
        changeEquipmentImageButton.setVisible(!isViewing);
        deleteEquipmentButton.setVisible(!isViewing);
        
        setFieldsEditable(!isViewing);
        
        // Always disable these fields in either mode
        nextMaintenanceDatePicker.setDisable(true);
    }

    /**
     * Set editable state for form fields
     */
    private void setFieldsEditable(boolean editable) {
        equipmentCodeTextField.setDisable(!editable);
        equipmentNameTextField.setDisable(!editable);
        equipmentDescriptionTextField.setDisable(!editable);
        regularMaintenanceTimeTextField.setDisable(!editable);
        equipmentCategoryComboBox.setDisable(!editable);
    }

    /**
     * Display equipment details in the form with async image loading
     */
    private void displayEquipmentDetails(Equipment equipment) throws SQLException {
        // Reset image changed flag
        isImageChanged = false;
        
        // Display text data immediately
        equipmentIDTextField.setText(String.valueOf(equipment.getId()));
        equipmentCodeTextField.setText(equipment.getCode());
        equipmentNameTextField.setText(equipment.getName());
        equipmentCategoryComboBox.getSelectionModel().select(
            categoryService.getCategoryById(equipment.getCategoryId()));
        statusEquipmentText.setText(equipment.getStatus().getDescription());
        equipmentDescriptionTextField.setText(equipment.getDescription());
        lastMaintenanceDateTextField.setText(equipment.getLastMaintenanceTime().toString());
        regularMaintenanceTimeTextField.setText(String.valueOf(equipment.getRegularMaintenanceDay()));
        
        // Clear previous image while loading
        equipmentImage.setImage(null);
        
        // Load image asynchronously
        Task<javafx.scene.image.Image> imageLoadTask = new Task<>() {
            @Override
            protected javafx.scene.image.Image call() throws Exception {
                try {
                    Image imageData = imageService.getImageById(equipment.getImageId());
                    if (imageData != null) {
                        return new javafx.scene.image.Image(imageData.getPath(), true); // true enables background loading
                    }
                    return null;
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to load image: " + e.getMessage(), e);
                    return null;
                }
            }
        };
        
        // Set up callbacks for when image is loaded
        imageLoadTask.setOnSucceeded(event -> {
            javafx.scene.image.Image loadedImage = imageLoadTask.getValue();
            Platform.runLater(() -> {
                if (loadedImage != null) {
                    equipmentImage.setImage(loadedImage);
                }
            });
        });
        
        imageLoadTask.setOnFailed(event -> {
            LOGGER.log(Level.WARNING, "Image loading failed", imageLoadTask.getException());
        });
        
        // Start image loading in background
        new Thread(imageLoadTask).start();
    }

    /**
     * Upload image to cloud service if it has been changed
     */
    private Image uploadImageIfChanged() throws SQLException {
        if (equipmentImage.getImage() != null) {
            File file = new File(equipmentImage.getImage().getUrl().replace("file:", ""));
            String imageUrl = cloudinaryService.uploadImage(file);
            Image image = new Image(file.getName(), imageUrl);
            imageService.addImage(image);
            return image;
        }
        return null;
    }

    /**
     * Refresh equipment data in the table
     */
    private void refreshEquipmentData() throws SQLException {
        String query = equipmentQueryTextField.getText();
        String key = typeFilterComboBox.getSelectionModel().getSelectedItem();
        String value = null;
        
        if (key != null) {
            if ("Category".equals(key)) {
                key = "category_id";
                Category category = (Category) valueFilterComboBox.getSelectionModel().getSelectedItem();
                if (category != null) {
                    value = String.valueOf(category.getId());
                }
            } else if ("Status".equals(key)) {
                Status status = (Status) valueFilterComboBox.getSelectionModel().getSelectedItem();
                if (status != null) {
                    value = String.valueOf(status.getId());
                }
            } else {
                key = null;
            }
        }
        
        List<Equipment> equipments = equipmentService.getEquipments(query, 0, 100, key, value);
        equipmentTable.setItems(FXCollections.observableList(equipments));
    }

    /**
     * Select an equipment in the table
     */
    private void selectEquipmentInTable(Equipment equipment) {
        equipmentTable.getSelectionModel().select(equipment);
        equipmentTable.scrollTo(equipment);
    }
    
    /**
     * Basic form validation
     */
    private boolean validateForm() {
        return equipmentCodeTextField.getText() != null && !equipmentCodeTextField.getText().isEmpty()
            && equipmentNameTextField.getText() != null && !equipmentNameTextField.getText().isEmpty()
            && equipmentCategoryComboBox.getSelectionModel().getSelectedItem() != null
            && regularMaintenanceTimeTextField.getText() != null && !regularMaintenanceTimeTextField.getText().isEmpty();
    }

    /**
     * Centralized exception handling
     */
    private void handleException(Exception e, String message) {
        e.printStackTrace();
        AlertBox.showError("Error", message + ": " + e.getMessage());
    }
    
    /**
     * Execute an operation safely with error handling
     */
    private void safeExecute(SqlOperation operation) {
        try {
            operation.execute();
        } catch (SQLException e) {
            handleException(e, "Database operation failed");
        }
    }

    public void refreshData() {
    }

    /**
     * Functional interface for SQL operations
     */
    @FunctionalInterface
    private interface SqlOperation {
        void execute() throws SQLException;
    }
}