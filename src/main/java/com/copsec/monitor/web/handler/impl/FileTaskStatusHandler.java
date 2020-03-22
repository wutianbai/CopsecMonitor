package com.copsec.monitor.web.handler.impl;

import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import com.copsec.monitor.web.beans.syslogParseBeans.FileSynLogBean;
import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.entity.FileSyncHistoryStatus;
import com.copsec.monitor.web.handler.TaskHistoryStatusHandler;
import com.copsec.monitor.web.pools.FileSyncStatusPools;
import com.copsec.monitor.web.pools.TaskStatusHandlerPools;
import com.copsec.monitor.web.repository.FileStatusRepository;
import com.copsec.monitor.web.utils.FormatUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class FileTaskStatusHandler implements TaskHistoryStatusHandler {

	private static final Logger logger = LoggerFactory.getLogger(FileTaskStatusHandler.class);

	private static final List<String> instancesNames =
			Lists.newArrayList("文件同步集群模式","缓存文件上传",
					"文件入库实例","文件采集实例",
					"用交互式文件同步");

	@Autowired
	private FileStatusRepository fileStatusRepository;

	@Override
	public void handler(SyslogMessageBean syslogMessageBean) {

		if(!canSave(syslogMessageBean.getProperties(),instancesNames))
		{
			FileSynLogBean bean = getFileSynLog(syslogMessageBean);

			updateFileSyncHistory(bean);
		}
	}

	protected FileSynLogBean getFileSynLog(SyslogMessageBean syslogMessageBean){

		FileSynLogBean status = new FileSynLogBean();
		status.setDeviceId(syslogMessageBean.getDeviceId());
		status.setUpdateTime(syslogMessageBean.getUpdateTime());
		syslogMessageBean.getProperties().stream().filter(d -> {

			if(d.split("=").length == 2){

				return true;
			}
			return false;
		}).forEach(item -> {

			String[] attrs = item.split("=");
			switch(attrs[0]){

				case "taskName":{

					String taskName = attrs[1].replace("_back_storage","")
							.replace("_front_collect","");
					status.setTaskName(taskName);
					break;
				}
				case "logType":{

					status.setGather(attrs[1].equalsIgnoreCase("文件采集"));
					break;
				}
				case "objectName":{

					status.setFileName(attrs[1]);
					break;
				}
				case "desc":{

					if((attrs[1].startsWith("开始下载文件") || attrs[1].startsWith("开始上传文件")) && !attrs[1].contains("次") ){
						long size = FormatUtils.getValue(attrs[1],2,true);
						status.setFileSize(size);
						status.setStart(true);
						status.setCanSave(true);
					}else if(attrs[1].startsWith("下载文件完成") || attrs[1].startsWith("文件上传完成")){
						long time = FormatUtils.getValue(attrs[1],1,false);
						status.setTime(time);
						status.setFinish(true);
						status.setCanSave(true);
					}else if(attrs[1].startsWith("文件上传异常")){ //storage

						long size = FormatUtils.getValue(attrs[1],6,true);
						status.setFileSize(size);
						status.setCanSave(true);
						status.setError(true);

					}else if(attrs[1].startsWith("下载文件失败")){ //gather

						long size = FormatUtils.getValue(attrs[1],3,true);
						status.setFileSize(size);
						status.setCanSave(true);
						status.setError(true);

					}else{

						status.setCanSave(false);
					}
					break;
				}
			}

		});
		return status;
	}

	protected void updateFileSyncHistory(FileSynLogBean bean){


		if(ObjectUtils.isEmpty(bean) || bean.getTaskName().equals("N/A")){

			return ;
		}

		if(!bean.getTaskName().equals("N/A")){

			FileSyncStatusBean syncStatus = new FileSyncStatusBean();
			syncStatus.setTaskName(bean.getTaskName());
			syncStatus.setUpdateTime(bean.getUpdateTime());
			syncStatus.setStatus(true);
			FileSyncStatusPools.getInstances().update(syncStatus);
		}

		FileSyncHistoryStatus status = new FileSyncHistoryStatus();
		status.setDeviceId(bean.getDeviceId());
		status.setTaskName(bean.getTaskName());
		status.setUpdateTime(bean.getUpdateTime());
		if(bean.isGather()){

			if(bean.isError()){

				status.setGatherSizeSum(bean.getFileSize());
			}else if(bean.isStart()){

				status.setGatherSizeSum(bean.getFileSize());
			}else if(bean.isFinish()){

				status.setGatherCount(1);
				status.setGatherTime(bean.getTime());
			}
		}else{

			if(bean.isError()){

				status.setStorageSizeSum(bean.getFileSize());
			}else if(bean.isStart()){

				status.setStorageSizeSum(bean.getFileSize());
			}else if(bean.isFinish()){

				status.setStorageCount(1);
				status.setStorageTime(bean.getTime());
			}
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(bean.getUpdateTime());
		status.setYear(calendar.get(Calendar.YEAR));
		status.setMonth((calendar.get(Calendar.MONTH)+1));
		status.setDay(calendar.get(Calendar.DAY_OF_MONTH));
		status.setHour(calendar.get(Calendar.HOUR_OF_DAY));
		if(bean.isError()){

			fileStatusRepository.updateFileSyncHistoryStatusWithError(status);
		}else{

			fileStatusRepository.updateFileSyncHistoryStatus(status);
		}
	}

	@PostConstruct
	public void init(){

		TaskStatusHandlerPools.getInstance().registerHandler(this);
	}
}
