package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.entity.AuditSyslogMessage;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class AuditSyslogMessageRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<AuditSyslogMessage> getServerMessage(Query query, Pageable pageable) {

        long count = mongoTemplate.count(query, AuditSyslogMessage.class, "auditMessage");
        query.with(pageable);
        List<AuditSyslogMessage> list = mongoTemplate.find(query, AuditSyslogMessage.class);
        return new PageImpl<>(list, pageable, count);
    }
}
