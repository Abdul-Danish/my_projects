package com.change.streams.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.change.streams.model.Entity;
import com.change.streams.repository.EntityRepository;

@Component
public class EntityService {

    @Autowired
    private EntityRepository entityRepository;
    
    public void saveEntity(Entity entity) {
        entityRepository.save(entity);
    }
    
    public void updateEntity(Entity entity) {
        entityRepository.save(entity);
    }
    
}
