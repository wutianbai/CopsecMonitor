package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SystemTypeHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(SystemTypeHandlerImpl.class);

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorType(monitorItem.getMonitorType());

		CopsecResult cmdResult = ProcessorUtils.executeCommand(CommandsResources.system_type_cmd,str -> {

			if(!ObjectUtils.isEmpty(str)){

				return CopsecResult.success(CopsecResult.SUCCESS,str.replace(StatisResources.line_spliter,""));
			}
			return CopsecResult.failed(CopsecResult.FAILED,StatisResources.default_value);
		});

		if(cmdResult.getCode() == CopsecResult.SUCCESS_CODE){

			reportItem.setResult(cmdResult.getData());
			reportItem.setStatus(StatisResources.status_normal);
		}else{

			reportItem.setResult(cmdResult.getData());
			reportItem.setStatus(StatisResources.status_error);
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.SYSTEMTYPE,this);
	}
}
