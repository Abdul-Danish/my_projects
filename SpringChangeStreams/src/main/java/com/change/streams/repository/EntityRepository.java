package com.change.streams.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.change.streams.model.Entity;

@Repository
public interface EntityRepository extends MongoRepository<Entity, String> {

}
