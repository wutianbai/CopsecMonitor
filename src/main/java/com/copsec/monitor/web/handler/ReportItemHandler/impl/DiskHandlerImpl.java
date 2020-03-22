package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.DiskUseBean;
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
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class DiskHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(DiskHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorType(monitorItem.getMonitorType());

		CopsecResult result = null;
		if(config.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			result = ProcessorUtils.executeCommand(CommandsResources.solaris_disk_cmd,string -> {

				List<DiskUseBean> list = Lists.newArrayList();
				Arrays.stream(string.split(" ")).forEach(str -> {

					String[] attr = str.split(StatisResources.line_spliter,2);
					DiskUseBean bean = new DiskUseBean();
					bean.setDisk(attr[1]);
					bean.setUsed(attr[0].replaceAll("%",""));
					list.add(bean);
				});
				return CopsecResult.success(CopsecResult.SUCCESS,list);
			});

		}else{

			result = ProcessorUtils.executeCommand(CommandsResources.linux_disk_cmd,string -> {

				List<DiskUseBean> list = Lists.newArrayList();
				Arrays.stream(string.split(StatisResources.line_spliter)).forEach(str -> {

					String[] attrs = str.split(" ");
					DiskUseBean bean = new DiskUseBean();
					if(attrs.length == 2){

						bean.setDisk(attrs[1]);
						bean.setUsed(attrs[0].replaceAll("%",""));
					}else if(attrs.length == 3){

						bean.setTotal(attrs[0]);
						bean.setDisk(attrs[2]);
						bean.setUsed(attrs[1]);
					}
					list.add(bean);
				});
				return CopsecResult.success(CopsecResult.SUCCESS,list);
			});
		}
		if(!ObjectUtils.isEmpty(result) && result.getCode() == CopsecResult.SUCCESS_CODE){

			reportItem.setStatus(StatisResources.status_normal);
			reportItem.setResult(result.getData());
		}else{

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult(result.getData());
		}
		return reportItem;
	}

	@PostConstruct
	public void init(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.DISK,this);
	}
}
