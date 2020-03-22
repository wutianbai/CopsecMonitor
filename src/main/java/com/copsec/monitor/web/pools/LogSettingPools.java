package com.copsec.monitor.web.pools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.copsec.monitor.web.beans.syslogConf.LogSettingBean;
import com.google.common.collect.Maps;

public class LogSettingPools {

    private static LogSettingPools pool = null;
    private static HashMap<String, LogSettingBean> map = Maps.newHashMap();

    private LogSettingPools() {
    }

    public static synchronized LogSettingPools getInstances() {
        if (pool == null) {
            synchronized (LogSettingPools.class) {
                if (pool == null) {
                    pool = new LogSettingPools();
                }
            }
        }
        return pool;
    }

    public void update(LogSettingBean bean) {
        if (map.containsKey(bean.getLogType())) {
            map.replace(bean.getLogType(), bean);
        } else {
            map.putIfAbsent(bean.getLogType(), bean);
        }
    }

    public HashMap<String, LogSettingBean> getAllSetting() {
        return map;
    }

    public List<String> getAllEnableLogTypes() {
        List<String> list = map.entrySet().stream().filter(d -> {
            if (d.getValue().isEnable()) {
                return true;
            }
            return false;
        }).map(Map.Entry::getKey).collect(Collectors.toList());
        return list;
    }

    public List<LogSettingBean> getAllLogSettings() {
        List<LogSettingBean> list = map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        return list;
    }

    public void update(List<String> logTypes) {
        map.entrySet().stream().filter(d -> {
            if (logTypes.contains(d.getKey())) {
                return true;
            }
            return false;
        }).forEach(entry -> {
            LogSettingBean bean = entry.getValue();
            bean.setEnable(true);
            update(bean);
        });
    }
}
