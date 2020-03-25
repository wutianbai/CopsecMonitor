package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.entity.*;
import com.copsec.monitor.web.repository.baseRepository.BaseRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * repository 基类
 */
public class BaseRepositoryImpl implements BaseRepositoryCustom {

    @Override
    public Page<OperateLog> findOperateLogByCondition(Pageable pageable, LogConditionBean condition) {
        return null;
    }

    @Override
    public void deleteCheckOperateLog(List<String> ids) {
    }

    @Override
    public void deleteAllOperateLog() {
    }

    @Override
    public Page<AuditSyslogMessage> getServerMessage(Query query, Pageable pageable) {
        return null;
    }


    @Override
    public List<SyslogMessage> findByIdAfter() {
        return null;
    }


    @Override
    public void deleteDeviceMessage(SyslogMessage deviceMessage) {

    }

    @Override
    public List<String> getAllFileTaskName() {
        return null;
    }

    @Override
    public List<String> getDeviceIdsByTaskNames(List<String> taskNames) {
        return null;
    }

    @Override
    public List<String> getAlarmFileSyncTask() {
        return null;
    }

    @Override
    public List<WarningEvent> findWarningEvent() {
        return null;
    }

    @Override
    public Page<WarningEvent> findWarningEventByCondition(Pageable pageable, WarningEventBean condition) {
        return null;
    }

    @Override
    public List<WarningEvent> findWarningEventByCondition(WarningEventBean condition) {
        return null;
    }

    @Override
    public void insertWarningEvent(WarningEvent bean) {
    }

    @Override
    public boolean checkIsWarningByTime(String id) {
        return true;
    }

    @Override
    public boolean deleteWarningEvent(WarningEvent bean) {
        return true;
    }

    @Override
    public Page<WarningHistory> findWarningHistoryByCondition(Pageable pageable, WarningHistoryBean condition) {
        return null;
    }

    @Override
    public void handleWarningHistory(WarningHistory status) {
    }

    @Override
    public void insertWarningHistory(WarningHistory bean) {
    }
}
