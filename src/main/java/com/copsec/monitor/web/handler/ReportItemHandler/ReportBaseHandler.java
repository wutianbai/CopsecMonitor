package com.copsec.monitor.web.handler.ReportItemHandler;

import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.pools.WarningItemPools;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportBaseHandler {

//    public static void baseHandle(WarningService warningService,ReportItem reportItem, WarningEvent warningEvent) {
//        warningEvent.setEventDetail(reportItem.getResult().toString());
//        warningService.insertWarningEvent(warningEvent);
//    }

    public static List<WarningItemBean> getWarningItemList(ReportItem reportItem) {
        List<WarningItemBean> warningItemList = WarningItemPools.getInstances().getAll();//所有告警项
        List<WarningItemBean> list = new ArrayList<>();
        warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
            String[] monitorIds = warningItem.getMonitorIds().split(Resources.SPLITE, -1);
            if (new ArrayList<>(Arrays.asList(monitorIds)).contains(reportItem.getMonitorId())) {
                list.add(warningItem);
            }
        });
        return list;
    }
}
