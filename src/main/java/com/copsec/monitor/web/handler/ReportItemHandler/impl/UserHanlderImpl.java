package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.PostConstruct;

import ch.qos.logback.core.status.StatusBase;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.beans.UserStatusBean;
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
public class UserHanlderImpl implements MonitorHandler {


	private static final Logger logger = LoggerFactory.getLogger(UserHanlderImpl.class);

	@Autowired
	private CopsecConfigurations configurations;

	@Override
	public ReportItem handler(MonitorItem monitorItem)
	{
		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorType(monitorItem.getMonitorType());
		CopsecResult userResult = null;
		if(configurations.getSystemType().equalsIgnoreCase(StatisResources.system_type_solaris)){

			userResult = ProcessorUtils.executeCommand(CommandsResources.solaris_user_cmd,str -> {

				List<String> strList = Arrays.asList(str.split(StatisResources.line_spliter));

				List<UserStatusBean> status = Lists.newArrayList();

				strList.stream().forEach(item -> {

					String[] attrs = item.split(" ");
					if(monitorItem.getItem().contains(attrs[0])){

						UserStatusBean statusBean = new UserStatusBean();
						statusBean.setUserId(attrs[0]);
						if(attrs.length == 3){

							statusBean.setStatus(StatisResources.status_error);
						}else{

							statusBean.setStatus(StatisResources.status_normal);
						}
						status.add(statusBean);
					}
				});
 				return CopsecResult.success(status);
			});

		}else{

			userResult = ProcessorUtils.executeCommand(CommandsResources.linux_user_cmd,str -> {

				List<String> strList = Arrays.asList(str.split(StatisResources.line_spliter));

				List<UserStatusBean> status = Lists.newArrayList();

				strList.stream().forEach(item -> {

					String[] attrs = item.split(":");
					if(monitorItem.getItem().indexOf(attrs[0]) > 0){

						UserStatusBean statusBean = new UserStatusBean();
						statusBean.setUserId(attrs[0]);
						if(attrs[1].equals("!!")){

							statusBean.setStatus(StatisResources.status_error);
						}else{

							statusBean.setStatus(StatisResources.status_normal);
						}
						status.add(statusBean);
					}
				});
				return CopsecResult.success(status);
			});
		}

		if(!ObjectUtils.isEmpty(userResult) && userResult.getCode() == CopsecResult.SUCCESS_CODE){

			reportItem.setResult(userResult.getData());
			reportItem.setStatus(StatisResources.status_normal);
		}else{

			reportItem.setResult(userResult.getData());
			reportItem.setStatus(StatisResources.status_error);
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.USER,this);
	}
}
