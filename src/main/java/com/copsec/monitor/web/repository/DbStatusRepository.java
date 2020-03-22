package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.entity.DBSyncHistoryStatus;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DbStatusRepository extends MongoRepository<DBSyncHistoryStatus,ObjectId>,BaseRepositoryCustom {

}
