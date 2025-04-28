package com.hatecode.equipmentma2;

import com.hatecode.pojo.Notification;
import com.hatecode.services.NotificationService;
import com.hatecode.services.impl.NotificationServiceImpl;
import com.hatecode.utils.AlertBox;
import com.hatecode.utils.FormatDate;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationController {
    private static final Logger LOGGER = Logger.getLogger(NotificationController.class.getName());
    
    @FXML private ListView<Notification> notificationList;
    @FXML private Text notificationTitle;
    @FXML private Text notificationDate;
    @FXML private Text notificationDescription;
    @FXML private Text equipmentDetails;
    @FXML private Button markReadButton;
    @FXML private Button markAllReadButton;

    @FXML
    VBox notificationVBox;
    
    private final NotificationService notificationService;
    private Notification selectedNotification;
    
    public NotificationController() {
        this.notificationService = new NotificationServiceImpl();
    }
    
    @FXML
    public void initialize() {
        notificationVBox.setVisible(false);
        setupNotificationListView();
        loadNotifications();
        
        // Thêm listener cho button đánh dấu đã đọc
        markReadButton.setOnAction(event -> markSelectedAsRead());
        markAllReadButton.setOnAction(event -> markAllAsRead());
    }
    
    public void refreshData() {
        loadNotifications();
    }
    
    private void setupNotificationListView() {
        // Tùy chỉnh cell factory cho ListView
        notificationList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Notification notification, boolean empty) {
                super.updateItem(notification, empty);
                
                if (empty || notification == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Format content để hiển thị trong cell
                    String content = notification.getEquipmentName();
                    if (notification.getType() == Notification.NotificationType.MAINTENANCE_DUE) {
                        content += " - Equipment Maintenance Due";
                    } else {
                        content += " - System Notification";
                    }
                    
                    setText(content);
                    
                    // Nếu chưa đọc, hiển thị với font in đậm
                    if (!notification.isRead()) {
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
        
        // Thêm selection listener
        notificationList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedNotification = newVal;
                displayNotificationDetails(newVal);
            }
        });
    }
    
    private void loadNotifications() {
        try {
            List<Notification> notifications = notificationService.getAllNotifications();
            notificationList.setItems(FXCollections.observableArrayList(notifications));
            
            // Nếu có thông báo, hiển thị thông báo đầu tiên
            if (!notifications.isEmpty()) {
                notificationVBox.setVisible(true);
                notificationList.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading notifications", e);
            AlertBox.showError("Error", "Could not load notifications");
        }
    }
    
    private void displayNotificationDetails(Notification notification) {
        if (notification == null) {
            clearDetails();
            return;
        }
        
        // Hiển thị thông tin chi tiết của thông báo
        if (notification.getType() == Notification.NotificationType.MAINTENANCE_DUE) {
            notificationTitle.setText("Equipment Maintenance Due");
            LocalDateTime dueDate = notification.getMaintenanceDueDate();
            notificationDate.setText("Maintenance Due Date: " + FormatDate.formatDateTime(dueDate));
            notificationDescription.setText("This equipment is due for maintenance.");
            equipmentDetails.setText(String.format(
                "Equipment ID: %s\nEquipment Name: %s",
                notification.getEquipmentCode(),
                notification.getEquipmentName()
            ));
        } else {
            notificationTitle.setText(notification.getType().getDescription());
            notificationDate.setText("Created on: " + FormatDate.formatDateTime(notification.getCreatedAt()));
            notificationDescription.setText("System Notification: ");
            equipmentDetails.setText(String.format(
                "Equipment ID: %s\nEquipment Name: %s",
                notification.getEquipmentCode(),
                notification.getEquipmentName()
            ));
        }
        
        // Nếu thông báo chưa đọc, hiển thị nút đánh dấu đã đọc
        markReadButton.setDisable(notification.isRead());
    }
    
    private void clearDetails() {
        notificationTitle.setText("");
        notificationDate.setText("");
        notificationDescription.setText("");
        equipmentDetails.setText("");
        markReadButton.setDisable(true);
    }
    
    private void markSelectedAsRead() {
        if (selectedNotification == null || selectedNotification.isRead()) {
            return;
        }
        
        try {
            if (notificationService.markAsRead(selectedNotification.getId())) {
                selectedNotification.setRead(true);
                loadNotifications(); // Reload để cập nhật giao diện
                AlertBox.showInfo("Complete", "Marked as read successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking notification as read", e);
            AlertBox.showError("Error", "Could not mark notification as read");
        }
    }
    
    private void markAllAsRead() {
        try {
            if (notificationService.markAllAsRead()) {
                loadNotifications(); // Reload để cập nhật giao diện
                AlertBox.showInfo("Complete", "All notifications marked as read successfully");
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking all notifications as read", e);
            AlertBox.showError("Error", "Could not mark all notifications as read");
        }
    }

    public void init() {
        // Initialize the controller
        try {
            loadNotifications();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error initializing NotificationController", e);
            AlertBox.showError("Error", "Could not initialize notifications");
        }
    }
}