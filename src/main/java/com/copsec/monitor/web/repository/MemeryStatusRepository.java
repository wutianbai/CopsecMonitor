package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.MemoryHistoryStatus;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeryStatusRepository extends MongoRepository<MemoryHistoryStatus,ObjectId>,BaseRepositoryCustom {
}
