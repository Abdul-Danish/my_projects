package com.mail.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mail.config.model.ApplicationType;
import com.mail.config.model.JobApplicationRequest;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class EmailService {

    private List<Map<String, Object>> hrDetails;

    private final JavaMailSender mailSender;

    private int currentIndex = 0;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() throws JacksonException, IOException {
        Resource resource = new ClassPathResource("templates/HR_Emails.json");
        hrDetails = new ObjectMapper().readValue(resource.getInputStream(), new TypeReference<List<Map<String, Object>>>() {
        });
    }

    @Scheduled(fixedRate = 30000)      // 5 minutes
    public void mailSchedule() throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//        String resumeUrl = "https://drive.google.com/file/d/1Gv9n8ArbNYIYJXiKw2qFW8qqP5Awg-3B/view?usp=sharing";
//        byte[] content = new RestTemplate().getForObject(resumeUrl, byte[].class);
//        helper.addAttachment("Abdul_Danish_Resume.pdf", new ByteArrayResource(content));

        File resume = new File("/home/user/Documents/Resume/Resume/Final/confd/quantified/quantified_one_page/Abdul_Danish_Resume.pdf");
        helper.addAttachment("Abdul_Danish_Resume.pdf", resume);

        Map<String, Object> hrDetail = hrDetails.get(currentIndex);

        try {
            Resource resource = new ClassPathResource("templates/job-application-careers.txt");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            Map<String, String> mapVars = new HashMap<>();
            mapVars.put("sender_name", "Abdul Danish");
            mapVars.put("linkedin_link", "https://www.linkedin.com/in/abdul-danish-120872225/");
            mapVars.put("contact_number", "+91 6300339358");
            mapVars.put("sender_email", "abduldanish109@gmail.com");

            // mapVars.put("contact_name", hrDetail.get("contact_name").toString());
            mapVars.put("contact_name", "Hiring Team");
            mapVars.put("company_name", hrDetail.get("company").toString());

            Set<Entry<String, String>> entrySet = mapVars.entrySet();
            for (Entry<String, String> entry : entrySet) {
                template = template.replace("${" + entry.getKey() + "}", entry.getValue());
            }

            helper.setTo(hrDetail.get("email").toString());
            // helper.setSubject("Application for Backend Java Developer Opportunities");
            helper.setSubject("Application for Java Developer Position");
            helper.setText(template, false);

            log.info("sending mail: {} to company: {}", hrDetail.get("contact_name"), hrDetail.get("company"));

            // mailSender.send(message);
            currentIndex++;
            
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("/home/user/Documents/workspace-spring-tool-suite-4-4.19.1.RELEASE/ReferralPilot/src/main/resources/job_updates/sent_emails.txt", true))) {
                writer.write(hrDetail.get("email").toString());
                writer.append(",");
                writer.newLine();
            }
            if (currentIndex >= hrDetails.size()) {
                log.info("All Emails are Exausted");
                while (true) {
                    log.info("please shut down the application...");
                    Thread.sleep(60000);
                }
            }
        } catch (Exception ex) {
            try(BufferedWriter writer = new BufferedWriter(new FileWriter("/home/user/Documents/workspace-spring-tool-suite-4-4.19.1.RELEASE/ReferralPilot/src/main/resources/job_updates/failed_emails.txt", true))) {
                writer.write(hrDetail.get("email").toString());
                writer.append(",");
                writer.newLine();
            }
            
            log.error("Failed to send mail to; {} at mail: {} for company: {}", hrDetail.get("contact_name"), hrDetail.get("email"),
                hrDetail.get("company"), ex);
        }

    }

    /*
     * public void sendEmail(JobApplicationRequest emailRequest) throws MessagingException, IOException { MimeMessage message =
     * mailSender.createMimeMessage(); MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
     * 
     * // helper.setTo(emailRequest.getTo()); // helper.setSubject(emailRequest.getSubject()); // helper.setText(emailRequest.getBody(),
     * emailRequest.getIsHtml());
     * 
     * 
     * String resumeUrl = "https://drive.google.com/file/d/1Gv9n8ArbNYIYJXiKw2qFW8qqP5Awg-3B/view?usp=sharing"; // String resumeUrl =
     * emailRequest.getResumeUrl(); byte[] content = new RestTemplate().getForObject(resumeUrl, byte[].class);
     * helper.addAttachment("resume.pdf", new ByteArrayResource(content));
     * 
     * Resource resource = new ClassPathResource("EmailService.txt"); String template = new String(resource.getInputStream().readAllBytes(),
     * StandardCharsets.UTF_8);
     * 
     * Map<String, String> mapVars = new HashMap<>(); // mapVars.put("sender_name", emailRequest.getSenderEmail()); //
     * mapVars.put("linkedin_link", emailRequest.getLinkedinLink()); // mapVars.put("contact_number", emailRequest.getContactNumber()); //
     * mapVars.put("sender_email", emailRequest.getSenderEmail());
     * 
     * mapVars.put("sender_name", "Abdul Danish"); mapVars.put("linkedin_link", "https://www.linkedin.com/in/abdul-danish-120872225/");
     * mapVars.put("contact_number", "6300339358"); mapVars.put("sender_email", "abduldanish109@gmail.com");
     * 
     * for (Map<String, Object> hrDetail : hrDetails) { mapVars.put("contact_name", hrDetail.get("contact_name").toString());
     * mapVars.put("company_name", hrDetail.get("company").toString());
     * 
     * Set<Entry<String, String>> entrySet = mapVars.entrySet(); for (Entry<String, String> entry : entrySet) { template.replace("${" +
     * entry.getKey() + "}", entry.getValue()); }
     * 
     * helper.setTo(hrDetail.get("email").toString()); helper.setSubject("Application for Backend Java Developer Opportunities");
     * helper.setText(template, false);
     * 
     * mailSender.send(message);
     * 
     * } }
     */

//    public void sendEmail(EmailRequest emailRequest) throws MessagingException {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//
//        helper.setTo(emailRequest.getTo());
//        helper.setSubject(emailRequest.getSubject());
//        helper.setText(emailRequest.getBody(), emailRequest.getIsHtml());
//
////        File resume = new File(emailRequest.getResumePath());
////        helper.addAttachment(
////                resume.getName(),
////                resume
////        );
//
//        String downloadUrl = "https://drive.google.com/file/d/1Gv9n8ArbNYIYJXiKw2qFW8qqP5Awg-3B/view?usp=sharing";
//        byte[] content = new RestTemplate().getForObject(downloadUrl, byte[].class);
//        helper.addAttachment("resume.pdf", new ByteArrayResource(content));
//
//        mailSender.send(message);
//    }

}
