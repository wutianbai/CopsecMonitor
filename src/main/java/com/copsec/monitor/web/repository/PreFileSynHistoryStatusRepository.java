package com.copsec.monitor.web.repository;

import java.util.List;

import com.copsec.monitor.web.entity.PreFileSynHistoryStatus;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PreFileSynHistoryStatusRepository extends MongoRepository<PreFileSynHistoryStatus,ObjectId>,BaseRepositoryCustom {


}
