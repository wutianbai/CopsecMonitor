package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.OperateLog;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogRepository extends MongoRepository<OperateLog, ObjectId>, BaseRepositoryCustom {

}
