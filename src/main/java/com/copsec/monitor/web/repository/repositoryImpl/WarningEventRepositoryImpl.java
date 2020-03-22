package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import com.copsec.monitor.web.utils.FormatUtils;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class WarningEventRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<WarningEvent> findWarningEvent() {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("status").is(1));
        query.addCriteria(criteria).limit(100);

        return this.mongoTemplate.find(query, WarningEvent.class);
    }

    @Override
    public Page<WarningEvent> findWarningEventByCondition(Pageable pageable, WarningEventBean condition) {
        Query query = createCondition(condition);
        query.with(pageable);
        long total = this.mongoTemplate.count(query, WarningEvent.class);

        List<WarningEvent> content = this.mongoTemplate.find(query, WarningEvent.class);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<WarningEvent> findWarningEventByCondition(WarningEventBean condition) {
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC, "eventTime");
        Criteria criteria = new Criteria();

        if (!ObjectUtils.isEmpty(condition.getEnd())) {
            Date end = FormatUtils.getDate(condition.getEnd());
            criteria.and("eventTime").lte(end);
        }
        query.addCriteria(criteria);
        query.with(sort);
        return this.mongoTemplate.find(query, WarningEvent.class);
    }

    private Query createCondition(WarningEventBean condition) {
        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC, "eventTime");
        Criteria criteria = new Criteria();
        if (!ObjectUtils.isEmpty(condition.getEventSource())) {
            criteria.and("eventSource").is(MonitorItemEnum.getValueByName(condition.getEventSource()));
        }

        if (!ObjectUtils.isEmpty(condition.getEventType())) {
            criteria.and("eventType").is(WarningLevel.getValueByName(condition.getEventType()));
        }

        if (!ObjectUtils.isEmpty(condition.getEventDetail())) {
            Pattern pattern = Pattern.compile("^.*" + condition.getEventDetail() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("eventDetail").regex(pattern);
        }

        if (!ObjectUtils.isEmpty(condition.getDeviceName())) {
            criteria.and("deviceName").is(condition.getDeviceName());
        }

        if (!ObjectUtils.isEmpty(condition.getUserName())) {
            criteria.and("userName").is(condition.getUserName());
        }

        if (!ObjectUtils.isEmpty(condition.getStart()) && !ObjectUtils.isEmpty(condition.getEnd())) {
            Date start = FormatUtils.getDate(condition.getStart());
            Date end = FormatUtils.getDate(condition.getEnd());
            criteria.and("eventTime").gte(start).lte(end);
        }
        query.addCriteria(criteria);
        query.with(sort);

        return query;
    }

    @Override
    public void insertWarningEvent(WarningEvent bean) {
        this.mongoTemplate.insert(bean);
    }

    @Override
    public boolean deleteWarningEvent(WarningEvent bean) {
        Query query=new Query(Criteria.where("id").is(bean.getId()));
        DeleteResult deleteResult = this.mongoTemplate.remove(query, WarningEvent.class);
        return deleteResult.wasAcknowledged();
    }
}
