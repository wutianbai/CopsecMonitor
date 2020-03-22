package com.copsec.monitor.web.flume.pools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.beans.flume.FlumeAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存放agent配置
 */
public class FlumeAgentPool {

	private static final Logger logger = LoggerFactory.getLogger(FlumeAgentPool.class);

	private static FlumeAgentPool pool ;
	private FlumeAgentPool(){}

	private static Map<String,FlumeAgent>  map = new ConcurrentHashMap<>();

	public synchronized static FlumeAgentPool getInstances(){

		if(pool == null){

			synchronized (FlumeAgent.class){

				if(pool == null){

					pool = new FlumeAgentPool();
				}
			}
		}
		return pool;
	}


	public void add(FlumeAgent agent){

		map.putIfAbsent(agent.getAgentId(),agent);
	}

	public FlumeAgent get(String agentId){

		if(map.containsKey(agentId)){

			return map.get(agentId);
		}
		return null;
	}

	public void update(FlumeAgent agent){

		if(map.containsKey(agent.getAgentId())){

			map.replace(agent.getAgentId(),agent);
		}
	}

	public boolean isExist(String agentId){

		if(map.containsKey(agentId)){

			return true;
		}
		return false;
	}

	public void deleteFlumeAgent(String agentId){

		if(map.containsKey(agentId)){

			map.remove(agentId);
		}
	}

}
