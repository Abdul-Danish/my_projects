package com.change.streams.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.change.streams.model.Entity;
import com.change.streams.model.User;
import com.change.streams.service.EntityService;
import com.change.streams.service.UserService;

@RestController
@RequestMapping("api/v1")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private EntityService entityService;
    
    @PostMapping("/users")
    public void createUser(@RequestBody User user) {
        userService.saveUser(user);
    }
    
    @PutMapping("/users")
    public void updateUser(@RequestBody User user) {
        userService.updateUser(user);
    }
    
    @DeleteMapping("/users")
    public void removeUser(@RequestBody User user) {
        userService.removeUser(user);
    }
    
    @PostMapping("/entities")
    public void createEntiy(@RequestBody Entity entity) {
        entityService.saveEntity(entity);
    }
    
    @PutMapping("/entities")
    public void updateEntiy(@RequestBody Entity entity) {
        entityService.updateEntity(entity);
    }
}
