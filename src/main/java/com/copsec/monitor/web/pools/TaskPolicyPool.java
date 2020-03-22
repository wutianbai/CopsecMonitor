package com.copsec.monitor.web.pools;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.taskMonitor.FileTaskSyncPolicyBean;
import com.google.common.collect.Lists;

import static java.util.stream.Collectors.toList;

public class TaskPolicyPool {
	private static TaskPolicyPool ourInstance = null;

	private ConcurrentHashMap<String ,FileTaskSyncPolicyBean> map = new ConcurrentHashMap<>();

	public synchronized static TaskPolicyPool getInstance() {
		if(ourInstance == null){

			synchronized (TaskPolicyPool.class){

				if(ourInstance == null){

					ourInstance = new TaskPolicyPool();
				}
			}
		}
		return ourInstance;
	}

	private TaskPolicyPool() {}

	public void update(FileTaskSyncPolicyBean bean){

		if(map.containsKey(bean.getTaskName())){

			map.replace(bean.getTaskName(),bean);
		}else{

			map.putIfAbsent(bean.getTaskName(),bean);
		}
	}

	public List<FileTaskSyncPolicyBean> getAll(){

		List<FileTaskSyncPolicyBean> list =
				map.entrySet().stream().map(Map.Entry::getValue).collect(toList());
		return list;
	}

	public void delete(String taskName){

		Iterator<Map.Entry<String,FileTaskSyncPolicyBean>> iterator = map.entrySet().iterator();
		while(iterator.hasNext()){

			Map.Entry<String,FileTaskSyncPolicyBean> entry = iterator.next();
			if(entry.getKey().equals(taskName)){

				iterator.remove();
			}
		}
	}

	public FileTaskSyncPolicyBean get(String taskName){

		Optional<Entry<String ,FileTaskSyncPolicyBean>> optional =
				map.entrySet().stream().filter(d -> d.getKey().equals(taskName)).findFirst();

		if(optional.isPresent()){

			return optional.get().getValue();
		}
		return null;
	}
}
