package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.beans.statistics.FileStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import com.google.common.collect.Lists;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.ObjectUtils;

import java.util.Calendar;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class FileStatusRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<FileSyncHistoryStatus> getFileData(List<AggregationOperation> operationList) {

        Aggregation agg = Aggregation.newAggregation(operationList);

        AggregationResults<FileSyncHistoryStatus> result = mongoTemplate.aggregate(agg,
                "fileSyncHistoryStatus", FileSyncHistoryStatus.class);
        return result.getMappedResults();
    }

    /**
     * 计算总量
     *
     * @param operationList
     * @return
     */
    @Override
    public StatisticalResultBean countTotal(List<AggregationOperation> operationList) {


        Aggregation agg = Aggregation.newAggregation(operationList);

        AggregationResults<StatisticalResultBean> result = mongoTemplate.aggregate(agg,
                "fileSyncHistoryStatus", StatisticalResultBean.class);

        return result.getUniqueMappedResult();
    }

    @Override
    public void updateFileSyncHistoryStatus(FileSyncHistoryStatus status) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("deviceId").is(status.getDeviceId())
                .and("taskName").is(status.getTaskName())
                .and("year").is(status.getYear())
                .and("month").is(status.getMonth())
                .and("day").is(status.getDay())
                .and("hour").is(status.getHour()));
        query.addCriteria(criteria);
        FileSyncHistoryStatus preStatus = this.mongoTemplate.findOne(query, FileSyncHistoryStatus.class);
        if (ObjectUtils.isEmpty(preStatus)) {

            this.mongoTemplate.save(status);
            return;
        }
        Update update = new Update();
        update.set("gatherCount", preStatus.getGatherCount() + status.getGatherCount());
        update.set("gatherSizeSum", preStatus.getGatherSizeSum() + status.getGatherSizeSum());
        update.set("gatherTime", preStatus.getGatherTime() + status.getGatherTime());
        update.set("storageCount", preStatus.getStorageCount() + status.getStorageCount());
        update.set("storageSizeSum", preStatus.getStorageSizeSum() + status.getStorageSizeSum());
        update.set("storageTime", preStatus.getStorageTime() + status.getStorageTime());
        update.set("updateTime", status.getUpdateTime());
        this.mongoTemplate.updateFirst(query, update, FileSyncHistoryStatus.class);
    }

    @Override
    public void updateFileSyncHistoryStatusWithError(FileSyncHistoryStatus status) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(status.getUpdateTime());
        criteria.andOperator(Criteria.where("deviceId").is(status.getDeviceId())
                .and("taskName").is(status.getTaskName())
                .and("year").is(calendar.get(Calendar.YEAR))
                .and("month").is((calendar.get(Calendar.MONTH) + 1))
                .and("day").is(calendar.get(Calendar.DAY_OF_MONTH))
                .and("hour").is(calendar.get(Calendar.HOUR_OF_DAY)));
        query.addCriteria(criteria);
        FileSyncHistoryStatus preStatus = this.mongoTemplate.findOne(query, FileSyncHistoryStatus.class);
        if (ObjectUtils.isEmpty(preStatus)) { //当前时间内不存在记录减一个小时向前

            query = new Query();
            calendar.roll(Calendar.HOUR_OF_DAY, false);
            Criteria criteria1Before = new Criteria();
            criteria1Before.andOperator(Criteria.where("deviceId").is(status.getDeviceId())
                    .and("taskName").is(status.getTaskName())
                    .and("year").is(calendar.get(Calendar.YEAR))
                    .and("month").is((calendar.get(Calendar.MONTH) + 1))
                    .and("day").is(calendar.get(Calendar.DAY_OF_MONTH))
                    .and("hour").is(calendar.get(Calendar.HOUR_OF_DAY)));
            query.addCriteria(criteria1Before);
            preStatus = this.mongoTemplate.findOne(query, FileSyncHistoryStatus.class);
            if (ObjectUtils.isEmpty(preStatus)) {

                return;
            }
        }
        Update update = new Update();
        update.set("gatherSizeSum", preStatus.getGatherSizeSum() - status.getGatherSizeSum());
        update.set("storageSizeSum", preStatus.getStorageSizeSum() - status.getStorageSizeSum());
        update.set("updateTime", status.getUpdateTime());
        this.mongoTemplate.updateFirst(query, update, FileSyncHistoryStatus.class);

    }

    @Override
    public List<FileStatisticResultBean> countFileStatistic(List<AggregationOperation> operationList) {

        Aggregation aggregation = Aggregation.newAggregation(operationList);

        AggregationResults<FileStatisticResultBean> result = this.mongoTemplate.aggregate(aggregation,
                "fileSyncHistoryStatus", FileStatisticResultBean.class);

        return result.getMappedResults();
    }

    @Override
    public List<String> getAllFileTaskName() {

        List<AggregationOperation> list = Lists.newArrayList();
        list.add(project("taskName"));
        list.add(group("taskName"));
        Aggregation aggregation = Aggregation.newAggregation(list);
        AggregationResults<Document> result = this.mongoTemplate.aggregate(aggregation, "fileSyncHistoryStatus", Document.class);

        List<String> taskNames = Lists.newArrayList();
        result.getMappedResults().stream().forEach(document -> {

            taskNames.add(document.get("_id").toString());
        });
        return taskNames;
    }

    @Override
    public List<String> getDeviceIdsByTaskNames(List<String> taskNames) {

        List<AggregationOperation> list = Lists.newArrayList();
        list.add(project("taskName", "deviceId"));
        list.add(match(Criteria.where("taskName").in(taskNames)));
        list.add(group("taskName", "deviceId"));
        Aggregation aggregation = Aggregation.newAggregation(list);
        AggregationResults<Document> result = this.mongoTemplate.aggregate(aggregation, "fileSyncHistoryStatus", Document.class);

        List<String> deviceIds = Lists.newArrayList();
        result.getMappedResults().stream().forEach(document -> {

            String id = document.get("deviceId").toString();
            if (!deviceIds.contains(id)) {

                deviceIds.add(id);
            }
        });
        return deviceIds;
    }
}
