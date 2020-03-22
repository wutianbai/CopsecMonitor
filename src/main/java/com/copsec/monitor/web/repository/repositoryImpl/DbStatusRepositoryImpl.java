package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.beans.statistics.DBStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.FileStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
import com.copsec.monitor.web.entity.DBSyncHistoryStatus;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import com.copsec.monitor.web.repository.DbStatusRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

public class DbStatusRepositoryImpl extends BaseRepositoryImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<DBSyncHistoryStatus> getDbData(List<AggregationOperation> operationList) {

		Aggregation agg = Aggregation.newAggregation(operationList);

		AggregationResults<DBSyncHistoryStatus> result = mongoTemplate.aggregate(agg,
				"dbSyncHistoryStatus",DBSyncHistoryStatus.class);
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

		AggregationResults<StatisticalResultBean> result = mongoTemplate.aggregate(agg,
				"dbSyncHistoryStatus",StatisticalResultBean.class);

		return result.getUniqueMappedResult();
	}

	@Override
	public void updateDBSyncHistoryStatus(DBSyncHistoryStatus status) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("deviceId").is(status.getDeviceId())
				.and("taskName").is(status.getTaskName())
				.and("year").is(status.getYear())
				.and("month").is(status.getMonth())
				.and("day").is(status.getDay())
				.and("hour").is(status.getHour()));
		query.addCriteria(criteria);
		DBSyncHistoryStatus preStatus = this.mongoTemplate.findOne(query,DBSyncHistoryStatus.class);
		if(ObjectUtils.isEmpty(preStatus)){

			this.mongoTemplate.save(status);
			return ;
		}
		Update update = new Update();
		update.set("gatherCount",preStatus.getGatherCount() + status.getGatherCount());
		update.set("gatherInsertCount",preStatus.getGatherInsertCount()+ status.getGatherInsertCount());
		update.set("gatherDeleteCount",preStatus.getGatherDeleteCount() +  status.getGatherDeleteCount());
		update.set("gatherUpdateCount",preStatus.getGatherUpdateCount() + status.getGatherUpdateCount());
		update.set("storageCount",preStatus.getStorageCount()+ status.getStorageCount());
		update.set("storageInsertCount",preStatus.getStorageInsertCount() + status.getStorageInsertCount());
		update.set("storageDeleteCount",preStatus.getStorageDeleteCount()+ status.getStorageDeleteCount());
		update.set("storageUpdateCount",status.getStorageUpdateCount() + status.getStorageUpdateCount()) ;
		update.set("updateTime",status.getUpdateTime());
		this.mongoTemplate.updateFirst(query,update,DBSyncHistoryStatus.class);
	}

	@Override
	public List<DBStatisticResultBean> countDatabaseStatistic(List<AggregationOperation> operationList) {

		Aggregation aggregation = Aggregation.newAggregation(operationList);

		AggregationResults<DBStatisticResultBean> result = this.mongoTemplate.aggregate(aggregation,
				"dbSyncHistoryStatus",DBStatisticResultBean.class);

		return result.getMappedResults();
	}
}
