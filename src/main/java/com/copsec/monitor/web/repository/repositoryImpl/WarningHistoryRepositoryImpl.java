package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.entity.WarningHistory;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import com.copsec.monitor.web.utils.FormatUtils;
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
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class WarningHistoryRepositoryImpl extends BaseRepositoryImpl {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<WarningHistory> findWarningHistoryByCondition(Pageable pageable, WarningHistoryBean condition) {
        Query query = new Query();
        Sort sort = new Sort(Direction.DESC, "dealTime");
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

        if (!ObjectUtils.isEmpty(condition.getDStart()) && !ObjectUtils.isEmpty(condition.getDEnd())) {
            Date start = FormatUtils.getDate(condition.getDStart());
            Date end = FormatUtils.getDate(condition.getDEnd());
            criteria.and("dealTime").gte(start).lte(end);
        }

        if (!ObjectUtils.isEmpty(condition.getStatus())) {
            criteria.and("status").is(Integer.valueOf(condition.getStatus()));
        }

        query.addCriteria(criteria);
        query.with(sort).with(pageable);
        long total = this.mongoTemplate.count(query, WarningHistory.class);

        List<WarningHistory> content = this.mongoTemplate.find(query, WarningHistory.class);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void handleWarningHistory(WarningHistory bean) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("_id").is(bean.getId()));
        Update update = new Update();
        update.set("status", 1);
        update.set("userId", bean.getUserId());
        update.set("userName", bean.getUserName());
        update.set("dealTime", bean.getDealTime());
        query.addCriteria(criteria);
        this.mongoTemplate.findAndModify(query, update, WarningHistory.class);
    }

    @Override
    public void insertWarningHistory(WarningHistory bean) {
        this.mongoTemplate.insert(bean);
    }
}
