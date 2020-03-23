package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.CertInfoBean;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Cert70HandlerImpl implements ReportHandler {
    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> CERT70Map = new ConcurrentHashMap<>();
        List<CertInfoBean> list = (List<CertInfoBean>) reportItem.getResult();
        list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(certInfo -> {
            Status statusBean = new Status();
            if (certInfo.getStatus() == 0) {
                warningEvent.setEventDetail("证书70[" + certInfo.getNickname() + "]异常");
                warningEvent.setId(null);
                warningService.insertWarningEvent(warningEvent);

                monitorType.setStatus(0);
                monitorItemType.setStatus(0);
                statusBean.setStatus(0);
                statusBean.setMessage(warningEvent.getEventDetail());
            } else {
                statusBean.setMessage("证书70[" + certInfo.getNickname() + "]正常");
            }
            CERT70Map.putIfAbsent(certInfo.getNickname(), statusBean);
        });
        monitorItemType.setMessage(CERT70Map);

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.CERT70, this);
    }
}
