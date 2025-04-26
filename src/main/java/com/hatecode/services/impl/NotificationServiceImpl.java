package com.hatecode.services.impl;

import com.hatecode.pojo.Notification;
import com.hatecode.services.NotificationService;
import com.hatecode.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NotificationServiceImpl implements NotificationService {
    private static final Logger LOGGER = Logger.getLogger(NotificationServiceImpl.class.getName());

    @Override
    public List<Notification> getAllNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotification(rs));
            }
        }
        
        return notifications;
    }

    @Override
    public List<Notification> getUnreadNotifications() throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE is_read = FALSE ORDER BY created_at DESC";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotification(rs));
            }
        }
        
        return notifications;
    }

    @Override
    public Notification getNotificationById(int id) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractNotification(rs);
            }
        }
        
        return null;
    }

    @Override
    public boolean addNotification(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (equipment_id, equipment_name, equipment_code, " +
                    "maintenance_due_date, type) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notification.getEquipmentId());
            stmt.setString(2, notification.getEquipmentName());
            stmt.setString(3, notification.getEquipmentCode());
            stmt.setTimestamp(4, Timestamp.valueOf(notification.getMaintenanceDueDate()));
            stmt.setInt(5, notification.getType().getCode());
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markAsRead(int id) throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE id = ?";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean markAllAsRead() throws SQLException {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE is_read = FALSE";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteNotification(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public int countUnreadNotifications() throws SQLException {
        String sql = "SELECT COUNT(*) FROM notifications WHERE is_read = FALSE";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    private Notification extractNotification(ResultSet rs) throws SQLException {
        return new Notification(
            rs.getInt("id"),
            rs.getInt("equipment_id"),
            rs.getString("equipment_name"),
            rs.getString("equipment_code"),
            rs.getTimestamp("maintenance_due_date").toLocalDateTime(),
            rs.getTimestamp("created_at").toLocalDateTime(),
            rs.getBoolean("is_read"),
            Notification.NotificationType.values()[rs.getInt("type") - 1]
        );
    }
}