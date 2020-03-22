package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import java.io.IOException;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.common.CopsecResult;
import com.copsec.railway.rms.configurations.StatisResources;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.httpClient.HttpClientUtils;
import com.copsec.railway.rms.sigontanPools.AmTokenPools;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class ApplicationHandlerImpl implements MonitorHandler {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationHandlerImpl.class);

	@Override
	public ReportItem handler(MonitorItem monitorItem) {

		ReportItem reportItem = new ReportItem();
		reportItem.setMonitorId(monitorItem.getMonitorId());
		reportItem.setMonitorItemType(MonitorItemEnum.APPLICATION);
		reportItem.setMonitorType(monitorItem.getMonitorType());
		reportItem.setItem(monitorItem.getItem());
		String url = monitorItem.getItem();

		CopsecResult applicationResult = HttpClientUtils.getMethod(url,AmTokenPools.getInstances().getCookie(),response -> {

			if(logger.isDebugEnabled()){

				logger.debug("application call -> {} and response code -> {}",url,response.getStatusLine().getStatusCode());
			}
			if(!ObjectUtils.isEmpty(response)){

				try {
					EntityUtils.consume(response.getEntity());
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}
				return CopsecResult.success("应用["+monitorItem.getItem()+"]访问正常",response.getStatusLine().getStatusCode());
			}else{

				try {
					String message = EntityUtils.toString(response.getEntity(),HttpClientUtils.ENCODING);
					try {
						EntityUtils.consume(response.getEntity());
					}
					catch (IOException e) {

						logger.error(e.getMessage(),e);
					}
					return CopsecResult.failed("应用["+monitorItem.getItem()+"]访问异常["+ message + "]",response.getStatusLine().getStatusCode());
				}
				catch (IOException e) {

					logger.error(e.getMessage(),e);
				}

			}
			return CopsecResult.failed();
		});

		if(applicationResult.getData().equals(HttpStatus.SC_OK)){

			reportItem.setStatus(StatisResources.status_normal);
		}else{

			reportItem.setStatus(StatisResources.status_error);
		}
		reportItem.setResult(applicationResult.getMessage());
		return reportItem;
	}

	@PostConstruct
	public void init(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.APPLICATION,this);
	}
}
