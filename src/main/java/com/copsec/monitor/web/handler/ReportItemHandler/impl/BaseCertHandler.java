package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.copsec.railway.rms.beans.CertConfigBean;
import com.copsec.railway.rms.beans.CertInfoBean;
import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.CommandsResources;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorTypeEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.processorUtils.ProcessorUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.ObjectUtils;

public class BaseCertHandler implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(BaseCertHandler.class);

	@Override
	public ReportItem handler(MonitorItem monitorItem) {
		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorType(MonitorTypeEnum.CERT);
		reportItem.setMonitorItemType(monitorItem.getMonitorItemType());
		reportItem.setItem(monitorItem.getItem());

		final CertConfigBean certConfig = JSON.parseObject(monitorItem.getItem(),CertConfigBean.class);

		String instanceName = newCertDBPath(certConfig.getInstanceName());
		certConfig.setInstanceName(instanceName);

		CopsecResult getAllCertResult = ProcessorUtils.executeCommand(CommandsResources.
				getAllWeb70CertNicknames(certConfig.getInstanceName()),string -> {

			if(!ObjectUtils.isEmpty(string)){

				List<String> nicknames = Lists.newLinkedList();
				Arrays.asList(string.split(StatisResources.line_spliter)).stream().forEach(str -> {

					if(!ObjectUtils.isEmpty(str)){

						str = str.replace("CT,,","").replace("u,u,u","").trim();

						if(certConfig.getNickname().indexOf(str) != -1){

							nicknames.add(str);
						}
					}
				});
				if(nicknames.size() > 0){

					return CopsecResult.success(CopsecResult.SUCCESS,nicknames);
				}else{

					return CopsecResult.failed(CopsecResult.FAILED,"从实例["+certConfig.getInstanceName()+"]下未获取到证书信息");
				}
			}else{

				return CopsecResult.failed(CopsecResult.FAILED,"获取Web70证书信息信息错误");
			}
		});

		if(getAllCertResult.getCode() == CopsecResult.FALIED_CODE){

			reportItem.setStatus(StatisResources.status_error);
			reportItem.setResult(getAllCertResult.getData());
			return reportItem;
		}

		List<String> nicknames = (LinkedList<String>)getAllCertResult.getData();
		List<CertInfoBean> certInfos = Lists.newArrayList();
		nicknames.stream().forEach(nickname -> {

			CopsecResult certInfoResult = ProcessorUtils.
				executeCommand(CommandsResources.getWeb70CertInfoWithNickname(certConfig.getInstanceName(),nickname),string -> {

					CertInfoBean bean = new CertInfoBean();
					bean.setNickname(nickname);
					string = string.replace(StatisResources.line_spliter," ");
					if(string.startsWith("Certificate:")){

						bean = getCertInfos(string);
						if(!ObjectUtils.isEmpty(bean)){

							bean.setStatus(StatisResources.status_normal);
							return CopsecResult.success(bean);
						}
						bean.setStatus(StatisResources.status_error);
						bean.setMessage("解析实例["+certConfig.getInstanceName()
								+"]下证书["+nickname+"]失败");
						return CopsecResult.success(bean);
					}else{

						bean.setStatus(StatisResources.status_error);
						bean.setMessage("查询实例["+certConfig.getInstanceName()
								+"]下证书["+nickname+"]失败");
						return CopsecResult.success(CopsecResult.SUCCESS,bean);
					}
			});
			if(!ObjectUtils.isEmpty(certInfoResult) && certInfoResult.getCode() == CopsecResult.SUCCESS_CODE){

				certInfos.add((CertInfoBean)certInfoResult.getData());
			}
		});

		reportItem.setResult(certInfos);
		reportItem.setStatus(StatisResources.status_normal);

		return reportItem;
	}

	/**
	 * 证书路ing预处理
	 * @return
	 */
	public String newCertDBPath(String instanceName){

		return instanceName;
	}

	public static CertInfoBean getCertInfos(String str){

		CertInfoBean bean = new CertInfoBean();
		String issuer = getValues("Issuer:"," Validity:",str);
		if(!ObjectUtils.isEmpty(issuer)){

			bean.setIssuer(issuer.replaceAll(" ","").replaceAll("\"",""));
		}else{

			bean.setIssuer(StatisResources.default_value);
		}
		String startTime = getValues("Not Before:","Not After :",str);
		if(!ObjectUtils.isEmpty(startTime)){

			bean.setStartTime(formatTime(startTime));
		}

		String endTime = getValues("Not After :","Subject:",str);
		if(!ObjectUtils.isEmpty(endTime)){

			bean.setEndTime(formatTime(endTime));
		}

		String subject = getValues("Subject:","Subject",str);
		if(!ObjectUtils.isEmpty(subject)){

			bean.setSubject(subject.replaceAll(" ","").replaceAll("\"",""));
		}else{

			bean.setSubject(StatisResources.default_value);
		}

		return bean;
	}

	private static Date formatTime(String time){

		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US);
		try {

			return sdf.parse(time.substring(1,time.length()-1));
		}
		catch (ParseException e) {

			logger.error(e.getMessage(),e);
		}
		return null;
	}


	private static String getValues(String start,String end,String line){

		Pattern pattern = Pattern.compile("(?<="+start+").*?(?="+end+")");
		Matcher matcher = pattern.matcher(line);
		if(matcher.find()){

			return matcher.group();
		}
		return null;
	}
}
