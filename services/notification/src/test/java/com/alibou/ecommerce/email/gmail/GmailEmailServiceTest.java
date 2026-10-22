package com.alibou.ecommerce.email.gmail;

import com.alibou.ecommerce.kafka.Product;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GmailEmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    private GmailEmailService gmailEmailService;
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        mimeMessage = new MimeMessage(
                Session.getInstance(new Properties())
        );

        gmailEmailService = new GmailEmailService(mailSender);
    }

    @Test
    void sendEmail_ShouldSendPlainTextEmail() {
        gmailEmailService.sendEmail(
                "customer@example.com",
                "Test Subject",
                "Test email body"
        );

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();

        assertArrayEquals(
                new String[]{"customer@example.com"},
                message.getTo()
        );
        assertEquals("Test Subject", message.getSubject());
        assertEquals("Test email body", message.getText());
    }

    @Test
    void sendPaymentSuccessEmail_ShouldSendHtmlEmail()
            throws Exception {

        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        gmailEmailService.sendPaymentSuccessEmail(
                "customer@example.com",
                "John Smith",
                new BigDecimal("150.00"),
                "ORDER-101"
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        assertEquals(
                "Payment Successful - Order ORDER-101",
                mimeMessage.getSubject()
        );

        assertEquals(
                "customer@example.com",
                mimeMessage.getRecipients(Message.RecipientType.TO)[0]
                        .toString()
        );

        String body = extractBody(mimeMessage);

        assertTrue(body.contains("Payment Successful"));
        assertTrue(body.contains("John Smith"));
        assertTrue(body.contains("ORDER-101"));
        assertTrue(body.contains("150.00"));
    }

    @Test
    void sendOrderConfirmationEmail_ShouldIncludeProductDetails()
            throws Exception {

        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        Product product = mock(Product.class);

        when(product.name()).thenReturn("Laptop");
        when(product.quantity()).thenReturn(2.0);
        when(product.price()).thenReturn(new BigDecimal("500.00"));

        gmailEmailService.sendOrderConfirmationEmail(
                "customer@example.com",
                "Jane Smith",
                new BigDecimal("1000.00"),
                "ORDER-202",
                List.of(product)
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        assertEquals(
                "Order Confirmation - ORDER-202",
                mimeMessage.getSubject()
        );

        String body = extractBody(mimeMessage);

        assertTrue(body.contains("Order Confirmation"));
        assertTrue(body.contains("Jane Smith"));
        assertTrue(body.contains("ORDER-202"));
        assertTrue(body.contains("Laptop"));
        assertTrue(body.contains("500.00"));
        assertTrue(body.contains("1000.00"));
    }

    @Test
    void sendOrderConfirmationEmail_WithNoProducts_ShouldSendEmail()
            throws Exception {

        when(mailSender.createMimeMessage())
                .thenReturn(mimeMessage);

        gmailEmailService.sendOrderConfirmationEmail(
                "customer@example.com",
                "Jane Smith",
                BigDecimal.ZERO,
                "ORDER-303",
                List.of()
        );

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);

        String body = extractBody(mimeMessage);

        assertTrue(body.contains("ORDER-303"));
        assertTrue(body.contains("Order Total"));
        assertFalse(body.contains("<td>Laptop</td>"));
    }

    private String extractBody(Part part) throws Exception {
        Object content = part.getContent();

        if (content instanceof String text) {
            return text;
        }

        if (content instanceof Multipart multipart) {
            StringBuilder body = new StringBuilder();

            for (int i = 0; i < multipart.getCount(); i++) {
                body.append(
                        extractBody(multipart.getBodyPart(i))
                );
            }

            return body.toString();
        }

        return "";
    }
}