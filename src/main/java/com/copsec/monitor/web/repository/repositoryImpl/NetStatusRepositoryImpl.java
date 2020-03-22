package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
import com.copsec.monitor.web.entity.NetDataHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class NetStatusRepositoryImpl extends BaseRepositoryImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<NetDataHistoryStatus> getNetData(List<AggregationOperation> operationList) {


		Aggregation agg = newAggregation(operationList);

		AggregationResults<NetDataHistoryStatus> results = mongoTemplate.aggregate(agg,
				"netDataHistoryStatus",NetDataHistoryStatus.class);

		return results.getMappedResults();
	}

	/**
	 * 计算总量
	 * @param operationList
	 * @return
	 */
	@Override
	public StatisticalResultBean countTotal(List<AggregationOperation> operationList) {

		Aggregation agg = newAggregation(operationList);

		AggregationResults<StatisticalResultBean> results = mongoTemplate.aggregate(agg,
				"netDataHistoryStatus",StatisticalResultBean.class);

		return results.getMappedResults().size() > 1 ? results.getMappedResults().get(0):results.getUniqueMappedResult();

	}


}
