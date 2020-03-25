package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.node.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorItemListPools {
    private static MonitorItemListPools pool;
    //    private static ConcurrentHashMap<String, List<Status>> map;
    private static ConcurrentHashMap<String, List<Status>> map;

    private MonitorItemListPools() {

    }

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

    public synchronized ConcurrentHashMap<String, List<Status>> getMap() {
        ConcurrentHashMap<String, List<Status>> map = new ConcurrentHashMap<>();
        for (MonitorItemEnum e : MonitorItemEnum.values()) {
            List<Status> list = new ArrayList<>();
            map.putIfAbsent(e.name(), list);
        }
        return map;
    }
//    public synchronized ConcurrentHashMap<String, List<Status>> getMap() {
//        map = new ConcurrentHashMap<>();
//        return map;
//    }

    public synchronized void add(String key, List<Status> bean) {
        map.putIfAbsent(key, bean);
    }

    public synchronized List<Status> get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    public synchronized void update(String key, List<Status> value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
}
