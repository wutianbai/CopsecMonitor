package com.copsec.monitor.web.service;

import java.util.List;
import java.util.Map;

import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.beans.statistics.DBSumResultBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.entity.DBSyncHistoryStatus;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;

public interface StatisticsService {

	/**
	 * 获取指定天数的数据
	 * @param conditionBean
	 * @return
	 */
	CopsecResult getCpuStatisticsInfoByDayTime(ConditionBean conditionBean);

	/**
	 * 获取一天以内的cpu信息
	 */

	CopsecResult getCpuStatisticsInfoBy1(ConditionBean conditionBean);

	/**
	 * 网络数据流量统计
	 * @param conditionBean
	 * @return
	 */
	CopsecResult getNetData(ConditionBean conditionBean,boolean isSum);

	/**
	 * 协议处理
	 * @param conditionBean
	 * @return
	 */
	CopsecResult getProtocol(ConditionBean conditionBean);

	/**
	 * 获取数据库统计信息
	 * @param conditionBean
	 * @return
	 */
	CopsecResult getDbData(ConditionBean conditionBean);

	/**
	 * 获取文件发送信息
	 * @param conditionBean
	 * @return
	 */
	CopsecResult getFileData(ConditionBean conditionBean);

	CopsecResult getTotal4Net(ConditionBean conditionBean);

	CopsecResult getTotal4Db(ConditionBean conditionBean);

	CopsecResult getTotal4File(ConditionBean conditionBean);

	CopsecResult getTotal4Protocol(ConditionBean conditionBean);

	CopsecResult countTotal(ConditionBean conditionBean);

	/**
	 * 计算运行天数
	 * @param conditionBean
	 * @return
	 */
	CopsecResult countDay(ConditionBean conditionBean);

	/**
	 * 计算文件发送任务总量，当天量
	 * @param conditionBean
	 * @return
	 */
	CopsecResult countFileSynHistoryStatus(ConditionBean conditionBean);

	List<FileSyncHistoryStatus> getTotalSize4Yesterday(ConditionBean conditionBean);

	FileSyncHistoryStatus getFileCountToday(ConditionBean conditionBean);

	CopsecResult getPrecentCountOfTask(ConditionBean conditionBean);

	CopsecResult getPrecentSumOfTask(ConditionBean conditionBean);

	CopsecResult getPrecentCountValues(ConditionBean conditionBean);

	CopsecResult getPrecentSumValues(ConditionBean conditionBean);

	CopsecResult getCurrentNetData(ConditionBean conditionBean);

	CopsecResult getDeviceIdsByTaskNams(List<String> taskNames);

	List<String> getAlarmFileSyncTaskNames();

	CopsecResult getResourcesUse(boolean isCpu);


	/**
	 * 数据库同步任务详细信息，运行时间，总量等
	 * @return
	 */
	CopsecResult getDBSyncTaskDetails(ConditionBean conditionBean);

	/**
	 * 数据库任务当日统计信息
	 * @param conditionBean
	 * @return
	 */
	List<DBSumResultBean> getDBDataForToday(ConditionBean conditionBean);

	/**
	 * 数据库任务统计总量 gather Storage
	 * @param conditionBean
	 * @return
	 */
	DBSyncHistoryStatus getDBDataTotalForToday(ConditionBean conditionBean);

	/**
	 * 获取数据库任务统计饼状图数据
	 * @return
	 */
	CopsecResult getDBTaskPieDateForToday(ConditionBean conditionBean);
}
