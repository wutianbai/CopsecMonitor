package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.TaskPolicyPool;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class TaskPolicyReader extends  BaseFileReader<FileTaskSyncPolicyBean>{

	@Override
	public void getData(String filePath) throws CopsecException {
		List<String> list = super.readContent(filePath);
		if(!ObjectUtils.isEmpty(list)){

			list.stream().filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 7)
					.forEach(item -> {

						String[] attrs = item.split(Resources.SPLITER);
						FileTaskSyncPolicyBean bean = new FileTaskSyncPolicyBean();
						bean.setTaskName(attrs[0]);
						bean.setTotal(Integer.valueOf(attrs[1]));
						bean.setUnit(attrs[2]);
						bean.setInterval(Integer.valueOf(attrs[3]));
						bean.setIntervalUnit(attrs[4]);
						bean.setTotalEnable(attrs[5]);
						bean.setIntervalEnable(attrs[6]);
						TaskPolicyPool.getInstance().update(bean);
					});
		}
	}

}
