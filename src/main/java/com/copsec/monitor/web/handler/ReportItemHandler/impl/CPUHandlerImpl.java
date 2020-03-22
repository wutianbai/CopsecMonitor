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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class CPUHandlerImpl implements MonitorHandler {


	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorType(monitorItem.getMonitorType());

		CopsecResult result = null;
		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			result = ProcessorUtils.executeCommand(CommandsResources.solaris_cpu_ratio_cmd,string -> {

				if(ObjectUtils.isEmpty(string)){

					return CopsecResult.success("未获取到相关数据");
				}
				return CopsecResult.success(string);
			});
		}else{

			result = ProcessorUtils.executeCommand(CommandsResources.linux_cpu_ratio_cmd,string -> {

				if(ObjectUtils.isEmpty(string)){

					return CopsecResult.failed(CopsecResult.FAILED,"未获取到相关数据");
				}
				return CopsecResult.success(CopsecResult.SUCCESS,string.replace(StatisResources.line_spliter,""));
			});
		}
		if(!ObjectUtils.isEmpty(result) && result.getCode() == CopsecResult.SUCCESS_CODE){

			reportItem.setResult(result.getData());
			reportItem.setStatus(StatisResources.status_normal);
		}else{

			reportItem.setResult("获取CPU信息出错");
			reportItem.setStatus(StatisResources.status_error);
		}
		return reportItem;
	}

	@PostConstruct
	public void init(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.CPU,this);
	}
}
