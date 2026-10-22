package com.alibou.ecommerce.controller;

import com.alibou.ecommerce.email.gmail.GmailEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final GmailEmailService emailService;

    @PostMapping("/test-email")
    public String sendTestEmail(
            @RequestParam String to
    ) {

        emailService.sendEmail(
                to,
                "Ecommerce Notification Test",
                "Hello! Gmail integration is working successfully."
        );

        return "Email sent successfully";
    }
}
