package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.WarningHistory;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarningHistoryRepository extends MongoRepository<WarningHistory,ObjectId> ,BaseRepositoryCustom {
}
