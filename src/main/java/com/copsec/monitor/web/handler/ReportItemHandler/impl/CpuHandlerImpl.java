package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.config.Resources;
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
public class CpuHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(CpuHandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        //状态基本信息
        monitorItemType.setMessage("处理器" + reportItem.getItem());
        monitorItemType.setResult("使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "]");

        if (reportItem.getStatus() == 0) {
            baseHandle(deviceStatus, monitorType, monitorItemType);

            WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
            List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                        monitorType.setStatus(1);
                    } else {
                        if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("[处理器]" + reportItem.getItem() + "使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "][异常]" + "超出阈值[" + warningItem.getThreadHold() + Resources.PERCENTAGE + "]");
                            generateWarningEvent(warningService, reportItem, warningEvent);
                        }
                    }
                });
            } else {
                warningEvent.setEventDetail("[处理器]" + reportItem.getItem() + "使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "][异常]");
                generateWarningEvent(warningService, reportItem, warningEvent);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("CpuHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.CPU, this);
    }
}
