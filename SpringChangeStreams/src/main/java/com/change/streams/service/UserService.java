package com.change.streams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.change.streams.model.User;
import com.change.streams.repository.UserRepository;

@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    public void saveUser(User user) {
        userRepository.save(user);
    }
    
    public void updateUser(User user) {
        userRepository.save(user);
    }
    
    public void removeUser(User user) {
        userRepository.deleteById(user.getId());
    }
    
}
