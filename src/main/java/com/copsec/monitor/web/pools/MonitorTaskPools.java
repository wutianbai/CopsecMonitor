package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorTaskPools {
    private static MonitorTaskPools pool;
    private static ConcurrentHashMap<String, MonitorTaskBean> map = new ConcurrentHashMap<>();

    private MonitorTaskPools() {
    }

    public static synchronized MonitorTaskPools getInstances() {
        if (pool == null) {
            synchronized (MonitorTaskPools.class) {
                if (pool == null) {
                    pool = new MonitorTaskPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(MonitorTaskBean bean) {
        map.putIfAbsent(bean.getTaskId(), bean);
    }

    public synchronized void add(List<MonitorTaskBean> beanList) {
        for (MonitorTaskBean bean : beanList) {
            map.putIfAbsent(bean.getTaskId(), bean);
        }
    }

    public synchronized MonitorTaskBean get(String monitorId) {
        if (map.containsKey(monitorId)) {
            return map.get(monitorId);
        }
        return null;
    }

    public synchronized MonitorTaskBean getByDeviceId(String deviceId) {
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            MonitorTaskBean monitorTaskBean = (MonitorTaskBean) entry.getValue();
            if (deviceId.equalsIgnoreCase(monitorTaskBean.getDeviceId())) {
                return monitorTaskBean;
            }
        }
        return null;
    }

    public synchronized List<MonitorTaskBean> get(List<String> idArray) {
        List<MonitorTaskBean> list = new ArrayList<>();
        for (String id : idArray) {
            if (map.containsKey(id)) {
                list.add(map.get(id));
                return list;
            }
        }
        return null;
    }

    public synchronized void update(MonitorTaskBean bean) {
        if (map.containsKey(bean.getTaskId())) {
            map.replace(bean.getTaskId(), bean);
        } else {
            map.put(bean.getTaskId(), bean);
        }
    }

    public synchronized void delete(String monitorId) {
        if (map.containsKey(monitorId)) {
            map.remove(monitorId, map.get(monitorId));
        }
    }

    public synchronized void delete(List<String> idArray) {
        for (String id : idArray) {
            if (map.containsKey(id)) {
                map.remove(id, map.get(id));
            }
        }
    }

    public synchronized List<MonitorTaskBean> getAll() {
        ArrayList<MonitorTaskBean> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void clean() {
        map.clear();
    }
}
