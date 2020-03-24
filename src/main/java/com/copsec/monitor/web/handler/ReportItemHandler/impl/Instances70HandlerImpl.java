package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
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

/**
 * 读取server.xml配置文件获取pid信息和listener信息
 */
@Component
public class Instances70HandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(Instances70HandlerImpl.class);

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        JSONObject jSONObject = JSON.parseObject(reportItem.getResult().toString());
        //基本信息
        monitorItemType.setMessage(reportItem.getItem());
        monitorItemType.setResult(jSONObject.getString("ports"));

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);
        if (warningItemList.size() > 0) {
            warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                if (Integer.parseInt(jSONObject.getString("status")) == 0) {
                    warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                    warningEvent.setEventDetail("WEB70实例[" + reportItem.getItem() + "]" + jSONObject.getString("ports") + jSONObject.getString("message"));
                    warningEvent.setId(null);
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                }
            });
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Instances70Handler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.INSTANCES_WEB70, this);
    }
}
