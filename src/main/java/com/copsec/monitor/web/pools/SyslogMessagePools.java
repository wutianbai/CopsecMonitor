package com.copsec.monitor.web.pools;

import java.util.concurrent.LinkedBlockingQueue;

import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;

public class SyslogMessagePools {
	private static SyslogMessagePools ourInstance = null;

	private LinkedBlockingQueue<SyslogMessageBean> queue = new LinkedBlockingQueue<>();

	private SyslogMessagePools() {}
	public static synchronized SyslogMessagePools getInstance() {

		if(ourInstance == null){

			synchronized (SyslogMessagePools.class){

				if(ourInstance == null){

					ourInstance = new SyslogMessagePools();
				}
			}
		}
		return ourInstance;
	}

	public void add(SyslogMessageBean bean){

		if(!queue.contains(bean)){

			queue.add(bean);
		}
	}

	public SyslogMessageBean get(){

		return queue.poll();
	}

	public boolean isEmpty(){

		return queue.isEmpty();
	}


}
