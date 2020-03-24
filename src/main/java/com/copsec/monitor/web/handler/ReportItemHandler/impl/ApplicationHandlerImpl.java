package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class ApplicationHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationHandlerImpl.class);

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();

        //基本信息
        monitorItemType.setMessage(reportItem.getItem());
        monitorItemType.setResult("访问正常");

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
        if (warningItemList.size() > 0) {
            warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                if (reportItem.getStatus() == 0) {
                    warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                    warningEvent.setEventDetail("通道[" + reportItem.getItem() + "]" + reportItem.getResult().toString());
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                }
            });
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ApplicationHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.APPLICATION, this);
    }
}
