package com.copsec.monitor.web.beans.node;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@lombok.Data
public class Data {

    private String zone = "no";//区分设备\网络区域标志
    private String deviceId;
    private String deviceHostname;
    private String deviceType;
    private String deviceIP;
    private String monitorUserId = "";
    private Date reportTime;
    private Date requestTime;
    private String parent;//网络区域父节点标识Id
    private String id;
    private String name;
    private String source;
    private String target;
    private String[] targets;
    private String backgroundColor;
    @Override
    public String toString() {
        return JSON.toJSONString(this, false);
    }
}
