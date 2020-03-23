package com.copsec.monitor.web.handler.ReportItemHandler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImServiceHandlerImpl implements ReportHandler {
    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> IMMap = new ConcurrentHashMap<>();
        JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
        for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
            Status statusBean = new Status();
            JSONObject next = (JSONObject) iterator.next();
            if (next.getString("message").equalsIgnoreCase("已停止")) {
                warningEvent.setEventDetail("IM进程[" + next.getString("processorName") + "]已停止");
                warningEvent.setId(null);
                warningService.insertWarningEvent(warningEvent);

                monitorType.setStatus(0);
                monitorItemType.setStatus(0);
                statusBean.setStatus(0);
                statusBean.setMessage(warningEvent.getEventDetail());
            } else {
                statusBean.setMessage("IM进程[" + next.getString("processorName") + "]正在运行");
            }
            IMMap.putIfAbsent(next.getString("processorName"), statusBean);
        }
        monitorItemType.setMessage(IMMap);

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.IMSERVICE, this);
    }
}
