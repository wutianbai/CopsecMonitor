package com.copsec.monitor.web.pools;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.entity.WarningEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WarningEventPools {

	public static WarningEventPools pools = new WarningEventPools();
	public static ConcurrentHashMap<String,WarningEvent> eventMaps = new ConcurrentHashMap<>();
	private WarningEventPools(){}

	public static WarningEventPools getInstances(){

		return pools;
	}

	public void add(WarningEvent event){

		String id= event.getDeviceId()+ Resources.SPLITER + event.getEventSource() + Resources.SPLITER + event.getEventDetail();
		if(!eventMaps.containsKey(id)){

			eventMaps.put(id,event);
			if(log.isDebugEnabled()){

				log.debug("add warning event to cache -> {}",event);
			}
		}
	}

	public boolean exists(WarningEvent event){

		String id= event.getDeviceId()+ Resources.SPLITER + event.getEventSource() + Resources.SPLITER + event.getEventDetail();
		return eventMaps.containsKey(id);
	}


	public Map<String,WarningEvent> getAll(){

		return eventMaps;
	}

	public void remove(WarningEvent event){
		String id= event.getDeviceId()+ Resources.SPLITER + event.getEventSource() + Resources.SPLITER + event.getEventDetail();
		eventMaps.remove(id);
		if(log.isDebugEnabled()){

			log.debug("remove warning event from cache success -> {}",id);
		}
	}

	public void clean(){

		eventMaps.clear();
		if(log.isDebugEnabled()){

			log.debug("clean warningEvent cache success");
		}
	}
}
