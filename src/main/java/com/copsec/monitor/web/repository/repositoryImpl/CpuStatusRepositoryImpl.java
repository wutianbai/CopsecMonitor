package com.copsec.monitor.web.repository.repositoryImpl;

import java.util.List;

import com.copsec.monitor.web.entity.CpuHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class CpuStatusRepositoryImpl extends BaseRepositoryImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public List<CpuHistoryStatus> getCpuStatus(List<AggregationOperation> operationList) {

		Aggregation agg = newAggregation(operationList);

		AggregationResults<CpuHistoryStatus> results = mongoTemplate.aggregate(agg,
				"cpuHistoryStatus",CpuHistoryStatus.class);
		List<CpuHistoryStatus> resultList = results.getMappedResults();
		return resultList;
	}
}
