package com.copsec.monitor.web.pools;

import java.util.List;
import java.util.Optional;

import com.copsec.monitor.web.beans.remote.RemoteUriBean;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class RemoteUriPools {

	private static RemoteUriPools pool = null;

	private RemoteUriPools(){}

	private static Multimap<String,RemoteUriBean> map = ArrayListMultimap.create();

	public static synchronized RemoteUriPools getInstances(){

		if(pool == null){

			synchronized(RemoteUriPools.class){

				if(pool == null){

					pool = new RemoteUriPools();
				}
			}
		}
		return pool;
	}

	public void add(String type,RemoteUriBean bean){

		map.put(type,bean);
	}

	public Optional<RemoteUriBean> get(String type,String operateType){

		List<RemoteUriBean> lists = (List<RemoteUriBean>) map.asMap().get(type);
		Optional<RemoteUriBean> optional = lists.stream().filter(d -> {

			if(d.getUriType().equals(operateType)){
				return true;
			}
			return false;
		}).findFirst();

		return optional;
	}
}
