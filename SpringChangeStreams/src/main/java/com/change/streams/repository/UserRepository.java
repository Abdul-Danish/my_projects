package com.change.streams.repository;

import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.change.streams.model.User;

@Repository
@JaversSpringDataAuditable
public interface UserRepository extends MongoRepository<User, String> {

}
