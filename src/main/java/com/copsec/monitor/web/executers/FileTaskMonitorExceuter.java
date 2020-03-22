package com.copsec.monitor.web.executers;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.copsec.monitor.web.beans.statistics.ConditionBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.entity.FileSyncStatus;
import com.copsec.monitor.web.pools.FileSyncStatusPools;
import com.copsec.monitor.web.pools.TaskPolicyPool;
import com.copsec.monitor.web.repository.FileSyncStatusRepository;
import com.copsec.monitor.web.service.StatisticsService;
import com.copsec.monitor.web.utils.FormatUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class FileTaskMonitorExceuter {

	private static final Logger logger = LoggerFactory.getLogger(FileTaskMonitorExceuter.class);

	@Autowired
	private FileSyncStatusRepository fileSyncStatusRepository;

	@Autowired
	private StatisticsService statisticsService;

	/**
	 * 实时监测任务状态
	 */
	@Scheduled(fixedRate = 5000)
	public void monitor(){


		Map<String,FileSyncStatusBean> tasks = FileSyncStatusPools.getInstances().getAll();
		tasks.entrySet().stream().forEach(entry -> {

			FileTaskSyncPolicyBean policyBean = TaskPolicyPool.getInstance().get(entry.getValue().getTaskName());

			if(!ObjectUtils.isEmpty(policyBean)){

				if(policyBean.getIntervalEnable().equals("yes")){

					long unitTime = 0L;
					if(policyBean.getIntervalUnit().equals("m")){

						unitTime = policyBean.getInterval()* 60*1000;
					}else if(policyBean.getIntervalUnit().equals("h")){

						unitTime = policyBean.getInterval()* 60 * 60 * 1000;
					}
					Date now = Calendar.getInstance().getTime();
					FileSyncStatusBean statusBean = entry.getValue();
					long time = now.getTime() - statusBean.getUpdateTime().getTime();
					if(time >= unitTime ){

						if(statusBean.isStatus()) {

							FileSyncStatus taskStatus = new FileSyncStatus();
							taskStatus.setTaskName(statusBean.getTaskName());
							taskStatus.setUpdateTime(statusBean.getUpdateTime());
							taskStatus.setStatus(false);
							taskStatus.setMessage("任务超过" + FormatUtils.getFormatTimeMillis(time) + "未上报数据");
							taskStatus.setOperateUser("N/A");
							fileSyncStatusRepository.save(taskStatus);
							statusBean.setStatus(false);
							FileSyncStatusPools.getInstances().update(statusBean);
						}else{

							statusBean.setStatus(false);
							FileSyncStatusPools.getInstances().update(statusBean);
						}
					}
				}
			}
		});
	}

	/**
	 * 每天凌晨一点执行一次，获取前一天文件同步任务总量，对比同步任务策略，执行
	 */
	@Scheduled(cron = "0 0 1 * * ?")
	public void getFileTaskTotalSizeForYesterday(){

		final String yesterday = FormatUtils.getDateBefore("yyyy-MM-dd",Calendar.DAY_OF_YEAR,-1);
		ConditionBean conditionBean = new ConditionBean();
		conditionBean.setStart(yesterday);
		conditionBean.setEnd(yesterday);

		List<FileTaskSyncPolicyBean> policyBeans  = TaskPolicyPool.getInstance().getAll();

		List<String> taskIds = Lists.newArrayList();

		List<String> _taskIds = Lists.newArrayList();
		policyBeans.stream().forEach(policy -> {
			taskIds.add(policy.getTaskName());
			_taskIds.add(policy.getTaskName());
		});

		if(taskIds.size() == 0 ){

			logger.warn("{} find no file sync task policy specified",Thread.currentThread().getName());
			return ;
		}

		conditionBean.setDeviceIds(taskIds);
		List<FileSyncHistoryStatus> fileTasks = statisticsService.getTotalSize4Yesterday(conditionBean);

		fileTasks.stream().forEach(task -> {

			_taskIds.remove(task.getTaskName());
			FileTaskSyncPolicyBean policy = TaskPolicyPool.getInstance().get(task.getTaskName());
			if(!ObjectUtils.isEmpty(policy)){

				if(policy.getTotalEnable().equals("yes")){

					BigDecimal total = new BigDecimal(task.getGatherSizeSum() + task.getStorageSizeSum());
					BigDecimal policyValue = new BigDecimal(0L);
					if(policy.getUnit().equals("M")){

						policyValue = new BigDecimal(policy.getTotal()).multiply(new BigDecimal(1024 * 1024));
					}else if(policy.getUnit().equals("G")){

						policyValue = new BigDecimal(policy.getTotal())
								.multiply(new BigDecimal(1024)).multiply(new BigDecimal(1024)).multiply(new BigDecimal(1024));
					}
					/**
					 * 触发告警信息
					 */
					if(total.compareTo(policyValue) < 0){

						FileSyncStatus taskStatus = new FileSyncStatus();
						taskStatus.setTaskName(task.getTaskName());
						taskStatus.setUpdateTime(new Date());
						taskStatus.setStatus(false);
						taskStatus.setMessage("任务[" +task.getTaskName()+ "]["+yesterday+"]日数据总量["+
								FormatUtils.getFormatSizeByteForCeil(total.longValue())+"]小于预警值["+policy.getTotal() + policy.getUnit()+"]");
						taskStatus.setOperateUser("N/A");
						fileSyncStatusRepository.save(taskStatus);
					}
				}
			}
		});

		if(_taskIds.size() > 0){

			_taskIds.stream().forEach( taskName -> {

				FileSyncStatus taskStatus = new FileSyncStatus();
				taskStatus.setTaskName(taskName);
				taskStatus.setUpdateTime(new Date());
				taskStatus.setStatus(false);
				taskStatus.setMessage("任务[" +taskName+ "]["+yesterday+"]日数据总量["+
						FormatUtils.getFormatSizeByteForCeil(0L)+"]请检查任务是否正常，如正常请忽略");
				taskStatus.setOperateUser("N/A");
				fileSyncStatusRepository.save(taskStatus);
			});
		}

		if(logger.isDebugEnabled()){

			logger.debug("count file task total size for yestoday {}",yesterday);
		}
	}
}
