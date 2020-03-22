package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.sun.org.apache.bcel.internal.classfile.PMGClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class NetworkHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(NetworkHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setItem(monitorItem.getItem());

		CopsecResult pingResult = null;
		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			pingResult = ProcessorUtils.executeCommand(CommandsResources.getSolairsPingCmd(monitorItem.getItem()),
				str -> {
				if(str.equalsIgnoreCase("0")){

					return CopsecResult.success(StatisResources.status_normal);
				}
				return CopsecResult.success(StatisResources.status_error);
			});

		}else{

			pingResult  = ProcessorUtils.executeCommand(CommandsResources.getLinuxPingCmd(monitorItem.getItem()),
				str -> {

					str = str.replace(StatisResources.line_spliter,"");
					if(str.equalsIgnoreCase("0")){

						return CopsecResult.success(StatisResources.status_normal);
					}
					return CopsecResult.success(StatisResources.status_error);
			});
		}

		if(!ObjectUtils.isEmpty(pingResult) && pingResult.getData().equals(StatisResources.status_normal))
		{

			reportItem.setStatus(StatisResources.status_normal);
			reportItem.setResult("网络正常");
		}else{

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult(pingResult.getData());
		}
		if(logger.isDebugEnabled()){

			logger.debug("network -> {} and report -> {}",monitorItem.getItem(),reportItem.getResult());
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.NETWORK,this);
	}
}
