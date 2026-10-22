
package com.alibou.ecommerce.controller;

import com.alibou.ecommerce.email.gmail.GmailEmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private GmailEmailService emailService;

    @InjectMocks
    private NotificationController notificationController;

    @Test
    void sendTestEmail_ShouldSendEmailAndReturnSuccessMessage() {
        String recipient = "customer@example.com";

        String response =
                notificationController.sendTestEmail(recipient);

        assertEquals("Email sent successfully", response);

        verify(emailService).sendEmail(
                recipient,
                "Ecommerce Notification Test",
                "Hello! Gmail integration is working successfully."
        );
    }
}