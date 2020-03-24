package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.node.Status;

import java.util.concurrent.ConcurrentHashMap;

public class MonitorTypePools {
    private static MonitorTypePools pool;
    private static ConcurrentHashMap<String, Status> map;

    public static synchronized MonitorTypePools getInstances() {
        if (pool == null) {
            synchronized (MonitorTypePools.class) {
                if (pool == null) {
                    pool = new MonitorTypePools();
                }
            }
        }
        return pool;
    }

    public ConcurrentHashMap<String, Status> getMap() {
        map = new ConcurrentHashMap<>();
        synchronized (this) {
            for (MonitorTypeEnum e : MonitorTypeEnum.values()) {
                map.putIfAbsent(e.name(), new Status());
            }
        }
        return map;
    }
}
