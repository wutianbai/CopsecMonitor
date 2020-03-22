package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ProcessorStatusBean;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class ImServiceHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ImServiceHandlerImpl.class);

	@Override
	public ReportItem handler(MonitorItem monitorItem)
	{
		ReportItem reportItem = new ReportItem();
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setItem(monitorItem.getItem());

		List<String> dNames = Arrays.asList(monitorItem.getItem().split(","));
		List<ProcessorStatusBean> statusBeanList = Lists.newArrayList();
		dNames.stream().forEach(name -> {

			ProcessorStatusBean status = new ProcessorStatusBean();
			status.setProcessorName(name);
			CopsecResult result = ProcessorUtils.processorIsRunning(CommandsResources.getProcessCmd(name));
			status.setMessage(result.getMessage());
			statusBeanList.add(status);
		});

		reportItem.setStatus(StatisResources.status_normal);
		reportItem.setResult(statusBeanList);
		if(logger.isDebugEnabled()){

			logger.debug("IM processor -> {}", Objects.toString(statusBeanList));
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.IMSERVICE,this);
	}
}
