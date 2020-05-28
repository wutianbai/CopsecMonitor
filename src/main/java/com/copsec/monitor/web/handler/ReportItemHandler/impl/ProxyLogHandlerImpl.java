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
public class ProxyLogHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(ProxyLogHandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        //基本信息
        JSONObject log = JSON.parseObject(reportItem.getItem());
        monitorItemType.setMessage("路径[" + log.getString("logPath") + "]阈值[" + log.getString("threadHold") + "]");
        monitorItemType.setResult(reportItem.getResult().toString());

        //发送SysLog日志
        SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "代理日志", "代理日志[" + log.getString("logPath") + "][" + reportItem.getResult() + "]阈值[" + log.getString("threadHold") + "]");

        if (reportItem.getStatus() == 0) {
            baseHandle(deviceStatus, monitorType, monitorItemType);

            WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
            List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                        monitorType.setStatus(1);
                    } else {
                        warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                        warningEvent.setEventDetail("代理日志[" + log.getString("logPath") + "][" + reportItem.getResult() + "]" + "超出阈值[" + warningItem.getThreadHold() + "]");
                        generateWarningEvent(warningService, reportItem, warningEvent);
                    }
                });
            } else {
                warningEvent.setEventDetail("代理日志[" + log.getString("logPath") + "][" + reportItem.getResult() + "]");
                generateWarningEvent(warningService, reportItem, warningEvent);
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("ProxyLogHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.PROXYLOG, this);
    }
}
