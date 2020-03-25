package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.beans.warning.VmInfoBean;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RaidHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(RaidHandlerImpl.class);

    //    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningService warningService, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> RAIDMap = new ConcurrentHashMap<>();

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);

        VmInfoBean vmInfo = (VmInfoBean) reportItem.getResult();

        ConcurrentHashMap<String, Status> diskInfoMap = new ConcurrentHashMap<>();
        Status diskInfoStatusBean = new Status();
        vmInfo.getDiskInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(diskInfo -> {
            Status diskInfoStatus = new Status();

            //基本信息
            diskInfoStatus.setMessage("虚拟机[" + diskInfo.getId() + "]磁盘");
            diskInfoStatus.setResult("容量[" + diskInfo.getSize() + "]");
            diskInfoStatus.setState("剩余空间[" + diskInfo.getSpare() + "]");

            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (Integer.parseInt(diskInfo.getStatus()) == 0) {
                        if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("虚拟机[" + diskInfo.getId() + "]磁盘 异常");

                            warningEvent.setId(null);
                            warningService.insertWarningEvent(warningEvent);
                        }

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        diskInfoStatusBean.setStatus(0);
                        diskInfoStatus.setStatus(0);
                    }
                });
            }
            diskInfoMap.putIfAbsent(diskInfo.getId(), diskInfoStatus);
        });
        diskInfoStatusBean.setMessage(diskInfoMap);
        RAIDMap.putIfAbsent("diskInfos", diskInfoStatusBean);

        ConcurrentHashMap<String, Status> domainInfoMap = new ConcurrentHashMap<>();
        Status domainInfoStatusBean = new Status();
        vmInfo.getDomainInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(domainInfo -> {
            Status domainInfoStatus = new Status();

            //基本信息
            domainInfoStatus.setMessage("虚拟机[" + domainInfo.getName() + "]域");
            domainInfoStatus.setResult("使用率[" + domainInfo.getUtil() + "]");
            domainInfoStatus.setState("CPU数[" + domainInfo.getMem() + "]");

            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (Integer.parseInt(domainInfo.getState()) == 0) {
                        if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("虚拟机[" + domainInfo.getName() + "]域 异常");
                            warningEvent.setId(new ObjectId(reportItem.getMonitorId().replace("-", "")));
                            warningService.deleteWarningEvent(warningEvent);
                            warningService.insertWarningEvent(warningEvent);
                        }

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        domainInfoStatusBean.setStatus(0);
                        domainInfoStatus.setStatus(0);
                    }
                });
            }
            domainInfoMap.putIfAbsent(domainInfo.getName(), domainInfoStatus);
        });
        domainInfoStatusBean.setMessage(domainInfoMap);
        RAIDMap.putIfAbsent("domainInfos", domainInfoStatusBean);

        ConcurrentHashMap<String, Status> volumeInfoMap = new ConcurrentHashMap<>();
        Status volumeInfoStatusBean = new Status();
        vmInfo.getVolumeInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(volumeInfo -> {
            Status volumeInfoStatus = new Status();

            //基本信息
            volumeInfoStatus.setMessage("虚拟机[" + volumeInfo.getName() + "]卷");
            volumeInfoStatus.setResult("等级[" + volumeInfo.getLevel() + "]");
            volumeInfoStatus.setState("磁盘数[" + volumeInfo.getNumDisks() + "]");

            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (Integer.parseInt(volumeInfo.getStatus()) == 0) {
                        if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
                            warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                            warningEvent.setEventDetail("虚拟机[" + volumeInfo.getName() + "]卷 异常");
                            warningEvent.setId(new ObjectId(reportItem.getMonitorId().replace("-", "")));
                            warningService.deleteWarningEvent(warningEvent);
                            warningService.insertWarningEvent(warningEvent);
                        }

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        volumeInfoStatusBean.setStatus(0);
                        volumeInfoStatus.setStatus(0);
                    }
                });
            }
            volumeInfoMap.putIfAbsent(volumeInfo.getId(), volumeInfoStatus);
        });
        volumeInfoStatusBean.setMessage(volumeInfoMap);
        RAIDMap.putIfAbsent("volumeInfos", volumeInfoStatusBean);

        Status controllerInfoStatusBean = new Status();
        controllerInfoStatusBean.setMessage(vmInfo.getControllerInfos());
        RAIDMap.putIfAbsent("controllerInfos", controllerInfoStatusBean);

        monitorItemType.setMessage(RAIDMap);

        if (logger.isDebugEnabled()) {
            logger.debug("RaidHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.RAID, this);
    }
}