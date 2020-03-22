package com.copsec.monitor.web.beans.monitor;

import com.alibaba.fastjson.JSONArray;
import com.copsec.monitor.web.config.Resources;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MonitorGroupBean {
    private String id = UUID.randomUUID().toString();//分组标识
    private String name;//分组名称
    private List monitorItems;//监控项

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();
        builder.append(this.id+ Resources.SPLITER);
        builder.append(this.name+Resources.SPLITER);
        builder.append(JSONArray.toJSONString(this.monitorItems));
        return builder.toString();
    }
}
