package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.logUtils.SysLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class MemoryHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(MemoryHandlerImpl.class);

    @Autowired
    private SystemConfig config;

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        //状态基本信息
        monitorItemType.setMessage("内存" + reportItem.getItem());
        monitorItemType.setResult("使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "]");

        //发送SysLog日志
        SysLogUtil.sendLog(config, device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "内存", "[内存阈值]" + reportItem.getItem()+ Resources.PERCENTAGE + "使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "]");

        if (reportItem.getStatus() == 0) {
            baseHandle(deviceStatus, monitorType, monitorItemType);

            WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
            List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                        if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                            deviceStatus.setStatus(1);
                            monitorType.setStatus(1);
                        } else {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("[内存阈值]" + reportItem.getItem() + Resources.PERCENTAGE+ "使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "][超出阈值]");
                            generateWarningEvent(warningService, reportItem, warningEvent);
                        }
                    }
                });
            } else {
                warningEvent.setEventDetail("[内存阈值]" + reportItem.getItem() + Resources.PERCENTAGE+"使用率[" + reportItem.getResult() + Resources.PERCENTAGE + "][超出阈值]");
                generateWarningEvent(warningService, reportItem, warningEvent);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("MemoryHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.MEMORY, this);
    }
}
