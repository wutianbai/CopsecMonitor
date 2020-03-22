package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.Objects;

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
public class MemoryHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(MemoryHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorId(monitorItem.getMonitorId());

		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			CopsecResult totalResult = ProcessorUtils.executeCommand(CommandsResources.solaris_memeory_total_cmd,str -> {

				long total = 0L;
				str = str.replace(StatisResources.line_spliter,"");
				if(str.matches("\\d+\\s+[m|M]\\w+"))
				{
					total = Long.valueOf(str.substring(0, str.indexOf(" ")))* 1024*1024;
				}
				else if(str.matches("\\d+\\s+[k|K]\\w+"))
				{
					total = Long.valueOf(str.substring(0, str.indexOf(" ")))* 1024;
				}
				else if(str.matches("\\d+\\s+[g|G]\\w+"))
				{
					total = Long.valueOf(str.substring(0, str.indexOf(" ")))* 1024* 1024* 1024;
				}
				else if(str.matches("\\d+\\s+[t|T]\\w+"))
				{
					total = Long.valueOf(str.substring(0, str.indexOf(" ")))* 1024* 1024* 1024* 1024;
				}

				return CopsecResult.success(CopsecResult.SUCCESS,total);
			});

			CopsecResult freeResult = ProcessorUtils.executeCommand(CommandsResources.solaris_memory_free_cmd,str -> {

				if(ObjectUtils.isEmpty(str)){

					return CopsecResult.success(CopsecResult.SUCCESS,0L);
				}else{

					return CopsecResult.success(CopsecResult.SUCCESS,Long.valueOf(str));
				}
			});
			if(totalResult.getCode() == CopsecResult.SUCCESS_CODE && freeResult.getCode() == CopsecResult.SUCCESS_CODE){

				long used = 100- 100 * Long.valueOf((long)freeResult.getData())/Long.valueOf((long)totalResult.getData());

				reportItem.setResult(used);
				reportItem.setStatus(StatisResources.status_normal);
			}else{

				reportItem.setResult(0L);
				reportItem.setStatus(StatisResources.status_error);
			}
		}else {

			long total=0L,buffered = 0L,cached = 0L,free = 0L;
			String[] cmds = {CommandsResources.linux_memory_total_cmd,CommandsResources.linux_memory_buffered_cmd,
			CommandsResources.linux_meory_cached_cmd,CommandsResources.linux_memory_free_cmd};

			for(int i=0;i< cmds.length ;i++){

				String cmd = cmds[i];
				CopsecResult result = ProcessorUtils.executeCommand(cmd,str -> {

					str = str.replace(StatisResources.line_spliter,"");
					if (str.matches("\\d+\\s+[m|M]\\w+")) {

						return CopsecResult.success(CopsecResult.SUCCESS,Long.valueOf(str.substring(0, str
								.indexOf(" "))) * 1024 * 1024);
					} else if (str.matches("\\d+\\s+[k|K]\\w+")) {

						return CopsecResult.success(CopsecResult.SUCCESS,Long.valueOf(str.substring(0, str
								.indexOf(" "))) * 1024);
					} else if (str.matches("\\d+\\s+[g|G]\\w+")) {

						return CopsecResult.success(CopsecResult.SUCCESS,Long.valueOf(str.substring(0, str
								.indexOf(" "))) * 1024 * 1024 * 1024);
					} else if (str.matches("\\d+\\s+[t|T]\\w+")) {

						return CopsecResult.success(CopsecResult.SUCCESS,Long.valueOf(str.substring(0, str
								.indexOf(" "))) * 1024 * 1024 * 1024 * 1024);
					} else {

						return CopsecResult.success(CopsecResult.SUCCESS,0L);
					}
				});
				switch(i){

					case 0:
						total = (long)result.getData();
						break;
					case 1:
						buffered = (long)result.getData();
						break;
					case 2:
						cached = (long)result.getData();
						break;
					case 3:
						free = (long)result.getData();
						break;
				}
			}
			if(total > 0L){

				long used = total - free - cached - buffered;
				long ratio = 100 - 100 * used/total;

				reportItem.setStatus(StatisResources.status_normal);
				reportItem.setResult(ratio);
			}else{

				reportItem.setStatus(StatisResources.status_error);
				reportItem.setResult(StatisResources.default_value);
			}
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.MEMORY,this);
	}
}
