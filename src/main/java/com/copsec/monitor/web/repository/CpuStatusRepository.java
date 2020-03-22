package com.copsec.monitor.web.repository;

import java.util.List;
import java.util.Optional;

import com.copsec.monitor.web.entity.CpuHistoryStatus;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.bson.types.ObjectId;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CpuStatusRepository extends MongoRepository<CpuHistoryStatus,ObjectId>,BaseRepositoryCustom {

	List<CpuHistoryStatus> findByDeviceIdAndYearAndMonthAndDayAndMaxUseRate(String deviceid,
			int year,int month,int day,double maxUseRate);
}
