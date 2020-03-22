package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;

public interface AuditSyslogMessageRepository extends MongoRepository<AuditSyslogMessage, ObjectId>, BaseRepositoryCustom {

    void deleteAllByReportTimeBefore(Date reportTime);
}
