package com.copsec.monitor.web.pools;

import java.util.Date;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.google.common.collect.Maps;

public class SnmpDeviceStatusPools {

	private static SnmpDeviceStatusPools pool ;
	private static Map<String ,SnmpDeviceStatus> map = Maps.newConcurrentMap();

	private SnmpDeviceStatusPools(){}

	public synchronized static SnmpDeviceStatusPools getInstances(){

		if(pool == null){

			synchronized (SnmpDeviceStatusPools.class){

				if(pool == null){

					pool = new SnmpDeviceStatusPools();
				}
			}
		}
		return pool;
	}

	public void update(SnmpDeviceStatus status){

		if(map.containsKey(status.getDeviceId())){

			map.replace(status.getDeviceId(),status);
		}else{

			map.putIfAbsent(status.getDeviceId(),status);
		}
	}

	public SnmpDeviceStatus get(String deviceId){

		return map.get(deviceId);
	}

	public String getStatusReport(SnmpDeviceStatus status){

		StringBuffer sb = new StringBuffer();
		sb.append(status.getDeviceId() + "\r\n\r\n");
		sb.append("cpuMemNet"+ "\r\n\r\n");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("cpuMemNet",status);
		sb.append(jsonObject.toJSONString());
		return sb.toString();
	}
}
