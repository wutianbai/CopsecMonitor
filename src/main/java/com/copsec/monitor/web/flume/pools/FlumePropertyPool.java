package com.copsec.monitor.web.flume.pools;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.flume.FlumeProperty;
import com.google.common.collect.Lists;

/**
 * 存储flume property 属性
 */
public class FlumePropertyPool {

	private static FlumePropertyPool pool;

	private FlumePropertyPool(){}

	private Map<String ,List<FlumeProperty>> map = new ConcurrentHashMap<>();

	public synchronized static FlumePropertyPool getInstances(){

		if(pool == null){

			synchronized (FlumePropertyPool.class){

				if(pool == null){

					pool = new FlumePropertyPool();
				}
			}
		}
		return pool;
	}

	public void add(String propsId,List<FlumeProperty> props){

		map.putIfAbsent(propsId,props);
	}

	public List<FlumeProperty> get(String propsId){

		return map.get(propsId);
	}

	public Map<String,List<FlumeProperty>> getAll(){

		return map;
	}

	/**
	 *
	 * @param prefix may be one of sources ,sink, channel
	 * @return
	 */
	public List<String> getTypeList(final String prefix){

		Set<String> keys = map.keySet();
		List<String> keyList = Lists.newArrayList();
		keys.stream().forEach(key -> {

			if(key.startsWith(prefix)){

				keyList.add(key);
			}
		});
		return keyList;
	}
}
