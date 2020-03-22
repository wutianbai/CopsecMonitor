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

    private String monitorUserId;

    private Date reportTime;

    private Date requestTime;

    private String parent;//网络区域父节点标识Id

    private String id;

    private String name;

    private String source;

    private String target;

    private String[] targets;

    private String backgroundColor;

//    public Data(String id, String source, String target) {
//        this.id = id;
//        this.source = source;
//        this.target = target;
//    }
//    private String id;
//
//    private String name;
//
//    private String source;
//
//    private String target;
//
//    /**
//     * 网络区域父节点标识Id
//     */
//    private String parent;
//
//    /**
//     * 双主机设备
//     */
//    private boolean hasSub = false;
//
//    private String innerDeviceId = "N/A";
//
//    private boolean useSnmp = false;
//
//    /**
//     * 网络区域节点
//     */
//    private String zone = "no";
//
//    private String backgroundColor;
//
//    /**
//     * 单向设备-linux ,网络审计-networkAudit，防火墙-firewall 用于表示snmp获取方式
//     */
//    private String deviceType;
//
//    private boolean pingCheck = false;
//
//    private String checkIp;
//
//    private String serverType = "kebo";
//
//    @JSONField(serialize = false)
//    private SnmpConfigBean config = null;

    @Override
    public String toString() {
        return JSON.toJSONString(this, false);
    }
}
