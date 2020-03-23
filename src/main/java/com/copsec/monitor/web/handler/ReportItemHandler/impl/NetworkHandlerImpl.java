package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class NetworkHandlerImpl implements ReportHandler {
	private WarningService warningService = SpringContext.getBean(WarningService.class);

	public Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
		Status monitorItemType = new Status();
		if (!reportItem.getResult().toString().equalsIgnoreCase("网络正常")) {
			warningEvent.setEventDetail("网络[" + reportItem.getItem() + "]异常");
			warningService.insertWarningEvent(warningEvent);

			monitorType.setStatus(0);
			monitorItemType.setStatus(0);
			monitorItemType.setMessage(warningEvent.getEventDetail());
		} else {
			monitorItemType.setMessage("网络[" + reportItem.getItem() + "]正常");
		}

		return monitorItemType;
	}

	@PostConstruct
	public void init() {
		ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.NETWORK, this);
	}
}
