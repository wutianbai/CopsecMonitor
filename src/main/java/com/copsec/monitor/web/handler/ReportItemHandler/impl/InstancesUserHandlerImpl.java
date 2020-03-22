package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.InstancesListenerBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.fileUtils.FileReaderUtils;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class InstancesUserHandlerImpl implements MonitorHandler {

	private  static final Logger logger = LoggerFactory.getLogger(InstancesUserHandlerImpl.class);

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorType(monitorItem.getMonitorType());

		String instanceDir = monitorItem.getItem() + File.separator + "/config/dse.ldif";
		File file = new File(instanceDir);
		if(!file.exists()){

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult("UserDS实例["+instanceDir + "]下配置文件不存在");
			return reportItem;
		}
		List<Integer> ports = Lists.newArrayList();
		InstancesListenerBean listenerBean = new InstancesListenerBean();
		try {

			try(BufferedReader reader = new BufferedReader(new FileReader(new File(instanceDir)))){

				String line = null;
				while( (line = reader.readLine()) != null){

					if(line.matches("^nsslapd-port:\\s*\\d+")){

						String[] strs = line.split(":");

						if(strs.length ==2){

							ports.add(Integer.valueOf(strs[1].trim()));
						}
					}
				}
				listenerBean.setPorts(ports);
			}catch (IOException e){

				logger.error(e.getMessage(),e);
			}
			String pidPath = monitorItem.getItem() + File.separator + "/logs/pid";

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

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.INSTANCES_USER,this);

	}
}
