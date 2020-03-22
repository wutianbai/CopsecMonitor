package com.copsec.monitor.web.pools;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.copsec.monitor.web.beans.taskMonitor.FileSyncMonitorBean;
import com.copsec.monitor.web.beans.taskMonitor.FileSyncStatusBean;
import com.copsec.monitor.web.entity.FileSyncStatus;
import com.google.common.collect.Maps;

public class FileSyncStatusPools {

	private static FileSyncStatusPools pools;
	private static Map<String,FileSyncStatusBean> map = Maps.newConcurrentMap();

	private FileSyncMonitorBean monitorConfig = new FileSyncMonitorBean();

	private FileSyncStatusPools(){}

	public static synchronized FileSyncStatusPools getInstances(){

		if(pools == null){

			synchronized (FileSyncStatusPools.class){

				if(pools == null){

					pools = new FileSyncStatusPools();
				}
			}
		}
		return pools;
	}

	public FileSyncStatusBean getStatusByTaskName(String taskName){

		Optional<Map.Entry<String,FileSyncStatusBean>> optional = map.entrySet().stream().
				filter(d -> d.getValue().getTaskName().equalsIgnoreCase(taskName)).findFirst();
		if(optional.isPresent()){

			return optional.get().getValue();
		}
		return null;
	}

	public void update(FileSyncStatusBean status){

		if(map.containsKey(status.getTaskName())){

			map.replace(status.getTaskName(),status);
		}else{

			map.put(status.getTaskName(),status);
		}
	}

	public void updateByCondition(String taskName,String type,String userId){

		Iterator<Map.Entry<String,FileSyncStatusBean>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){

			Entry<String,FileSyncStatusBean> entry = iterator.next();
			if(type.equals("all")){

				FileSyncStatusBean bean = entry.getValue();
				if(!bean.isStatus()){

					bean.setStatus(true);
					bean.setOperateUser(userId);
					bean.setOperateTime(new Date());
					update(bean);
				}
			}else{

				if(entry.getValue().getTaskName().equals(taskName)){

					FileSyncStatusBean bean = entry.getValue();
					bean.setStatus(true);
					bean.setUpdateTime(new Date());
					bean.setOperateUser(userId);
					bean.setOperateTime(new Date());
					update(bean);
				}
			}
		}

	}

	public Map<String,FileSyncStatusBean> getAll(){

		return map;
	}

	public void setConfig(FileSyncMonitorBean configBean){

		monitorConfig = configBean;
	}

	public FileSyncMonitorBean getConfig(){

		return monitorConfig;
	}

}
