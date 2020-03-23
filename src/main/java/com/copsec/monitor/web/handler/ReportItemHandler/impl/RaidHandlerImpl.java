package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.beans.warning.VmInfoBean;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RaidHandlerImpl implements ReportHandler {

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> RAIDMap = new ConcurrentHashMap<>();
        VmInfoBean vmInfo = (VmInfoBean) reportItem.getResult();

        ConcurrentHashMap<String, Status> diskInfoMap = new ConcurrentHashMap<>();
        Status diskInfoStatusBean = new Status();
        vmInfo.getDiskInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(diskInfo -> {
            Status diskInfoStatus = new Status();
            if (Integer.parseInt(diskInfo.getStatus()) == 0) {
                warningEvent.setEventDetail("虚拟机[" + diskInfo.getId() + "]磁盘异常");
                warningEvent.setId(null);
                warningService.insertWarningEvent(warningEvent);

                monitorType.setStatus(0);
                monitorItemType.setStatus(0);
                diskInfoStatusBean.setStatus(0);
                diskInfoStatus.setStatus(0);
                diskInfoStatus.setMessage(warningEvent.getEventDetail());
            } else {
                diskInfoStatus.setMessage("虚拟机[" + diskInfo.getId() + "]磁盘正常");
            }
            diskInfoMap.putIfAbsent(diskInfo.getId(), diskInfoStatus);
        });
        diskInfoStatusBean.setMessage(diskInfoMap);
        RAIDMap.putIfAbsent("diskInfos", diskInfoStatusBean);

        ConcurrentHashMap<String, Status> domainInfoMap = new ConcurrentHashMap<>();
        Status domainInfoStatusBean = new Status();
        vmInfo.getDomainInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(domainInfo -> {
            Status domainInfoStatus = new Status();
            if (Integer.parseInt(domainInfo.getState()) == 0) {
                warningEvent.setEventDetail("虚拟机[" + domainInfo.getName() + "]域异常");
                warningEvent.setId(null);
                warningService.insertWarningEvent(warningEvent);

                monitorType.setStatus(0);
                monitorItemType.setStatus(0);
                domainInfoStatusBean.setStatus(0);
                domainInfoStatus.setStatus(0);
                domainInfoStatus.setMessage(warningEvent.getEventDetail());
            } else {
                domainInfoStatus.setMessage("虚拟机[" + domainInfo.getName() + "]域正常");
            }
            domainInfoMap.putIfAbsent(domainInfo.getName(), domainInfoStatus);
        });
        domainInfoStatusBean.setMessage(domainInfoMap);
        RAIDMap.putIfAbsent("domainInfos", domainInfoStatusBean);

        ConcurrentHashMap<String, Status> volumeInfoMap = new ConcurrentHashMap<>();
        Status volumeInfoStatusBean = new Status();
        vmInfo.getVolumeInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(volumeInfo -> {
            Status volumeInfoStatus = new Status();
            if (Integer.parseInt(volumeInfo.getStatus()) == 0) {
                warningEvent.setEventDetail("虚拟机[" + volumeInfo.getName() + "]卷异常");
                warningEvent.setId(null);
                warningService.insertWarningEvent(warningEvent);

                monitorType.setStatus(0);
                monitorItemType.setStatus(0);
                volumeInfoStatusBean.setStatus(0);
                volumeInfoStatus.setStatus(0);
                volumeInfoStatus.setMessage(warningEvent.getEventDetail());
            } else {
                volumeInfoStatus.setMessage("虚拟机[" + volumeInfo.getName() + "]卷正常");
            }
            volumeInfoMap.putIfAbsent(volumeInfo.getId(), volumeInfoStatus);
        });
        volumeInfoStatusBean.setMessage(volumeInfoMap);
        RAIDMap.putIfAbsent("volumeInfos", volumeInfoStatusBean);

        Status controllerInfoStatusBean = new Status();
        controllerInfoStatusBean.setMessage(vmInfo.getControllerInfos());
        RAIDMap.putIfAbsent("controllerInfos", controllerInfoStatusBean);

        monitorItemType.setMessage(RAIDMap);

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.RAID, this);
    }
}
