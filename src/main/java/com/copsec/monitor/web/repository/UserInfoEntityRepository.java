package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.UserInfoEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserInfoEntityRepository extends MongoRepository<UserInfoEntity,ObjectId>,BaseRepositoryCustom {
}
