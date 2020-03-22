package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class SystemVersionHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(SystemVersionHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setMonitorId(monitorItem.getMonitorId());

		CopsecResult versionResult = null;
		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			versionResult = ProcessorUtils.executeCommand(CommandsResources.solaris_version_cmd,str -> {

				List<String> strs = Arrays.asList(str.split(StatisResources.line_spliter));
				StringBuilder sb = new StringBuilder();
				strs.stream().forEach(s -> {

					if(s.startsWith("Release")){

						sb.append(s.split(":")[1]);
					}
				});
				return CopsecResult.success(sb.toString());
			});
		}else{

			versionResult = ProcessorUtils.executeCommand(CommandsResources.linux_version_cmd,str -> {

				str = str.replace(StatisResources.line_spliter,"");
				if(!ObjectUtils.isEmpty(str)){

					return CopsecResult.success(CopsecResult.SUCCESS,str);
				}
				return CopsecResult.failed(CopsecResult.FAILED,"未获取到相关版本信息");
			});
		}

		if(!ObjectUtils.isEmpty(versionResult) && versionResult.getCode() == CopsecResult.SUCCESS_CODE){

			reportItem.setStatus(StatisResources.status_normal);
			reportItem.setResult(versionResult.getData());
		}else{

			reportItem.setResult(versionResult.getData());
			reportItem.setStatus(StatisResources.status_error);
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.SYSTEMVERSION,this);
	}
}
