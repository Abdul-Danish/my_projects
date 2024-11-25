package com.cache.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.cache.model.RedisUser;

@Repository
public interface UserRedisRepository extends CrudRepository<RedisUser, String> {

}
