package com.copsec.monitor.web.flume.pools;

import java.util.List;

import com.copsec.monitor.web.beans.flume.AliasMapBean;
import com.google.common.collect.Lists;

public class FlumeAliasPool {

	private static FlumeAliasPool pool ;
	private static List<AliasMapBean> list = Lists.newArrayList();

	private FlumeAliasPool(){}

	public synchronized static FlumeAliasPool getInstances(){

		if(pool == null){

			synchronized (FlumeAliasPool.class){

				if(pool == null){

					pool = new FlumeAliasPool();
				}
			}
		}
		return pool;
	}

	public void add(AliasMapBean map){

		if(!list.contains(map)){
			list.add(map);
		}
	}

	public String get(String type,String alias){

		for(AliasMapBean item:list){

			if(item.getAlias().equalsIgnoreCase(alias) && item.getType().equalsIgnoreCase(type)){

				return item.getFileName();
			}
		}
		return "";
	}
}
