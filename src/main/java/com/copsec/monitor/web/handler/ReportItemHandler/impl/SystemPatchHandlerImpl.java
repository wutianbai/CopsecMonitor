package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import javax.annotation.PostConstruct;

import com.copsec.railway.rms.beans.MonitorItem;
import com.copsec.railway.rms.beans.ReportItem;
import com.copsec.railway.rms.enums.MonitorItemEnum;
import com.copsec.railway.rms.handler.MonitorHandler;
import com.copsec.railway.rms.sigontanPools.MonitorHandlerPools;

import org.springframework.stereotype.Component;

@Component
public class SystemPatchHandlerImpl implements MonitorHandler {

	@Override
	public ReportItem handler(MonitorItem monitorItem) {
		return null;
	}

	@PostConstruct
	public void inti(){

		MonitorHandlerPools.getInstance().registerHandler(MonitorItemEnum.SYSTEMPATCH,this);
	}
}
