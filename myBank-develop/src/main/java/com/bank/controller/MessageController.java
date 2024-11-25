package com.bank.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.model.Message;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MessageController {

    
    @GetMapping("/public")
    public Message publicEndPoint() {
        return new Message("public uri");
    }
    
    @GetMapping("/test/private")
    public Message privateEndPoint() {
        return new Message("private uri");
    }
    
    @GetMapping("/private-scope")
    public Message privateScopeEndPoint() {
        return new Message("private-scope uri");
    }
    
    @PostMapping("/private-scope/post")
    public String privateScopePostEndPoint(String msg) {
        return msg;
    }
    
}
