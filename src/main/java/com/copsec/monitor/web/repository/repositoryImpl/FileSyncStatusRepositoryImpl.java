package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.entity.FileSyncStatus;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class FileSyncStatusRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * find status equals false
     *
     * @return
     */
    @Override
    public List<FileSyncStatus> findFileTaskByStatus() {

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("status").is(false));
        query.addCriteria(criteria).limit(100);

        List<FileSyncStatus> list = this.mongoTemplate.find(query, FileSyncStatus.class);
        return list;
    }

    @Override
    public Page<FileSyncStatus> findFileTaskByCondition(Pageable pageable) {

        Query query = new Query();
        Sort sort = new Sort(Direction.DESC, "updateTime");
        query.with(sort).with(pageable);
        long total = this.mongoTemplate.count(query, FileSyncStatus.class);

        List<FileSyncStatus> content = this.mongoTemplate.find(query, FileSyncStatus.class);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<String> getAlarmFileSyncTask() {

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("taskName").exists(true)).and("status").is(false);
        query.addCriteria(criteria);
        return this.mongoTemplate.findDistinct(query, "taskName", "fileSyncStatus", String.class);
    }

    @Override
    public void updateTaskStatus(FileSyncStatus status) {

        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("_id").is(status.getId()));
        Update update = new Update();
        update.set("status", true);
        update.set("operateUser", status.getOperateUser());
        update.set("operateTime", status.getOperateTime());
        query.addCriteria(criteria);
        this.mongoTemplate.findAndModify(query, update, FileSyncStatus.class);
    }
}
