package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.entity.ProtocolHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;

public class ProtocolStatusRepositoryImpl extends BaseRepositoryImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<ProtocolHistoryStatus> getProtocolData(List<AggregationOperation> operationList) {

		Aggregation agg = Aggregation.newAggregation(operationList);

		AggregationResults<ProtocolHistoryStatus> result =
				mongoTemplate.aggregate(agg,"protocolHistoryStatus",ProtocolHistoryStatus.class);

		return result.getMappedResults();
	}

	/**
	 * 计算总量
	 * @param operationList
	 * @return
	 */
	@Override
	public StatisticalResultBean countTotal(List<AggregationOperation> operationList) {

		Aggregation agg = Aggregation.newAggregation(operationList);

		AggregationResults<StatisticalResultBean> result =
				mongoTemplate.aggregate(agg,"protocolHistoryStatus",StatisticalResultBean.class);

		return result.getUniqueMappedResult();
	}

	@Override
	public void updateProtocolHistoryStatus(ProtocolHistoryStatus status) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("deviceId").is(status.getDeviceId())
				.and("taskName").is(status.getTaskName())
				.and("year").is(status.getYear())
				.and("month").is(status.getMonth())
				.and("day").is(status.getDay())
				.and("hour").is(status.getHour()));
		query.addCriteria(criteria);
		ProtocolHistoryStatus preStatus = this.mongoTemplate.findOne(query,ProtocolHistoryStatus.class);
		if(ObjectUtils.isEmpty(preStatus)){

			this.mongoTemplate.save(status);
			return ;
		}
		Update update = new Update();
		update.set("handleConnectCount",preStatus.getHandleConnectCount() + status.getHandleConnectCount());
		update.set("handleErrorConnectCount",preStatus.getHandleErrorConnectCount() + status.getHandleErrorConnectCount());
		update.set("handleNetDataSize",preStatus.getHandleNetDataSize() + status.getHandleNetDataSize());
		update.set("updateTime",status.getUpdateTime());
		this.mongoTemplate.updateFirst(query,update,ProtocolHistoryStatus.class);
	}
}
