package com.copsec.monitor.web.snmp.statusComponent;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.snmp.executors.SnmpExecutors;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import com.google.common.collect.Maps;
import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class SnmpForDetection extends BaseSnmpComponent {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SnmpForDetection.class);

	private SnmpConfigBean snmpConfig;

	private Map<String,OidMapBean> oidMap;

	private SystemConfig systemConfig;

	public SnmpForDetection(SnmpConfigBean config ,Map<String,OidMapBean> maps,SystemConfig systemConfig){

		super(config,maps,systemConfig);
		this.systemConfig = systemConfig;
		this.snmpConfig = config;
		this.oidMap = maps;
	}

	@Override
	public void getCpuUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		String useRate = statusMap.get(SnmpFieldResources.CPU_USE_RATE);
		if(ObjectUtils.isEmpty(useRate)){

			logger.error("get cpu use rate from detection error");
			useRate = "0.00%";
		}
		useRate = useRate.replace("%","").replace("CPU","").trim();
		status.setCpuUseRate(Double.valueOf(useRate));
	}

	@Override
	public void getMemoryUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		String memoryString = statusMap.get(SnmpFieldResources.MEMORY_TOTAL);
		if(ObjectUtils.isEmpty(memoryString)){

			logger.error("get memory use rate from detection error");
			status.setMenUseRate(0.00);
			return ;
		}
		String[] memoryArray = memoryString.substring(0,memoryString.indexOf("|")).split(" ");
		HashMap<String,Double> totalMap = Maps.newHashMap();
		for(String str:memoryArray){

			if(!ObjectUtils.isEmpty(str)){

				String[] attr = str.split(":");
				if(str.startsWith("MEM->Total")){

					totalMap.put("total",Double.valueOf(attr[1]));
				}else if(str.startsWith("Free")){

					totalMap.put("free",Double.valueOf(attr[1]));
				}
			}
		}
		BigDecimal free = new BigDecimal(Double.valueOf(totalMap.get("free")));
		BigDecimal total = new BigDecimal(Double.valueOf(totalMap.get("total")));
		double precent = free.divide(total,4, BigDecimal.ROUND_HALF_DOWN).doubleValue() * 100;
		status.setMenUseRate(( 100.0 - precent ));
	}

	@Override
	public void getDetectionNetInfo(SnmpDeviceStatus status, Map<String, String> statusMap) {

		logger.debug("get detections net info values {}",statusMap.get(SnmpFieldResources.INTERFACE_TABLE));
	}

}
