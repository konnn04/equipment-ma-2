package com.hatecode.services;

import com.hatecode.pojo.Notification;

import java.sql.SQLException;
import java.util.List;

public interface NotificationService {
    List<Notification> getAllNotifications() throws SQLException;
    List<Notification> getUnreadNotifications() throws SQLException;
    Notification getNotificationById(int id) throws SQLException;
    boolean addNotification(Notification notification) throws SQLException;
    boolean markAsRead(int id) throws SQLException;
    boolean markAllAsRead() throws SQLException;
    boolean deleteNotification(int id) throws SQLException;
    int countUnreadNotifications() throws SQLException;
}