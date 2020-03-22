package com.copsec.monitor.web.flume.pools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存储配置文件中内容 flume 相关配置
 */
public class FlumeBeanPool {

	private static final Logger logger = LoggerFactory.getLogger(FlumeBeanPool.class);

	private static FlumeBeanPool pool;

	private Map<String,FlumeBean> map = new ConcurrentHashMap<>();

	private FlumeBeanPool(){}

	public synchronized static FlumeBeanPool getInstances(){

		if(pool == null){

			synchronized (FlumeBeanPool.class){

				if(pool == null){

					pool = new FlumeBeanPool();
				}
			}
		}
		return pool;
	}

	public void add(FlumeBean bean){

		if(!map.containsKey(bean.getAgentId())){

			map.putIfAbsent(bean.getAgentId(),bean);
		}
	}

	public void delete(String agentId){

		if(map.containsKey(agentId)){

			map.remove(agentId);
		}
	}

	public void update(FlumeBean flumeBean){

		if(map.containsKey(flumeBean.getAgentId())){

			map.replace(flumeBean.getAgentId(),flumeBean);
		}
	}

	public boolean exist(FlumeBean flumeBean){

		if(map.containsKey(flumeBean.getAgentId())){

			return true;
		}
		return false;
	}

	public List<FlumeBean> getAll(){

		List<FlumeBean> list = Lists.newArrayList();
		map.entrySet().stream().forEach(entry -> {

			list.add(entry.getValue());
		});
		return list;
	}

	public FlumeBean getFlumeConfig(String agentId){

		if(map.containsKey(agentId)){

			return map.get(agentId);
		}
		return null;
	}
}
