package com.copsec.monitor.web.beans.node;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class Status {

    private int status = 1;

    private Object message;

    private String deviceId;

//    private String cpuUseRate;
//
//    private String memoryUseRate;
//
//    private String netSpeed;
//
//    private String deviceStatus;

    private String warnMessage;

    private String updateTime;

    private String result;

    private String state;
}
