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

import com.alibaba.fastjson.JSON;
import com.copsec.railway.rms.beans.FilePropertyBean;
import com.copsec.railway.rms.beans.LogPolicyBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.fileUtils.FileReaderUtils;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.sigontanPools.LogFilePropertyPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class BaseLogHandler implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(BaseLogHandler.class);

	public final String SPLIT_STRING = " ";

	public static final String WHITE_STRING = "SAML";

	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setItem(monitorItem.getItem());
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setMonitorType(MonitorTypeEnum.LOG);
		reportItem.setMonitorId(monitorItem.getMonitorId());

		LogPolicyBean logPolicyBean = JSON.parseObject(monitorItem.getItem().toString(),LogPolicyBean.class);

		if(logger.isDebugEnabled()){

			logger.debug("access log policy is -> {}", Objects.toString(logPolicyBean));
		}

		if(ObjectUtils.isEmpty(logPolicyBean)){

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult("日志检测策略解析异常");

			return reportItem;
		}

		File sourceFile = new File(logPolicyBean.getLogPath());

		File destFile = new File(StatisResources.copied_file_path);

		if(!sourceFile.exists()){

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult("日志文件信息不存在"+logPolicyBean.getLogPath());

			return reportItem;
		}

		FilePropertyBean filePropertyBean = LogFilePropertyPools.getInstance().get(monitorItem.getMonitorId());

		if(ObjectUtils.isEmpty(filePropertyBean)){

			filePropertyBean = new FilePropertyBean();
			filePropertyBean.setMonitorId(monitorItem.getMonitorId());

			LogFilePropertyPools.getInstance().add(filePropertyBean);
		}

		try{
			/**
			 * 将日文件进行备份到/tmp/下，返回该文件对应的路径
			 * @param sources
			 * @throws IOException
			 */
			FileReaderUtils.copyFileWithFileProtities(sourceFile,destFile,filePropertyBean);

			ConcurrentHashMap<String, AtomicLong> currentMap = new ConcurrentHashMap<>();

			parseAccessLogs(destFile,currentMap);

			String result = "";
			if(!reportItem.getMonitorItemType().equals(MonitorItemEnum.ACCESSLOG)){

				result = parseWarningMessage(currentMap,logPolicyBean.getThreadHold(),monitorItem.getMonitorId());
			} else{

				result = "Am11下日志信息正常";
			}
			if(result.length() > 0){

				reportItem.setStatus(StatisResources.status_error);
				reportItem.setResult(result);
			}else{

				reportItem.setStatus(StatisResources.status_normal);
				reportItem.setResult(StatisResources.default_value);
			}
			destFile.delete();

		}catch(Throwable t){

			logger.error(t.getMessage(),t);
		}

		return reportItem;
	}


	/**
	 * 解析备份文件，形成Map，其中key为响应码为500的连接地址，value为该连接出现500响应的次数
	 * 解析完成删除该文件
	 * @param destFile
	 * @param  current
	 */
	public void parseAccessLogs(File destFile,ConcurrentHashMap<String, AtomicLong> current){


	}

	protected void updateUrlMaps(String url,ConcurrentHashMap<String, AtomicLong> current){

		if(current.containsKey(url)){
			AtomicLong v = current.get(url);
			v.getAndIncrement();
			current.replace(url, v);
		}else{

			current.put(url, new AtomicLong(1L));
		}
	}

	/**
	 * 解析Map，形成告警信息
	 */
	private String parseWarningMessage(ConcurrentHashMap<String, AtomicLong> current,long total,String monitorId){

		FilePropertyBean propertyBean = LogFilePropertyPools.getInstance().get(monitorId);
		if(ObjectUtils.isEmpty(propertyBean)){

			return "";
		}

		ConcurrentHashMap<String, AtomicLong> urlMaps = propertyBean.getUrlMaps();

		StringBuilder builder = new StringBuilder();

		long oldSum = getTotal(urlMaps);
		long currentSum = getTotal(current);
		long tmp = currentSum - oldSum;
		if( tmp >= total){ //上报数据

			builder.append("总异常记录["+ ( currentSum - oldSum )+"] 新增异常记录["+currentSum+"]");
			Iterator<Entry<String, AtomicLong>> iterator = current.entrySet().iterator();
			while(iterator.hasNext()){

				Entry<String, AtomicLong> entry = iterator.next();
				builder.append(entry.getKey() + "["+ entry.getValue().get() + "]");
			}
			urlMaps.clear();
			urlMaps.putAll(current);

		}else if( tmp <= 0){ //清空记录

			urlMaps.clear();
			builder.append("代理日志信息正常");
		}
		propertyBean.setUrlMaps(urlMaps);

		LogFilePropertyPools.getInstance().update(propertyBean);

		return builder.toString();
	}

	protected static long getTotal(ConcurrentHashMap<String, AtomicLong> map){

		Iterator<Entry<String, AtomicLong>> iterator = map.entrySet().iterator();
		long sum = 0L;
		while(iterator.hasNext()){

			Entry<String, AtomicLong> entry = iterator.next();
			sum += entry.getValue().get();
		}
		return sum;
	}
}
