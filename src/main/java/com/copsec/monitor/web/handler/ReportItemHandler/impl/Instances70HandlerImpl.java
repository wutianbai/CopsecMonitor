package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.InstancesListenerBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.domUtils.XmlDomUtils;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.fileUtils.FileReaderUtils;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.google.common.collect.Lists;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 读取server.xml配置文件获取pid信息和listener信息
 */
@Component
public class Instances70HandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(Instances70HandlerImpl.class);
	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorType(monitorItem.getMonitorType());

		String instanceDir = monitorItem.getItem() + File.separator + StatisResources.server_config_path;
		File file = new File(instanceDir);
		if(!file.exists()){

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult("Web70实例["+instanceDir + "]下配置文件不存在");
			return reportItem;
		}
		List<Integer> ports = Lists.newArrayList();
		InstancesListenerBean listenerBean = new InstancesListenerBean();
		try {
			List<Element> elements = XmlDomUtils.getAllElement("/server/http-listener/port",instanceDir);

			elements.stream().forEach(e -> {

				ports.add(Integer.valueOf(e.getTextTrim()));
			});

			listenerBean.setPorts(ports);

			String pidPath = XmlDomUtils.getObjectByPath("/server/temp-path",instanceDir) + File.separator+ "pid";

			Optional<String> optionalS = FileReaderUtils.readerContent(pidPath);

			if(optionalS.isPresent()){

				String cmd = CommandsResources.prcessorBuilder(optionalS.get());

				CopsecResult cmdResult = ProcessorUtils.processorIsRunning(cmd);

				if(cmdResult.getData().equals(true)){

					reportItem.setStatus(StatisResources.status_normal);
				}else{

					reportItem.setStatus(StatisResources.status_error);
				}
				listenerBean.setMessage(cmdResult.getMessage());
			}else{

				listenerBean.setMessage("实例已停止");
			}
			reportItem.setResult(listenerBean);
		}
		catch (Exception e) {

			logger.error(e.getMessage(),e);
		}
		if(logger.isDebugEnabled()){

			logger.debug("instance -> {} and report -> {}",monitorItem.getItem(),listenerBean);
		}
		return reportItem;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.INSTANCES_WEB70,this);
	}
}
