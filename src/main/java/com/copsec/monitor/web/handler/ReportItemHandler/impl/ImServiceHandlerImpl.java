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
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.service.WarningService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ImServiceHandlerImpl extends ReportBaseHandler implements ReportHandler {

    private static final Logger logger = LoggerFactory.getLogger(ImServiceHandlerImpl.class);

    private WarningService warningService = SpringContext.getBean(WarningService.class);

    public Status handle(WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        Status monitorItemType = new Status();
        ConcurrentHashMap<String, Status> map = new ConcurrentHashMap<>();

        List<WarningItemBean> warningItemList = getWarningItemList(reportItem);

        JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
        for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
            JSONObject next = (JSONObject) iterator.next();
            Status statusBean = new Status();

            //基本信息
            statusBean.setMessage("IM进程[" + next.getString("processorName") + "]");

            if (warningItemList.size() > 0) {
                warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                    if (next.getString("message").equalsIgnoreCase("已停止")) {
                        warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                        warningEvent.setEventDetail("IM进程[" + next.getString("processorName") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                    }
                });
            }
            map.put(next.getString("processorName"), statusBean);
        }
        monitorItemType.setMessage(map);

        if (logger.isDebugEnabled()) {
            logger.debug("ImServiceHandler return {}" + monitorItemType);
        }

        return monitorItemType;
    }

    @PostConstruct
    public void init() {
        ReportHandlerPools.getInstance().registerHandler(MonitorItemEnum.IMSERVICE, this);
    }
}
