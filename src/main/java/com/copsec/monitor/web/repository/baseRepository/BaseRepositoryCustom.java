package com.copsec.monitor.web.repository.baseRepository;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * 用户定义复杂查询
 */
public interface BaseRepositoryCustom {

    Page<OperateLog> findOperateLogByCondition(Pageable pageable, LogConditionBean condition);

    void deleteCheckOperateLog(List<String> ids);

    void deleteAllOperateLog();

    Page<AuditSyslogMessage> getServerMessage(Query query, Pageable pageable);

    Page<WarningEvent> findWarningEventByCondition(Pageable pageable, WarningEventBean condition);

    List<WarningEvent> findWarningEventByCondition(WarningEventBean condition);

    void insertWarningEvent(WarningEvent bean);

    boolean checkIsWarningByTime(String id);

    boolean deleteWarningEvent(WarningEvent bean);

    boolean handleDeviceOutTimeWarning(String deviceId);

    Page<WarningHistory> findWarningHistoryByCondition(Pageable pageable, WarningHistoryBean condition);

    void handleWarningHistory(WarningHistory status);

    void insertWarningHistory(WarningHistory bean);
}
