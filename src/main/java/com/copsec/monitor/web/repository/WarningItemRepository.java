package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.entity.WarningItemEntity;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarningItemRepository extends MongoRepository<WarningItemEntity,ObjectId>,BaseRepositoryCustom {

	Long deleteByWarningItemId(String warningItemId);

	WarningItemEntity findByWarningItemId(String warningItemId);
}
