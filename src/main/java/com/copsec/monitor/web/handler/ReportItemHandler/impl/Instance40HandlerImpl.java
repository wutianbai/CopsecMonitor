package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.logUtils.SysLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class Instance40HandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(Instance40HandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        JSONObject jSONObject = JSON.parseObject(reportItem.getResult().toString());
        //基本信息
        monitorItemType.setMessage(reportItem.getItem());
        monitorItemType.setResult(jSONObject.getString("ports"));

        //发送SysLog日志
        SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "WebProxy40实例", "WebProxy40实例[" + reportItem.getItem() + "][" + jSONObject.getString("ports") + "][" + jSONObject.getString("message") + "]");

        if (reportItem.getStatus() == 0) {
            baseHandle(deviceStatus, monitorType, monitorItemType);

            WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
            warningEvent.setEventDetail("WebProxy40实例[" + reportItem.getItem() + "][" + jSONObject.getString("ports") + "][" + jSONObject.getString("message") + "]");
            List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                        monitorType.setStatus(1);
                    }else{
                        warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                        generateWarningEvent(warningService, reportItem, warningEvent);
                    }
                });
            } else {
                generateWarningEvent(warningService, reportItem, warningEvent);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Instance40Handler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.INSTANCES_WEBPROXY40, this);
    }
}
