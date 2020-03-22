package com.copsec.monitor.web.pools;

import java.util.List;

import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;
import com.copsec.monitor.web.handler.TaskHistoryStatusHandler;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskStatusHandlerPools {

	private static final Logger logger = LoggerFactory.getLogger(TaskStatusHandlerPools.class);

	private static TaskStatusHandlerPools ourInstance = null;

	private List<TaskHistoryStatusHandler> handlerList = Lists.newArrayList();

	public static synchronized TaskStatusHandlerPools getInstance() {

		if(ourInstance == null){

			synchronized(TaskStatusHandlerPools.class){

				if(ourInstance == null){

					ourInstance = new TaskStatusHandlerPools();
				}
			}
		}
		return ourInstance;
	}

	private TaskStatusHandlerPools() {}

	public void registerHandler(TaskHistoryStatusHandler handler){

		if(!handlerList.contains(handler)){

			handlerList.add(handler);
			if(logger.isDebugEnabled()){

				logger.debug("register handler with {} success",handler.getClass().getName());
			}
		}
	}

	public void handleSyslogMessage(final SyslogMessageBean syslogMessageBean){

		handlerList.parallelStream().forEach( handler -> {

			handler.handler(syslogMessageBean);
		});
	}

	public boolean isEmpty(){

		return handlerList.isEmpty();
	}

}
