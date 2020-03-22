package com.copsec.monitor.web.snmp.statusComponent;

import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.snmp.InterfaceBean;
import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.beans.snmp.SnmpDeviceStatus;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.snmp.sendComponent.AbstractSnmpComponent;
import com.copsec.monitor.web.snmp.sendComponent.SnmpComponent4V1;
import com.copsec.monitor.web.snmp.sendComponent.SnmpComponent4V2;
import com.copsec.monitor.web.snmp.sendComponent.SnmpComponent4V3;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseSnmpComponent {

	private SnmpConfigBean snmpConfig;

	private Map<String,OidMapBean> oidMap;

	private SystemConfig systemConfig;

	public BaseSnmpComponent(SnmpConfigBean config ,Map<String,OidMapBean> maps,SystemConfig systemConfig){

		this.snmpConfig = config;
		this.oidMap = maps;
		this.systemConfig = systemConfig;
	}

	public SnmpDeviceStatus generateDeviceStatus(List<InterfaceBean> preValue,long period,Map<String,String> map){

		SnmpDeviceStatus status = new SnmpDeviceStatus();
		Device device = DevicePools.getInstance().getDevice(snmpConfig.getDeviceId());
		/**
		 * 获取响应信息
		 */
		status.setDeviceId(snmpConfig.getDeviceId());
		AbstractSnmpComponent snmp = null;
		if(snmpConfig.getVersion().equals("v1")){

			snmp = new SnmpComponent4V1(snmpConfig,systemConfig);
		}else if(snmpConfig.getVersion().equals("v2")){

			snmp = new SnmpComponent4V2(snmpConfig,systemConfig);
		}else if(snmpConfig.getVersion().equals("v3")){

			snmp = new SnmpComponent4V3(snmpConfig,systemConfig);
		}else{

			snmp = null;
		}

		List<OidMapBean> oidList = Lists.newArrayList();
		oidMap.entrySet().stream().forEach(entry -> {

			oidList.add(entry.getValue());
		});
		try {
			snmp.initSnmp();
			snmp.getResponse(oidList,map);
			if(map.entrySet().size() == 0){

				status.setWarnMessage("获取服务器资源信息失败");
				status.setDeviceStatus("故障");
				return status;
			}
		}catch (Exception e){
			status.setWarnMessage("服务器连接失败");
			status.setDeviceStatus("故障");
			return status;
		}
		status.setDeviceStatus("健康");
//		if(device.getData().getDeviceType().equals("linux")){
//
//			OID[] oids = { new OID(LinuxMibs.INTERFACE_TABLE)};
//			snmp.getInterfaceTable(oids,map);
//
//			getSpeed(preValue,period,status,map);
//
//			countTotalSize(status,preValue,status.getTotalAddSize(),map);
//
//			status.setWarnMessage(SnmpDeviceStatusUtils.getMessage(getInterfaceCurrentValues(map),preValue));
//		}

		snmp.close();

		getCpuUseRate(status,map);

		getMemoryUseRate(status,map);

		status.setUpdateTime(new Date());

		return status;
	}

	public List<InterfaceBean> getInterfaceCurrentValues(Map<String,String> statusMap){return null;}

	public void getSpeed(List<InterfaceBean> preValue,long period,SnmpDeviceStatus status,Map<String,String> statusMap){}

	public void getCpuUseRate(SnmpDeviceStatus status,Map<String,String> statusMap){}

	public void getMemoryUseRate(SnmpDeviceStatus status,Map<String,String> statusMap){}

	public void countTotalSize(SnmpDeviceStatus status,List<InterfaceBean> preValue,long total,Map<String,String> statusMap){}

	public void setWarnMessage(SnmpDeviceStatus status,List<InterfaceBean> preValue,Map<String,String> statusMap){}

	public void getDetectionNetInfo(SnmpDeviceStatus status,Map<String,String> statusMap){}
}
