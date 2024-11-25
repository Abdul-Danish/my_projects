package com.cache.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cache.model.RedisUser;
import com.cache.model.User;
import com.cache.model.UserDto;
import com.cache.repository.UserRedisRepository;
import com.cache.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/users/")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserRedisRepository redisRepository;
    
    // impl for 'RedisTemplate' (Ideal for more complex Redis operations, such as manipulating lists, sets, hashes, or using Redis as a message broker.) 
    
    @PostMapping("redis/")
    public ResponseEntity<RedisUser> addUserRedis(@RequestBody UserDto user) {
        if (user != null) {
            RedisUser userObj = redisRepository.save(RedisUser.builder().firstName(user.getFirstName()).lastName(user.getLastNmae()).build());
            return ResponseEntity.ok(userObj);
        }
        return null;
    }
    
    
    // impl for 'RedisCacheManager' (Ideal for general caching scenarios where we want to cache method results easily.)
    
    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody UserDto user) {
        User build = User.builder().firstName(user.getFirstName()).lastName(user.getLastNmae()).build();
        User userObj = userRepository.save(build);
        return ResponseEntity.ok(userObj);
    }
    
    @GetMapping("{userId}/")
    @Cacheable(cacheNames = "User", key = "#userId")
    public User getUser(@PathVariable String userId) {
        log.info("Retrieving User from db");
        Optional<User> userObj = userRepository.findById(userId);
        if (userObj.isPresent()) {
            return userObj.get();
        }
        return null;
    }
    
    @PutMapping
    @CachePut(cacheNames = "User", key = "#user.id")
    public ResponseEntity<User> updateUser(@RequestBody UserDto user) {
        Optional<User> userOpt = userRepository.findByFirstNameOrLastName(user.getFirstName(), user.getLastNmae());
        if (userOpt.isPresent()) {
            User usr = userOpt.get();
            User userObj = userRepository.save(new User(usr.getId(), user.getFirstName(), user.getLastNmae()));
            return ResponseEntity.ok(userObj);
        }
        return null;
    }
    
    @DeleteMapping("{userId}")
    @CacheEvict(cacheNames = "User", key = "#userId")
    public void removeUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
    }
    
}
