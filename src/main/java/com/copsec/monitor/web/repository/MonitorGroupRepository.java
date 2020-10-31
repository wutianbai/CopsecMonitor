package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.MonitorGroupEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface MonitorGroupRepository extends MongoRepository<MonitorGroupEntity,ObjectId>,BaseRepositoryCustom {

	Long deleteByMonitorGroupId(String monitorGroupId);

	MonitorGroupEntity findByMonitorGroupId(String monitorGroupId);

}
