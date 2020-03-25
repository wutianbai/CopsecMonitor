package com.copsec.monitor.web.beans.monitor;

import com.alibaba.fastjson.JSON;
import lombok.Data;

@Data
public class LogConfig {
    private String logPath = "";//日志路径
    private String threadHold = "";//阈值

    @Override
    public String toString() {
        return JSON.toJSONString(this, false);
    }
}
