package com.mail.config.model;

import lombok.Data;

@Data
public class JobApplicationRequest {
    
    private String senderName;
    private String linkedinLink;
    private int contactNumber;
    private String senderEmail;
//    private String to;
    private String subject;
//    private Boolean isHtml;
    private String resumeUrl;
//    private String body;
    private ApplicationType applicationType;

}
