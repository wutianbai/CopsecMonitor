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

/**
 * 监控Web70实例下日志
 */
@Component
public class ProxyLogHandlerImpl implements ReportHandler {
    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
            warningEvent.setEventDetail(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]" + "超出阈值[" + warningItem.getThreadHold() + "]");
            warningService.insertWarningEvent(warningEvent);

            monitorType.setStatus(0);
            monitorItemType.setStatus(0);
            monitorItemType.setMessage(warningEvent.getEventDetail());
        } else {
            monitorItemType.setMessage(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]");
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.PROXYLOG, this);
    }
}
