package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.DeviceEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DeviceRepository extends MongoRepository<DeviceEntity,ObjectId>,BaseRepositoryCustom {

	Long deleteByDeviceId(String deviceId);

	DeviceEntity findByDeviceId(String deviceId);
}
