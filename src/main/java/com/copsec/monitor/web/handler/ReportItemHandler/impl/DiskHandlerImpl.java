package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DiskHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(DiskHandlerImpl.class);

    @Autowired
    private SystemConfig config;

    public Status handle(Status deviceStatus, Device device, UserInfoBean userInfo, WarningService warningService, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();

        ConcurrentHashMap<String, Status> map = new ConcurrentHashMap<>();

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);

        JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
        for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
            JSONObject next = (JSONObject) iterator.next();
            Status statusBean = new Status();
            //状态基本信息
            statusBean.setMessage(next.getString("disk"));
            statusBean.setResult(next.getString("total"));
            statusBean.setState(next.getString("used") + Resources.PERCENTAGE);

            //发送SysLog日志
            SysLogUtil.sendLog(config, device.getData().getDeviceIP(), device.getData().getDeviceHostname(), "磁盘", "[" + next.getString("disk") + "]" + reportItem.getItem() + "总量[" + next.getString("total") + "]" + "使用率[" + next.getString("used") + Resources.PERCENTAGE + "]");

            if (reportItem.getStatus() == 0) {//信息异常
                baseHandle(deviceStatus, monitorType, monitorItemType);
                statusBean.setStatus(0);

                WarningEvent warningEvent = makeWarningEvent(reportItem, device, userInfo);
                if (warningItemList.size() > 0) {
                    warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                        if (warningItem.getWarningLevel().name().equals("NORMAL")) {
                            deviceStatus.setStatus(1);
                            monitorType.setStatus(1);
                            monitorItemType.setStatus(1);
                        } else {
                            if (warningItem.getThreadHold() < Integer.parseInt(next.getString("used"))) {
                                warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                                warningEvent.setEventDetail("[" + next.getString("disk") + "]" + reportItem.getItem() + "[异常]" + "总量[" + next.getString("total") + "]" + "使用率[" + next.getString("used") + Resources.PERCENTAGE + "]" + "超出阈值[" + warningItem.getThreadHold() + Resources.PERCENTAGE + "]");
                                generateWarningEvent(warningService, reportItem, warningEvent);
                            }
                        }
                    });
                } else {
                    warningEvent.setEventDetail("[" + next.getString("disk") + "]" + reportItem.getItem() + "[异常]" + "总量[" + next.getString("total") + "]" + "使用率[" + next.getString("used") + Resources.PERCENTAGE + "]");
                    generateWarningEvent(warningService, reportItem, warningEvent);
                }
            }

            map.put(next.getString("disk"), statusBean);
        }
        monitorItemType.setMessage(map);

        if (logger.isDebugEnabled()) {
            logger.debug("DiskHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.DISK, this);
    }
}
