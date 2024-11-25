package com.cache.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.cache.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByFirstNameOrLastName(String firstName, String lastName);
    
}
