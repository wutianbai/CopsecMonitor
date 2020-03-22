package com.copsec.monitor.web.snmp.statusComponent;

import java.util.Map;

import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class SnmpForNetworkAudit extends BaseSnmpComponent{

	private static final Logger logger = LoggerFactory.getLogger(SnmpForNetworkAudit.class);
	private SnmpConfigBean snmpConfig;

	private Map<String,OidMapBean> oidMap;

	private SystemConfig systemConfig;

	public SnmpForNetworkAudit(SnmpConfigBean config ,Map<String,OidMapBean> maps,SystemConfig systemConfig){

		super(config,maps,systemConfig);
		this.systemConfig = systemConfig;
		this.snmpConfig = config;
		this.oidMap = maps;
	}

	@Override
	public void getCpuUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		String cpuUseRate = statusMap.get(SnmpFieldResources.CPU_USE_RATE);
		logger.debug("networkAudit cpuUseRate is:{}",cpuUseRate);
		if(ObjectUtils.isEmpty(cpuUseRate) || cpuUseRate.equals("Null")){

			logger.error("get cpu use rate from network audit error ");
			status.setCpuUseRate(0.00);
			return ;
		}
		cpuUseRate = cpuUseRate.replace("%","");
		status.setCpuUseRate(Double.valueOf(cpuUseRate));
	}

	@Override
	public void getMemoryUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		String memoryUseRate = statusMap.get(SnmpFieldResources.MEMORY_TOTAL);
		if(ObjectUtils.isEmpty(memoryUseRate) || memoryUseRate.equals("Null")){

			logger.error("get memory use rate error ");
			status.setMenUseRate(0.00);
			return ;
		}
		memoryUseRate = memoryUseRate.replace("%","");
		status.setMenUseRate(Double.valueOf(memoryUseRate));
	}
}
