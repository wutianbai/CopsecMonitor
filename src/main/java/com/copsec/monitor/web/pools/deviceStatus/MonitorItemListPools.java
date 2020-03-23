package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.node.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorItemListPools {
    private static MonitorItemListPools pool;
    private static ConcurrentHashMap<String, List<Status>> map;

    public static synchronized MonitorItemListPools getInstances() {
        if (pool == null) {
            synchronized (MonitorItemListPools.class) {
                if (pool == null) {
                    pool = new MonitorItemListPools();
                }
            }
        }
        return pool;
    }

    public ConcurrentHashMap<String, List<Status>> getMap() {
        map = new ConcurrentHashMap<>();
        synchronized (this) {
            for (MonitorItemEnum e : MonitorItemEnum.values()) {
                map.putIfAbsent(e.name(), new ArrayList<>());
            }
        }
        return map;
    }
}
