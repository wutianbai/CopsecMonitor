package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.copsec.railway.rms.syslogReports.LogParseUtils;
import com.copsec.railway.rms.syslogUtils.SyslogReportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监控Web70实例下日志
 */
@Component
public class ProxyLogHandlerImpl extends BaseLogHandler {

	private static final Logger logger = LoggerFactory.getLogger(ProxyLogHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;
	/**
	 * 需要根据proxy log解析
	 * @param destFile
	 * @param  current
	 */
	@Override
	public void parseAccessLogs(File destFile,ConcurrentHashMap<String, AtomicLong> current){

		try(BufferedReader reader = new BufferedReader(new FileReader(destFile))){

			reader.lines().forEach(line -> {

				if(config.isEnableReportForProxy()){

					String message = LogParseUtils.parseProxyLogs(line,config);
					if(message.length() != 0){

						SyslogReportUtils.sendLog(message,config.getSyslogIp(),config.getSyslogPort());
					}
				}
				String[] attrs = line.split(SPLIT_STRING);
				if(attrs.length == 11){

					if(!attrs[7].contains(WHITE_STRING) && ( attrs[9].startsWith("5") ||  attrs[9].startsWith("4"))){

						updateUrlMaps(attrs[7],current);
					}
				}
			});
		}catch (Throwable t){

			logger.error(t.getMessage(),t);
		}
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.PROXYLOG,this);
	}
}
