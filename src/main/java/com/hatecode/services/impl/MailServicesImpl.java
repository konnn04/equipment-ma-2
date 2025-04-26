/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.config.AppConfig;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.MailServices;
import com.hatecode.utils.FormatDate;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.*;
import jakarta.mail.internet.*;

/**
 * @author ADMIN
 */
public class MailServicesImpl implements MailServices {

    private static final Logger logger = Logger.getLogger(MailServicesImpl.class.getName());

    @Override
    public void sendEmailNotify(String receivedUser, String subject, List<Integer> maintainanceIds) {
        String username = AppConfig.EMAIL_USERNAME;
        String password = AppConfig.EMAIL_PASSWORD; // Mật khẩu ứng dụng đã tạo trên Gmail
        // receivedUser = "2251012121quang@ou.edu.vn";
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN"); // Bắt buộc dùng cơ chế cơ bản
        props.put("mail.smtp.auth.plain.disable", "false");
        props.put("mail.smtp.auth.login.disable", "false");
        props.put("mail.smtp.auth.xoauth2.disable", "true"); // Tắt OAUTH2

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receivedUser));
            message.setSubject(subject); // Sử dụng subject được truyền vào

            String htmlContent = buildEmailContent(receivedUser, maintainanceIds);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Enable debug để xem log quá trình gửi mail
            session.setDebug(true);

            Transport.send(message);

            logger.log(Level.INFO, "Email sent successfully to {0}", receivedUser);

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send email to " + receivedUser, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    /**
     * Send enhanced email with detailed maintenance and equipment information
     * 
     * @param receivedUser Email address of the recipient
     * @param subject Email subject
     * @param maintenance The maintenance schedule
     * @param equipmentMaintenanceList List of equipment maintenance records
     */
    public void sendEnhancedMaintenanceEmail(
            String receivedUser, 
            String subject, 
            Maintenance maintenance,
            List<EquipmentMaintenance> equipmentMaintenanceList) {
        
        String username = AppConfig.EMAIL_USERNAME;
        String password = AppConfig.EMAIL_PASSWORD;
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth.mechanisms", "PLAIN LOGIN");
        props.put("mail.smtp.auth.plain.disable", "false");
        props.put("mail.smtp.auth.login.disable", "false");
        props.put("mail.smtp.auth.xoauth2.disable", "true");

        Session session = Session.getInstance(props,
                new jakarta.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(receivedUser));
            message.setSubject(subject);

            String htmlContent = buildEnhancedEmailContent(receivedUser, maintenance, equipmentMaintenanceList);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            session.setDebug(true);
            Transport.send(message);

            logger.log(Level.INFO, "Enhanced email sent successfully to {0}", receivedUser);

        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Failed to send enhanced email to " + receivedUser, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private static String buildEmailContent(String employeeName, List<Integer> deviceCodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Kính gửi anh/chị ").append(employeeName).append(",</h2>");
        sb.append("<p>Dưới đây là danh sách các thiết bị cần được bảo trì:</p>");
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        sb.append("<tr><th style='padding: 8px;'>STT</th><th style='padding: 8px;'>Mã Thiết Bị</th></tr>");

        for (int i = 0; i < deviceCodes.size(); i++) {
            sb.append("<tr>")
                    .append("<td style='padding: 8px;'>").append(i + 1).append("</td>")
                    .append("<td style='padding: 8px;'>").append(deviceCodes.get(i)).append("</td>")
                    .append("</tr>");
        }

        sb.append("</table>");
        sb.append("<p>Vui lòng sắp xếp thời gian bảo trì các thiết bị trên.</p>");
        sb.append("<p>Trân trọng,<br>Phòng Kỹ Thuật</p>");
        sb.append("</body></html>");
        return sb.toString();
    }
    
    /**
     * Build enhanced HTML content for the maintenance notification email
     */
    private static String buildEnhancedEmailContent(
            String employeeName, 
            Maintenance maintenance, 
            List<EquipmentMaintenance> equipmentMaintenanceList) {
        
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("<h2>Kính gửi anh/chị ").append(employeeName).append(",</h2>");
        
        // Add maintenance schedule information
        sb.append("<div style='background-color: #f9f9f9; border: 1px solid #ddd; padding: 15px; margin-bottom: 20px;'>");
        sb.append("<h3 style='color: #0066cc; margin-top: 0;'>Thông Tin Lịch Bảo Trì</h3>");
        sb.append("<p><strong>Tiêu đề:</strong> ").append(maintenance.getTitle()).append("</p>");
        
        if (maintenance.getDescription() != null && !maintenance.getDescription().isEmpty()) {
            sb.append("<p><strong>Mô tả:</strong> ").append(maintenance.getDescription()).append("</p>");
        }
        
        sb.append("<p><strong>Thời gian bắt đầu:</strong> ").append(FormatDate.formatDateTime(maintenance.getStartDateTime())).append("</p>");
        sb.append("<p><strong>Thời gian kết thúc:</strong> ").append(FormatDate.formatDateTime(maintenance.getEndDateTime())).append("</p>");
        sb.append("</div>");
        
        // Add equipment table with more details
        sb.append("<h3 style='color: #0066cc;'>Danh Sách Thiết Bị Cần Bảo Trì</h3>");
        sb.append("<table border='1' style='border-collapse: collapse; width: 100%; margin-bottom: 20px;'>");
        sb.append("<tr style='background-color: #f2f2f2;'>");
        sb.append("<th style='padding: 10px; text-align: center;'>STT</th>");
        sb.append("<th style='padding: 10px; text-align: center;'>Mã Thiết Bị</th>");
        sb.append("<th style='padding: 10px; text-align: center;'>Tên Thiết Bị</th>");
        sb.append("</tr>");

        for (int i = 0; i < equipmentMaintenanceList.size(); i++) {
            EquipmentMaintenance em = equipmentMaintenanceList.get(i);
            sb.append("<tr>")
                .append("<td style='padding: 8px; text-align: center;'>").append(i + 1).append("</td>")
                .append("<td style='padding: 8px;'>").append(em.getEquipmentCode()).append("</td>")
                .append("<td style='padding: 8px;'>").append(em.getEquipmentName()).append("</td>")
                .append("</tr>");
        }

        sb.append("</table>");
        
        // Add additional instructions
        sb.append("<div style='background-color: #f9f9f9; border: 1px solid #ddd; padding: 15px;'>");
        sb.append("<p><strong>Lưu ý:</strong></p>");
        sb.append("<ul>");
        sb.append("<li>Vui lòng tuân thủ đúng lịch trình bảo trì đã đề ra</li>");
        sb.append("<li>Báo cáo tình trạng thiết bị sau khi hoàn thành công việc</li>");
        sb.append("<li>Liên hệ quản lý nếu gặp vấn đề hoặc có câu hỏi</li>");
        sb.append("</ul>");
        sb.append("</div>");
        
        // Add footer
        sb.append("<p style='margin-top: 20px;'>Trân trọng,<br><strong>Phòng Kỹ Thuật</strong></p>");
        sb.append("</body></html>");
        
        return sb.toString();
    }
}


