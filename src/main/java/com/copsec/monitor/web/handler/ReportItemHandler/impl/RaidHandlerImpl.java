package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.beans.warning.VmInfoBean;
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
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RaidHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(RaidHandlerImpl.class);

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
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

            //发送SysLog日志
            SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "虚拟机", "虚拟机[" + diskInfo.getId() + "]磁盘 " + "容量[" + diskInfo.getSize() + "]" + "剩余空间[" + diskInfo.getSpare() + "]");

            if (reportItem.getStatus() == 0) {
                baseHandle(deviceStatus, monitorType, monitorItemType);
                diskInfoStatusBean.setStatus(0);
                diskInfoStatus.setStatus(0);

                WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
                warningEvent.setEventDetail("虚拟机[" + diskInfo.getId() + "]磁盘 异常");
                if (warningItemList.size() > 0) {
                    warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                        if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                            monitorType.setStatus(1);
                            monitorItemType.setStatus(1);
                        } else {
                            if (Integer.parseInt(diskInfo.getStatus()) == 0) {
                                warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                                generateWarningEvent(warningService, reportItem, warningEvent);
                            }
                        }
                    });
                } else {
                    generateWarningEvent(warningService, reportItem, warningEvent);
                }
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

            //发送SysLog日志
            SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "虚拟机", "虚拟机[" + domainInfo.getName() + "]域 " + "使用率[" + domainInfo.getUtil() + "]" + "CPU数[" + domainInfo.getMem() + "]");

            if (reportItem.getStatus() == 0) {
                baseHandle(deviceStatus, monitorType, monitorItemType);
                domainInfoStatusBean.setStatus(0);
                domainInfoStatus.setStatus(0);

                WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
                warningEvent.setEventDetail("虚拟机[" + domainInfo.getName() + "]域 异常");
                if (warningItemList.size() > 0) {
                    warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                        if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                            monitorType.setStatus(1);
                            monitorItemType.setStatus(1);
                        } else {
                            if (Integer.parseInt(domainInfo.getState()) == 0) {
                                warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                                generateWarningEvent(warningService, reportItem, warningEvent);
                            }
                        }
                    });
                } else {
                    generateWarningEvent(warningService, reportItem, warningEvent);
                }
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

            //发送SysLog日志
            SysLogUtil.sendLog(device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "虚拟机", "虚拟机[" + volumeInfo.getName() + "]卷 " + "等级[" + volumeInfo.getLevel() + "]" + "磁盘数[" + volumeInfo.getNumDisks() + "]");

            if (reportItem.getStatus() == 0) {
                baseHandle(deviceStatus, monitorType, monitorItemType);
                volumeInfoStatusBean.setStatus(0);
                volumeInfoStatus.setStatus(0);

                WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
                warningEvent.setEventDetail("虚拟机[" + volumeInfo.getName() + "]卷 异常");
                if (warningItemList.size() > 0) {
                    warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                        if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                            monitorType.setStatus(1);
                            monitorItemType.setStatus(1);
                        } else {
                            if (Integer.parseInt(volumeInfo.getStatus()) == 0) {
                                warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                                generateWarningEvent(warningService, reportItem, warningEvent);
                            }
                        }
                    });
                } else {
                    generateWarningEvent(warningService, reportItem, warningEvent);
                }
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
