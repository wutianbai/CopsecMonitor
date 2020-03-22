package com.copsec.monitor.web.repository;


import com.copsec.monitor.web.entity.DeviceNowStatus;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceStatusRepository extends MongoRepository<DeviceNowStatus,ObjectId> ,BaseRepositoryCustom {
	void deleteByDeviceId(String deviceId);
}
