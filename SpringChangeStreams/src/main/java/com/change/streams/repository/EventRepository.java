package com.change.streams.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.change.streams.model.Event;

public interface EventRepository extends MongoRepository<Event, String> {

}
