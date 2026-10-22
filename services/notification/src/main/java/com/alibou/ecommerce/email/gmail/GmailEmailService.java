package com.alibou.ecommerce.email.gmail;

import com.alibou.ecommerce.kafka.Product;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GmailEmailService {

    private final JavaMailSender mailSender;

    // Temporary method for testing Gmail integration
    public void sendEmail(
            String to,
            String subject,
            String body
    ) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    // Payment confirmation email
    public void sendPaymentSuccessEmail(
            String to,
            String customerName,
            BigDecimal amount,
            String orderReference
    ) throws MessagingException {

        String subject = "Payment Successful - Order " + orderReference;

        String body = """
                <html>
                <body>
                    <h2>Payment Successful</h2>
                
                    <p>Hello %s,</p>
                
                    <p>Your payment has been successfully processed.</p>
                
                    <p>
                        <strong>Order Reference:</strong> %s
                    </p>
                
                    <p>
                        <strong>Amount:</strong> %s
                    </p>
                
                    <p>
                        Thank you for your purchase.
                    </p>
                
                    <p>
                        Regards,<br>
                        AliBouali Ecommerce Team
                    </p>
                </body>
                </html>
                """.formatted(
                customerName,
                orderReference,
                amount
        );

        sendHtmlEmail(to, subject, body);
    }

    // Order confirmation email
    public void sendOrderConfirmationEmail(
            String to,
            String customerName,
            BigDecimal totalAmount,
            String orderReference,
            List<Product> products
    ) throws MessagingException {

        String subject = "Order Confirmation - " + orderReference;

        StringBuilder productRows = new StringBuilder();

        for (Product product : products) {

            BigDecimal lineTotal = product.price()
                    .multiply(BigDecimal.valueOf(product.quantity()));

            productRows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                    </tr>
                    """.formatted(
                    product.name(),
                    product.quantity(),
                    product.price(),
                    lineTotal
            ));
        }

        String body = """
                <html>
                <body>
                
                    <h2>Order Confirmation</h2>
                
                    <p>Hello %s,</p>
                
                    <p>
                        Thank you for your order.
                        Your order has been successfully created.
                    </p>
                
                    <p>
                        <strong>Order Reference:</strong> %s
                    </p>
                
                    <h3>Order Details</h3>
                
                    <table border="1" cellpadding="8" cellspacing="0">
                        <thead>
                            <tr>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Unit Price</th>
                                <th>Total</th>
                            </tr>
                        </thead>
                
                        <tbody>
                            %s
                        </tbody>
                    </table>
                
                    <br>
                
                    <p>
                        <strong>Order Total:</strong> %s
                    </p>
                
                    <p>
                        Regards,<br>
                        AliBouali Ecommerce Team
                    </p>
                
                </body>
                </html>
                """.formatted(
                customerName,
                orderReference,
                productRows,
                totalAmount
        );

        sendHtmlEmail(to, subject, body);
    }

    private void sendHtmlEmail(
            String to,
            String subject,
            String body
    ) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, true);

        mailSender.send(message);
    }
}