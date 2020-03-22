package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.node.Status;

import java.util.concurrent.ConcurrentHashMap;

public class DeviceStatusPools {
    private static DeviceStatusPools pool;
    private static ConcurrentHashMap<String, ConcurrentHashMap<String, Status>> map = new ConcurrentHashMap<>();

    private DeviceStatusPools() {
//        synchronized (this) {
//            for (MonitorItemEnum e : MonitorItemEnum.values()) {
//                map.putIfAbsent(e.name(), new ObjectListPools());
//            }
//            map.putIfAbsent("CPU", new ObjectListPools());
//            map.putIfAbsent("DISK", new ObjectListPools());
//            map.putIfAbsent("MEMORY", new ObjectListPools());
//            map.putIfAbsent("USER", new ObjectListPools());
//            map.putIfAbsent("SYSTEMTYPE", new ObjectListPools());
//            map.putIfAbsent("SYSTEMVERSION", new ObjectListPools());
//            map.putIfAbsent("SYSTEMPATCH", new ObjectListPools());
//            map.putIfAbsent("APPLICATION", new ObjectListPools());
//            map.putIfAbsent("INSTANCES_WEB70", new ObjectListPools());
//            map.putIfAbsent("INSTANCES_WEBPROXY40", new ObjectListPools());
//            map.putIfAbsent("INSTANCES_CONFIG", new ObjectListPools());
//            map.putIfAbsent("INSTANCES_USER", new ObjectListPools());
//            map.putIfAbsent("NETWORK", new ObjectListPools());
//            map.putIfAbsent("ACCESSLOG", new ObjectListPools());
//            map.putIfAbsent("PROXYLOG", new ObjectListPools());
//            map.putIfAbsent("CERT70", new ObjectListPools());
//            map.putIfAbsent("CERT40", new ObjectListPools());
//            map.putIfAbsent("IMSERVICE", new ObjectListPools());
//            map.putIfAbsent("RAID", new ObjectListPools());
//        }
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

    public ConcurrentHashMap<String, ConcurrentHashMap<String, Status>> getPools() {
        return map;
    }

    public synchronized void update(String key, ConcurrentHashMap<String, Status> value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
}
