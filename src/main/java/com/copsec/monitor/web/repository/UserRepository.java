package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.UserEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity,ObjectId>,BaseRepositoryCustom {
}
