package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

@Component
public class Cert70HandlerImpl extends BaseCertHandler {

	@PostConstruct
	public void init(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.CERT70, this);
	}


	@Override
	public String newCertDBPath(String instanceName) {

		return instanceName;
	}
}
