package com.copsec.monitor.web.repository.baseRepository;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.beans.statistics.DBStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.FileStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;


/**
 * 用户定义复杂查询
 */
public interface BaseRepositoryCustom {

    List<CpuHistoryStatus> getCpuStatus(List<AggregationOperation> operationList);

    List<NetDataHistoryStatus> getNetData(List<AggregationOperation> operationList);

    List<ProtocolHistoryStatus> getProtocolData(List<AggregationOperation> operationList);

    List<DBSyncHistoryStatus> getDbData(List<AggregationOperation> operationList);

    List<FileSyncHistoryStatus> getFileData(List<AggregationOperation> operationList);

    Page<OperateLog> findOperateLogByCondition(Pageable pageable, LogConditionBean condition);

    void deleteCheckOperateLog(List<String> ids);

    void deleteAllOperateLog();

    Page<AuditSyslogMessage> getServerMessage(Query query, Pageable pageable);

    List<SyslogMessage> findByIdAfter();

    void updatePreFileSyncHisotryStatus(PreFileSynHistoryStatus preFileSynHistoryStatus);

    /**
     * 计算总量
     *
     * @param operationList
     * @return
     */
    StatisticalResultBean countTotal(List<AggregationOperation> operationList);

    void updateFileSyncHistoryStatus(FileSyncHistoryStatus status);

    /**
     * 发生错误，应该减去对应的值
     *
     * @param status
     */
    void updateFileSyncHistoryStatusWithError(FileSyncHistoryStatus status);

    /**
     * 统计文件相关数据
     *
     * @return
     */
    List<FileStatisticResultBean> countFileStatistic(List<AggregationOperation> operationList);

    List<DBStatisticResultBean> countDatabaseStatistic(List<AggregationOperation> operationList);

    /**
     * find status equals false
     *
     * @return
     */
    List<FileSyncStatus> findFileTaskByStatus();

    Page<FileSyncStatus> findFileTaskByCondition(Pageable pageable);


    void updateTaskStatus(FileSyncStatus status);

    List<DeviceNowStatus> countDeviceStatus(ConditionBean conditionBean);

    void deleteDeviceMessage(SyslogMessage deviceMessage);

    List<String> getAllFileTaskName();

    List<String> getDeviceIdsByTaskNames(List<String> taskNames);

    List<String> getAlarmFileSyncTask();

    void updateDBSyncHistoryStatus(DBSyncHistoryStatus status);

    void updateProtocolHistoryStatus(ProtocolHistoryStatus status);

    /**
     * 告警事件
     *
     * @return
     */
    List<WarningEvent> findWarningEvent();

    Page<WarningEvent> findWarningEventByCondition(Pageable pageable, WarningEventBean condition);

    List<WarningEvent> findWarningEventByCondition(WarningEventBean condition);

    void insertWarningEvent(WarningEvent bean);

    boolean deleteWarningEvent(WarningEvent bean);


    Page<WarningHistory> findWarningHistoryByCondition(Pageable pageable, WarningHistoryBean condition);

    void handleWarningHistory(WarningHistory status);

    void insertWarningHistory(WarningHistory bean);
}
