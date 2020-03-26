package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.CertInfoBean;
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
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Cert40HandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(Cert40HandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        WarningEvent warningEvent = baseHandle(deviceStatus, device, userInfo, warningService, reportItem);

        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> CERT40Map = new ConcurrentHashMap<>();

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);

        List<CertInfoBean> list = (List<CertInfoBean>) reportItem.getResult();
        list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(certInfo -> {
            Status statusBean = new Status();

            //基本信息
            statusBean.setMessage("证书40[" + certInfo.getNickname() + "]");
            statusBean.setResult(certInfo.getMessage());

            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (certInfo.getStatus() == 0) {
                        if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("证书40[" + certInfo.getNickname() + "]异常[" + certInfo.getMessage() + "]");

                            warningEvent.setId(null);
                            warningService.insertWarningEvent(warningEvent);
                        }

                        deviceStatus.setStatus(0);
                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                    }
                });
            }
            CERT40Map.putIfAbsent(certInfo.getNickname(), statusBean);
        });
        monitorItemType.setMessage(CERT40Map);

        if (logger.isDebugEnabled()) {
            logger.debug("Cert40Handler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.CERT40, this);
    }
}
