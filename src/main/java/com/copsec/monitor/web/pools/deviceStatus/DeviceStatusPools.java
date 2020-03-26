package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.node.Status;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceStatusPools {
    private static DeviceStatusPools pool;
    private static ConcurrentHashMap<String, Status> map = new ConcurrentHashMap<>();

    private DeviceStatusPools() {

    }

    public static synchronized DeviceStatusPools getInstances() {
        if (pool == null) {
            synchronized (DeviceStatusPools.class) {
                if (pool == null) {
                    pool = new DeviceStatusPools();
                }
            }
        }
        return pool;
    }

    public synchronized ConcurrentHashMap<String, Status> getMap() {
        return map;
    }

    public synchronized void update(String key, Status value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
}
