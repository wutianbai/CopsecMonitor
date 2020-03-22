package com.copsec.monitor.web.snmp.statusComponent;

import java.util.Map;

import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SnmpForFirewall extends BaseSnmpComponent {

	private static final Logger logger = LoggerFactory.getLogger(SnmpForFirewall.class);

	private SnmpConfigBean snmpConfig;

	private Map<String,OidMapBean> oidMap;

	private SystemConfig systemConfig;

	public SnmpForFirewall(SnmpConfigBean config ,Map<String,OidMapBean> maps,SystemConfig systemConfig){

		super(config,maps,systemConfig);
		this.systemConfig = systemConfig;
		this.snmpConfig = config;
		this.oidMap = maps;
	}

	@Override
	public void getCpuUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		logger.debug("get cpu status from firewall {}",statusMap.get(SnmpFieldResources.CPU_USE_RATE));
		status.setCpuUseRate(Double.valueOf(statusMap.get(SnmpFieldResources.CPU_USE_RATE)));
	}

	@Override
	public void getMemoryUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		logger.debug("get memory use rate from firewall {}",statusMap.get(SnmpFieldResources.MEMORY_TOTAL));
		status.setMenUseRate(Double.valueOf(statusMap.get(SnmpFieldResources.MEMORY_TOTAL)));
	}
}
