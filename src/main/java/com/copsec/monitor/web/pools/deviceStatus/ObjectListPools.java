package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.node.Status;

import java.util.concurrent.ConcurrentHashMap;

public class ObjectListPools {
    private static ObjectListPools pool;
    private static ConcurrentHashMap<String, Status> map;

    public static synchronized ObjectListPools getInstances() {
        if (pool == null) {
            synchronized (ObjectListPools.class) {
                if (pool == null) {
                    pool = new ObjectListPools();
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
