package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.LinkEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LinkRepository extends MongoRepository<LinkEntity,ObjectId>,BaseRepositoryCustom {
}
