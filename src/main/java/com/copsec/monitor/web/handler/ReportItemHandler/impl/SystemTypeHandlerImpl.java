package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.logUtils.SysLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SystemTypeHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(SystemTypeHandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {

        Status monitorItemType = new Status();
        monitorItemType.setMessage(reportItem.getResult());

        //发送SysLog日志
        SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "系统类型", reportItem.getResult().toString());

        if (logger.isDebugEnabled()) {
            logger.debug("SystemTypeHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.SYSTEMTYPE, this);
    }
}
