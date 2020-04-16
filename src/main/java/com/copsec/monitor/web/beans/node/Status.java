package com.copsec.monitor.web.beans.node;

import lombok.Data;

@Data
public class Status {

    private int status = 1;

    private Object message;

    private String deviceId;

    private String warnMessage;

    private String updateTime;

    private String result;

    private String state;

    //证书信息
    private String nickname;//证书名称
    private String subject;//证书主体
    private String issuer;//颁发机构
    private String starTime;//起始时间
    private String endTime;//有效期时间
}
