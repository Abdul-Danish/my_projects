package com.mail.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mail.config.model.JobApplicationRequest;
import com.mail.service.EmailService;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send")
    public String sendMail(@RequestBody JobApplicationRequest emailRequest) throws MessagingException, IOException {
//        emailService.sendEmail(emailRequest);
        return "Email sent successfully";
    }

}
