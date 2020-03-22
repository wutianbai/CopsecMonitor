package com.copsec.monitor.web.handler;

import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;

import java.util.List;
import java.util.Optional;

public interface TaskHistoryStatusHandler {

    void handler(SyslogMessageBean syslogMessageBean);

    default boolean canSave(List<String> properties, List<String> instancesNames) {
        Optional<String> optionalS = properties.stream().filter(str -> {
            if (str.startsWith("instanceName")) {
                return true;
            }
            return false;
        }).findFirst();
        if (optionalS.isPresent()) {
            if (instancesNames.contains(optionalS.get().split("=")[1])) {
                return true;
            }
        }
        return false;
    }
}
