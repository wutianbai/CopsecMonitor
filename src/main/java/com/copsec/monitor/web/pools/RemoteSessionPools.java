package com.copsec.monitor.web.pools;

import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.remote.RemoteSessionBean;
import jdk.nashorn.internal.runtime.options.Option;

/**
 * key deviceId ,value remoteLoginInfo
 */
public class RemoteSessionPools {

	private static RemoteSessionPools pool = null;
	private RemoteSessionPools(){}

	private ConcurrentHashMap<String,RemoteSessionBean> map = new ConcurrentHashMap<>();

	public static synchronized RemoteSessionPools getInstances(){

		if(pool == null){

			synchronized (RemoteSessionPools.class){

				if(pool == null){

					pool = new RemoteSessionPools();
				}
			}
		}
		return pool;
	}

	public void add(RemoteSessionBean session){

		map.putIfAbsent(session.getKey(),session);
	}

	public Option<RemoteSessionBean> get(String deviceId){

		return null;
	}
}
