package com.copsec.monitor.web.handler.ReportItemHandler;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.pools.WarningItemPools;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportBaseHandler {
    public static WarningEvent makeWarningEvent(ReportItem reportItem, Device device, UserInfoBean userInfo) {
        WarningEvent warningEvent = new WarningEvent();
        warningEvent.setMonitorId(reportItem.getMonitorId());
        warningEvent.setEventSource(reportItem.getMonitorItemType());
        warningEvent.setEventType(WarningLevel.WARNING);//初始告警级别
        warningEvent.setEventTime(new Date());
        warningEvent.setDeviceId(device.getData().getDeviceId());
        warningEvent.setDeviceName(device.getData().getDeviceHostname());

        if (!ObjectUtils.isEmpty(userInfo)) {
            warningEvent.setUserId(userInfo.getUserId());
            warningEvent.setUserName(userInfo.getUserName());
            warningEvent.setUserMobile(userInfo.getMobile());
        }
        return warningEvent;
    }

    public static void baseHandle(Status deviceStatus, Status monitorType, Status monitorItemType) {
        deviceStatus.setStatus(0);
        monitorType.setStatus(0);
        monitorItemType.setStatus(0);
    }

    public static void generateWarningEvent(WarningService warningService, ReportItem reportItem, WarningEvent warningEvent) {
        if (!warningService.checkIsWarningByTime(reportItem.getMonitorId())) {
            warningService.insertWarningEvent(warningEvent);
        }
    }

    public static List<WarningItemBean> getWarningItemList(ReportItem reportItem) {
        List<WarningItemBean> warningItemList = WarningItemPools.getInstances().getAll();//所有告警项
        List<WarningItemBean> list = new ArrayList<>();
        warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
            List<String> monitorIds = Arrays.asList(warningItem.getMonitorIds().split(Resources.SPLITE, -1));
            if (monitorIds.contains(reportItem.getMonitorId())) {
                list.add(warningItem);
            }
        });
        return list;
    }
}
