package com.copsec.monitor.web.beans.monitor;

import com.alibaba.fastjson.JSONArray;
import com.copsec.monitor.web.config.Resources;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MonitorTaskBean {
    private String taskId = UUID.randomUUID().toString();//监控任务唯一标识
    private String taskName;//监控任务名称
    private String deviceId;//设备唯一标识符
    private String groupId;//监控项分组id
    private List warningItems;//告警项集合

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.taskId+ Resources.SPLITER);
        builder.append(this.taskName+Resources.SPLITER);
        builder.append(this.deviceId+Resources.SPLITER);
        builder.append(this.groupId+Resources.SPLITER);
        builder.append(JSONArray.toJSONString(this.warningItems));
        return builder.toString();
    }
}
