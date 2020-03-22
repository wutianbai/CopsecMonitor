package com.copsec.monitor.web.snmp.pools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 存放snmpDevice信息该类型的设备放入该缓存当中，
 */
public class SnmpDevicePool {

	private static final Logger logger = LoggerFactory.getLogger(SnmpDevicePool.class);

	private static SnmpDevicePool snmpDevicePool;

	private static Map<String ,SnmpConfigBean> map = Maps.newConcurrentMap();

	private SnmpDevicePool(){}

	public synchronized static SnmpDevicePool getInstance(){

		if(snmpDevicePool == null){

			synchronized (SnmpDevicePool.class){

				if(snmpDevicePool == null){

					snmpDevicePool = new SnmpDevicePool();
				}
			}
		}
		return snmpDevicePool;
	}

	public void addSnmpConfig(SnmpConfigBean config){

		logger.error("add snmp Device info {}",config);
		map.putIfAbsent(config.getDeviceId(),config);
	}

	public void updateSnmpConfig(SnmpConfigBean config){

		if(map.containsKey(config.getDeviceId())){

			map.replace(config.getDeviceId(),config);
		}else{

			map.put(config.getDeviceId(),config);
		}
	}

	public void deleteConfig(String deviceId){

		if(map.containsKey(deviceId)){

			SnmpConfigBean config  = map.get(deviceId);
			map.remove(deviceId,config);
		}
	}

	public SnmpConfigBean getConfigByDeviceId(String deviceId){

		if(map.containsKey(deviceId)){

			return map.get(deviceId);
		}
		return null;
	}

	public Map<String,SnmpConfigBean> getAll(){

		return map;
	}

	public List<SnmpConfigBean> getList(){

		ArrayList<SnmpConfigBean> list = Lists.newArrayList();
		map.entrySet().stream().forEach(entry -> {

			list.add(entry.getValue());
		});

		return list;
	}
}
