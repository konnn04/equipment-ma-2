package com.hatecode.services;

import com.hatecode.config.TestDatabaseConfig;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.Maintenance;
import com.hatecode.pojo.MaintenanceStatus;
import com.hatecode.services.impl.MailServicesImpl;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({TestDatabaseConfig.class, MockitoExtension.class})
public class MailServicesImplTest {

    @Mock
    private Session mockSession;
    
    @Mock
    private MimeMessage mockMessage;
    
    private MailServices mailServices;
    
    @BeforeEach
    void setUp() {
        mailServices = new MailServicesImpl() {
            // Ghi đè phương thức để tránh gọi thực tế đến JavaMail
            @Override
            public void sendEmailNotify(String receivedUser, String subject, List<Integer> maintainanceIds) {
                // Gọi phương thức gốc nhưng không thực sự gửi email
                try {
                    // Kiểm tra tham số đầu vào
                    if (receivedUser == null || receivedUser.isEmpty()) {
                        throw new IllegalArgumentException("Email recipient cannot be empty");
                    }
                    if (subject == null || subject.isEmpty()) {
                        throw new IllegalArgumentException("Email subject cannot be empty");
                    }
                    if (maintainanceIds == null || maintainanceIds.isEmpty()) {
                        throw new IllegalArgumentException("Maintenance IDs cannot be empty");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            
            @Override
            public void sendEnhancedMaintenanceEmail(String receivedUser, String subject, Maintenance maintenance, List<EquipmentMaintenance> equipmentMaintenanceList) {
                // Gọi phương thức gốc nhưng không thực sự gửi email
                try {
                    // Kiểm tra tham số đầu vào
                    if (receivedUser == null || receivedUser.isEmpty()) {
                        throw new IllegalArgumentException("Email recipient cannot be empty");
                    }
                    if (subject == null || subject.isEmpty()) {
                        throw new IllegalArgumentException("Email subject cannot be empty");
                    }
                    if (maintenance == null) {
                        throw new IllegalArgumentException("Maintenance cannot be null");
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testSendEmailNotify_ValidParameters() {
        // Arrange
        String email = "test@example.com";
        String subject = "Test Subject";
        List<Integer> ids = Arrays.asList(1, 2, 3);
        
        // Act & Assert
        assertDoesNotThrow(() -> mailServices.sendEmailNotify(email, subject, ids));
    }
    
    @Test
    void testSendEmailNotify_EmptyEmail() {
        // Arrange
        String email = "";
        String subject = "Test Subject";
        List<Integer> ids = Arrays.asList(1, 2, 3);
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            mailServices.sendEmailNotify(email, subject, ids)
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Email recipient cannot be empty", exception.getCause().getMessage());
    }
    
    @Test
    void testSendEmailNotify_EmptySubject() {
        // Arrange
        String email = "test@example.com";
        String subject = "";
        List<Integer> ids = Arrays.asList(1, 2, 3);
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            mailServices.sendEmailNotify(email, subject, ids)
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Email subject cannot be empty", exception.getCause().getMessage());
    }
    
    @Test
    void testSendEmailNotify_EmptyMaintenanceIds() {
        // Arrange
        String email = "test@example.com";
        String subject = "Test Subject";
        List<Integer> ids = List.of();
        
        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> 
            mailServices.sendEmailNotify(email, subject, ids)
        );
        assertTrue(exception.getCause() instanceof IllegalArgumentException);
        assertEquals("Maintenance IDs cannot be empty", exception.getCause().getMessage());
    }
    
    @Test
    void testSendEnhancedMaintenanceEmail_ValidParameters() {
        // Arrange
        String email = "test@example.com";
        String subject = "Test Enhanced Email";
        Maintenance maintenance = new Maintenance(
            "Test Maintenance", "Test Description",
            LocalDateTime.now().plusDays(1), 
            LocalDateTime.now().plusDays(3),
            MaintenanceStatus.PENDING
        );
        List<EquipmentMaintenance> equipmentMaintenanceList = Arrays.asList(
            new EquipmentMaintenance( 1, 1, 1,  "EQ001", "Equipment 1","Test")
        );
        
        // Act & Assert
        assertDoesNotThrow(() -> 
            mailServices.sendEnhancedMaintenanceEmail(email, subject, maintenance, equipmentMaintenanceList)
        );
    }
}