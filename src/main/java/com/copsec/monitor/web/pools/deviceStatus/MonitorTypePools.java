package com.copsec.monitor.web.pools.deviceStatus;

import com.copsec.monitor.web.beans.node.Status;

import java.util.concurrent.ConcurrentHashMap;

public class MonitorTypePools {
    private static MonitorTypePools pool;
    //    private static ConcurrentHashMap<String, Status> map ;
    private static ConcurrentHashMap<String, Status> map;

    private MonitorTypePools() {

    }

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

//    public synchronized ConcurrentHashMap<String, Status> getMap() {
//        map = new ConcurrentHashMap<>();
//            for (MonitorTypeEnum e : MonitorTypeEnum.values()) {
//                Status status = new Status();
//
////                ConcurrentHashMap<String, List<Status>> monitorItemListMap = new ConcurrentHashMap<>();
////                for (MonitorItemEnum i : MonitorItemEnum.values()) {
////                    List<Status> list = new ArrayList<>();
////                    monitorItemListMap.putIfAbsent(i.name(), list);
////                }
//
////                status.setMessage(monitorItemListMap);
//                map.putIfAbsent(e.name(), status);
//            }
//        return map;
//    }

    public synchronized ConcurrentHashMap<String, Status> getMap() {
        map = new ConcurrentHashMap<>();
        return map;
    }

    public synchronized void add(String key, Status bean) {
        map.putIfAbsent(key, bean);
    }

    public synchronized Status get(String key) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return null;
    }

    public synchronized void update(String key, Status value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }
}
