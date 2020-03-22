package com.copsec.monitor.web.snmp.statusComponent;

import java.util.List;
import java.util.Map;

import com.copsec.monitor.web.beans.snmp.InterfaceBean;
import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.snmp.resources.SnmpFieldResources;
import com.copsec.monitor.web.snmp.utils.SnmpDeviceStatusUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class SnmpForDX extends BaseSnmpComponent {

	private static final Logger logger = LoggerFactory.getLogger(SnmpForDX.class);

	private SnmpConfigBean snmpConfig;

	private Map<String,OidMapBean> oidMap;

	private SystemConfig systemConfig;

	public SnmpForDX(SnmpConfigBean config ,Map<String,OidMapBean> maps,SystemConfig systemConfig){

		super(config,maps,systemConfig);
		this.systemConfig = systemConfig;
		this.snmpConfig = config;
		this.oidMap = maps;
	}

	@Override
	public List<InterfaceBean> getInterfaceCurrentValues(Map<String, String> statusMap) {
		return SnmpDeviceStatusUtils.getDeviceInterface(statusMap);
	}

	@Override
	public void getSpeed(List<InterfaceBean> preValue, long period, SnmpDeviceStatus status,Map<String,String> map) {

		List<InterfaceBean> current = getInterfaceCurrentValues(map);

		status.setNetSpeed(SnmpDeviceStatusUtils.getAllSpeed(preValue,current,period));
	}

	@Override
	public void getCpuUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		if(ObjectUtils.isEmpty(statusMap.get(SnmpFieldResources.CPU_USE_RATE))){

			logger.error("snmp component for dx error did not get cpu use rate values ");
			status.setCpuUseRate(0.00);
		}
		double cpuUseRate = (100.00 - Double.valueOf(statusMap.get(SnmpFieldResources.CPU_USE_RATE)));
		status.setCpuUseRate(cpuUseRate);
	}

	@Override
	public void getMemoryUseRate(SnmpDeviceStatus status, Map<String, String> statusMap) {

		status.setMenUseRate(SnmpDeviceStatusUtils.diskPercentage(statusMap));
	}


	@Override
	public void countTotalSize(SnmpDeviceStatus status, List<InterfaceBean> preValue, long total, Map<String, String> statusMap) {

		List<InterfaceBean> current = getInterfaceCurrentValues(statusMap);
		status.setTotalAddSize(SnmpDeviceStatusUtils.getTotal(preValue,current,status.getTotalAddSize()));
	}

	@Override
	public void setWarnMessage(SnmpDeviceStatus status, List<InterfaceBean> preValue, Map<String, String> statusMap) {

		List<InterfaceBean> current = getInterfaceCurrentValues(statusMap);
		status.setWarnMessage(SnmpDeviceStatusUtils.getMessage(current,preValue));
	}
}
