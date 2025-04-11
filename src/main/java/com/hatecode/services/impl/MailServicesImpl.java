/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.services.impl;

import com.hatecode.services.interfaces.MailServices;

import java.util.List;
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
    public void sendEmailNotify(String receivedUser, String subject, List<Integer> maintainanceIds, String username, String password) {
        receivedUser = "2251012121quang@ou.edu.vn";
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
}


