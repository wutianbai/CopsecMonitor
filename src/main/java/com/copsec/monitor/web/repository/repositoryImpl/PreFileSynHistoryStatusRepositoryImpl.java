package com.copsec.monitor.web.repository.repositoryImpl;


import java.util.Objects;

import com.copsec.monitor.web.entity.PreFileSynHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class PreFileSynHistoryStatusRepositoryImpl extends BaseRepositoryImpl {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void updatePreFileSyncHisotryStatus(PreFileSynHistoryStatus preFileSynHistoryStatus) {

		/*Query query = new Query();
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where("fileName").is(preFileSynHistoryStatus.getFileName()));
		Update update = new Update();
		if(preFileSynHistoryStatus.getGatherSizeSum() > 0){

			update.set("gatherSizeSum",preFileSynHistoryStatus.getGatherSizeSum());
		}else if(preFileSynHistoryStatus.getGatherTime() > 0 ){

			update.set("gatherTime",preFileSynHistoryStatus.getGatherTime());
		}else if(preFileSynHistoryStatus.getStorageSizeSum() > 0 ){

			update.set("storageSizeSum",preFileSynHistoryStatus.getStorageSizeSum());
		}else if(preFileSynHistoryStatus.getStorageTime() > 0){

			update.set("storageTime",preFileSynHistoryStatus.getStorageTime());
		}
		update.set("updateTime",preFileSynHistoryStatus.getUpdateTime());
		query.addCriteria(criteria);
		PreFileSynHistoryStatus t = this.mongoTemplate.findOne(query,PreFileSynHistoryStatus.class);
		if(Objects.isNull(t)){


		}*/
		this.mongoTemplate.save(preFileSynHistoryStatus);
		/*this.mongoTemplate.findAndModify(query,update,PreFileSynHistoryStatus.class);*/
	}
}
