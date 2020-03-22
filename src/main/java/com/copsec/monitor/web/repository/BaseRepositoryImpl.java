package com.copsec.monitor.web.repository;

import com.copsec.monitor.web.beans.LogConditionBean;
import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.beans.statistics.DBStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.FileStatisticResultBean;
import com.copsec.monitor.web.beans.statistics.StatisticalResultBean;
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
    public List<CpuHistoryStatus> getCpuStatus(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public List<NetDataHistoryStatus> getNetData(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public List<ProtocolHistoryStatus> getProtocolData(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public List<DBSyncHistoryStatus> getDbData(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public List<FileSyncHistoryStatus> getFileData(List<AggregationOperation> operationList) {
        return null;
    }

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

    /**
     * 计算总量
     *
     * @param operationList
     * @return
     */
    @Override
    public StatisticalResultBean countTotal(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public List<SyslogMessage> findByIdAfter() {
        return null;
    }

    @Override
    public void updatePreFileSyncHisotryStatus(PreFileSynHistoryStatus preFileSynHistoryStatus) {

    }

    @Override
    public void updateFileSyncHistoryStatus(FileSyncHistoryStatus status) {

    }

    @Override
    public List<FileStatisticResultBean> countFileStatistic(List<AggregationOperation> operationList) {
        return null;
    }

    /**
     * find status equals false
     *
     * @return
     */
    @Override
    public List<FileSyncStatus> findFileTaskByStatus() {
        return null;
    }

    @Override
    public Page<FileSyncStatus> findFileTaskByCondition(Pageable pageable) {
        return null;
    }

    @Override
    public void updateTaskStatus(FileSyncStatus status) {

    }

    @Override
    public List<DeviceNowStatus> countDeviceStatus(ConditionBean conditionBean) {
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
    public void updateFileSyncHistoryStatusWithError(FileSyncHistoryStatus status) {

    }

    @Override
    public void updateDBSyncHistoryStatus(DBSyncHistoryStatus status) {

    }

    @Override
    public List<DBStatisticResultBean> countDatabaseStatistic(List<AggregationOperation> operationList) {
        return null;
    }

    @Override
    public void updateProtocolHistoryStatus(ProtocolHistoryStatus status) {

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
    public void insertWarningEvent(WarningEvent bean) { }

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
    public void insertWarningHistory(WarningHistory bean) { }
}
