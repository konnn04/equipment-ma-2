package com.hatecode.services;

import com.hatecode.config.AppConfig;
import com.hatecode.pojo.Equipment;
import com.hatecode.pojo.Notification;
import com.hatecode.pojo.Status;
import com.hatecode.services.impl.EquipmentServiceImpl;
import com.hatecode.services.impl.NotificationServiceImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaintenanceCheckService {
    private static final Logger LOGGER = Logger.getLogger(MaintenanceCheckService.class.getName());
    
    private final EquipmentService equipmentService;
    private final NotificationService notificationService;
    
    public MaintenanceCheckService() {
        this.equipmentService = new EquipmentServiceImpl();
        this.notificationService = new NotificationServiceImpl();
    }
    
    /**
     * Kiểm tra tất cả thiết bị và tạo thông báo cho thiết bị sắp đến hạn bảo trì
     */
    public void checkAllEquipmentForMaintenance() {
        try {
            LOGGER.info("Bắt đầu kiểm tra bảo trì cho tất cả thiết bị");
            
            // Lấy tất cả thiết bị đang hoạt động
            List<Equipment> equipments = equipmentService.getEquipments();
            
            int notificationsCreated = 0;
            int notificationsDeleted = 0;
            
            // Xóa tất cả thông báo cũ không còn phù hợp
            // (thiết bị đã bảo trì hoặc quá hạn cảnh báo)
            cleanupOldNotifications();
            
            for (Equipment equipment : equipments) {
                // Chỉ kiểm tra thiết bị đang hoạt động và có cài đặt bảo trì định kỳ
                if (equipment.getStatus() == Status.ACTIVE && 
                    equipment.getRegularMaintenanceDay() > 0 &&
                    equipment.getLastMaintenanceTime() != null) {
                    
                    // Tính ngày bảo trì tiếp theo: lastMaintenanceTime + regularMaintenanceDay
                    LocalDateTime nextMaintenanceDate = equipment.getLastMaintenanceTime()
                            .plusDays(equipment.getRegularMaintenanceDay());
                    
                    // Kiểm tra nếu thiết bị sắp đến hạn bảo trì
                    if (isMaintenanceDueSoon(nextMaintenanceDate)) {
                        // Tạo thông báo cho thiết bị này
                        if (createMaintenanceNotification(equipment, nextMaintenanceDate)) {
                            notificationsCreated++;
                        }
                    }
                }
            }
            
            LOGGER.info("Kiểm tra bảo trì hoàn tất. Đã tạo " + notificationsCreated + 
                       " thông báo mới và xóa " + notificationsDeleted + " thông báo cũ.");
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra bảo trì thiết bị", e);
        }
    }
    
    /**
     * Xóa thông báo cũ không còn phù hợp
     */
    private int cleanupOldNotifications() throws SQLException {
        int deleted = 0;
        List<Notification> notifications = notificationService.getAllNotifications();
        
        for (Notification notification : notifications) {
            if (notification.getType() == Notification.NotificationType.MAINTENANCE_DUE) {
                // Kiểm tra xem thiết bị có còn trong hệ thống không
                Equipment equipment = equipmentService.getEquipmentById(notification.getEquipmentId());
                
                if (equipment == null // Thiết bị không còn tồn tại
                    || equipment.getStatus() != Status.ACTIVE // Thiết bị không còn hoạt động
                    || !isMaintenanceDueSoon(notification.getMaintenanceDueDate()) // Đã quá thời gian cảnh báo
                    || notification.getMaintenanceDueDate().isBefore(LocalDateTime.now())) { // Đã quá ngày bảo trì
                    
                    // Xóa thông báo
                    notificationService.deleteNotification(notification.getId());
                    deleted++;
                }
            }
        }
        
        return deleted;
    }
    
    /**
     * Kiểm tra xem ngày bảo trì có sắp đến hạn không
     * (trong khoảng thời gian cảnh báo từ AppConfig)
     */
    private boolean isMaintenanceDueSoon(LocalDateTime nextMaintenanceDate) {
        LocalDate today = LocalDate.now();
        LocalDate dueDate = nextMaintenanceDate.toLocalDate();
        
        // Nếu ngày bảo trì tiếp theo đã qua, không cần thông báo
        if (dueDate.isBefore(today)) {
            return false;
        }
        
        // Tính số ngày chính xác từ hôm nay đến ngày bảo trì tiếp theo
        long daysUntilMaintenance = ChronoUnit.DAYS.between(today, dueDate);
        
        // Nếu số ngày <= ngưỡng cảnh báo, tạo thông báo
        return daysUntilMaintenance <= AppConfig.NUMBER_DAY_TO_WARN_BEFORE_MAINTENANCE;
    }
    
    /**
     * Tạo thông báo cho thiết bị sắp đến hạn bảo trì và kiểm tra trùng lặp
     * @return true nếu tạo thông báo mới, false nếu đã có thông báo
     */
    private boolean createMaintenanceNotification(Equipment equipment, LocalDateTime maintenanceDate) 
            throws SQLException {
        
        // Kiểm tra xem đã có thông báo cho thiết bị và ngày bảo trì này chưa
        if (!hasExistingNotification(equipment.getId(), maintenanceDate)) {
            Notification notification = new Notification();
            notification.setEquipmentId(equipment.getId());
            notification.setEquipmentName(equipment.getName());
            notification.setEquipmentCode(equipment.getCode());
            notification.setMaintenanceDueDate(maintenanceDate);
            notification.setType(Notification.NotificationType.MAINTENANCE_DUE);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            
            notificationService.addNotification(notification);
            
            LOGGER.info(String.format("Tạo thông báo bảo trì mới cho thiết bị: %s (ID: %d), ngày bảo trì: %s",
                equipment.getName(), equipment.getId(), maintenanceDate.toString()));
                
            return true;
        }
        
        return false;
    }
    
    /**
     * Kiểm tra xem đã có thông báo cho thiết bị và ngày bảo trì này chưa
     */
    private boolean hasExistingNotification(int equipmentId, LocalDateTime maintenanceDate) throws SQLException {
        List<Notification> notifications = notificationService.getUnreadNotifications();
        
        for (Notification notification : notifications) {
            if (notification.getEquipmentId() == equipmentId && 
                notification.getType() == Notification.NotificationType.MAINTENANCE_DUE &&
                notification.getMaintenanceDueDate().toLocalDate().equals(maintenanceDate.toLocalDate())) {
                return true;
            }
        }
        
        return false;
    }
}