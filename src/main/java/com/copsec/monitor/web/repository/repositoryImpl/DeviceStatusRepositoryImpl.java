package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.entity.DeviceNowStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class DeviceStatusRepositoryImpl extends BaseRepositoryImpl {


	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<DeviceNowStatus> countDeviceStatus(ConditionBean conditionBean) {

		Query query = new Query();
		Sort sort = new Sort(conditionBean.getDirection(),conditionBean.getReference());
		query.with(sort).limit(5);
		List<DeviceNowStatus> list = this.mongoTemplate.find(query,DeviceNowStatus.class);
		return list;
	}
}
