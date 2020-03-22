package com.copsec.monitor.web.pools;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.copsec.monitor.web.beans.remote.RemoteDeviceBean;

public class RemoteDevicePools {

	private static  RemoteDevicePools pool = null;

	private RemoteDevicePools(){}

	private static ConcurrentHashMap<String,RemoteDeviceBean> map = new ConcurrentHashMap<>();

	public static synchronized RemoteDevicePools getInstances(){

		if(pool == null){

			synchronized(RemoteDevicePools.class){

				if(pool == null){

					pool = new RemoteDevicePools();
				}
			}
		}
		return pool;
	}

	public void update(RemoteDeviceBean device){

		if(map.containsKey(device.getDeviceId())){

			map.replace(device.getDeviceId(),device);
		}else{

			map.put(device.getDeviceId(),device);
		}
	}

	public void remove(String key){

		map.remove(key);
	}

	public List<RemoteDeviceBean> getAll(){

		List<RemoteDeviceBean> list =
		map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
		return list;
	}

	public Optional<RemoteDeviceBean> getDeviceById(String deviceId){

		return Optional.ofNullable(map.get(deviceId));
	}
}
