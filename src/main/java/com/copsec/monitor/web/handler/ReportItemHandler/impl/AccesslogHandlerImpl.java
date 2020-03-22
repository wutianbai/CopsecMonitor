package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import com.alibaba.fastjson.JSON;
import com.copsec.railway.rms.beans.FilePropertyBean;
import com.copsec.railway.rms.beans.LogPolicyBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.configurations.CopsecConfigurations;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.sigontanPools.LogFilePropertyPools;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import com.copsec.railway.rms.syslogReports.LogParseUtils;
import com.copsec.railway.rms.syslogUtils.SyslogReportUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 监控am11下access日志
 */
@Component
public class AccesslogHandlerImpl extends BaseLogHandler {

	private static final Logger logger = LoggerFactory.getLogger(AccesslogHandlerImpl.class);

	@Autowired
	private CopsecConfigurations config;
	/**
	 * 解析备份文件，形成Map，其中key为响应码为500的连接地址，value为该连接出现500响应的次数
	 * 解析完成删除该文件
	 * @param destFile
	 * @param  current
	 */
	@Override
	public void parseAccessLogs(File destFile,ConcurrentHashMap<String, AtomicLong> current){

		try(BufferedReader reader = new BufferedReader(new FileReader(destFile))){

			reader.lines().filter(d -> {

				if(d.startsWith("#")){

					return false;
				}
				return true;
			}).forEach(line -> {

				String message = LogParseUtils.parseAMLogs(line,config.getLogFormat(),config);
				SyslogReportUtils.sendLog(message,config.getSyslogIp(),config.getSyslogPort());
			});
		}catch (Throwable t){

			logger.error(t.getMessage(),t);
		}
	}


	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.ACCESSLOG,this);
	}
}
