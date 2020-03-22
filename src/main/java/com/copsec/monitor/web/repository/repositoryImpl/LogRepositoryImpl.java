package com.copsec.monitor.web.repository.repositoryImpl;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.entity.OperateLog;
import com.copsec.monitor.web.repository.BaseRepositoryImpl;
import com.copsec.monitor.web.utils.FormatUtils;
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

public class LogRepositoryImpl extends BaseRepositoryImpl {


    @Autowired
    private MongoTemplate logMongoTemplate;

    @Override
    public Page<OperateLog> findOperateLogByCondition(Pageable pageable, LogConditionBean condition) {

        Query query = new Query();
        Sort sort = new Sort(Sort.Direction.DESC, "date");
        Criteria criteria = new Criteria();
        if (!ObjectUtils.isEmpty(condition.getOperateUser())) {
            criteria.and("operateUser").is(condition.getOperateUser());
        }

        if (!ObjectUtils.isEmpty(condition.getIp())) {
            criteria.and("ip").is(condition.getIp());
        }

        if (!ObjectUtils.isEmpty(condition.getOperateType())) {
            criteria.and("operateType").is(condition.getOperateType());
        }

        if (!ObjectUtils.isEmpty(condition.getDesc())) {
            Pattern pattern = Pattern.compile("^.*" + condition.getDesc() + ".*$", Pattern.CASE_INSENSITIVE);
            criteria.and("desc").regex(pattern);
        }

        if (!ObjectUtils.isEmpty(condition.getResult())) {
            criteria.and("result").is(Integer.valueOf(condition.getResult()));
        }

        if (!ObjectUtils.isEmpty(condition.getStart()) && !ObjectUtils.isEmpty(condition.getEnd())) {
            Date start = FormatUtils.getDate(condition.getStart());
            Date end = FormatUtils.getDate(condition.getEnd());
            criteria.and("date").gte(start).lte(end);
        }
        query.addCriteria(criteria);
        query.with(sort).with(pageable);
        long total = this.logMongoTemplate.count(query, OperateLog.class);

        List<OperateLog> content = this.logMongoTemplate.find(query, OperateLog.class);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public void deleteCheckOperateLog(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        this.logMongoTemplate.remove(query, OperateLog.class);
    }

    @Override
    public void deleteAllOperateLog() {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").exists(true));
        this.logMongoTemplate.findAllAndRemove(query, OperateLog.class);
    }
}
