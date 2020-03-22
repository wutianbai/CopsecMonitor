package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.entity.SyslogMessage;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DeviceMessageRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate logMongoTemplate;

    @Override
    public List<SyslogMessage> findByIdAfter() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("host").exists(true));
        query.addCriteria(criteria).limit(10000);
        List<SyslogMessage> results = this.logMongoTemplate.find(query, SyslogMessage.class);
        return results;
    }

    @Override
    public void deleteDeviceMessage(SyslogMessage deviceMessage) {
        this.logMongoTemplate.remove(deviceMessage);
    }
}
