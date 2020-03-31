package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorItemPools {
    private static MonitorItemPools pool;
    private static ConcurrentHashMap<String, MonitorItemBean> map = new ConcurrentHashMap<>();

    private MonitorItemPools() {
    }

    public static synchronized MonitorItemPools getInstances() {
        if (pool == null) {
            synchronized (MonitorItemPools.class) {
                if (pool == null) {
                    pool = new MonitorItemPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(MonitorItemBean bean) {
        map.putIfAbsent(bean.getMonitorId(), bean);
    }

    public synchronized void add(List<MonitorItemBean> beanList) {
        beanList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && !ObjectUtils.isEmpty(d.getMonitorId()))
                .forEach((bean) -> map.putIfAbsent(bean.getMonitorId(), bean));
    }

    public synchronized MonitorItemBean get(String monitorId) {
        if (map.containsKey(monitorId)) {
            return map.get(monitorId);
        }
        return null;
    }

    public synchronized List<MonitorItemBean> get(List<String> idArray) {
        List<MonitorItemBean> list = new ArrayList<>();
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> list.add(map.get(id)));
        return list;
    }

    public synchronized void update(MonitorItemBean bean) {
        if (map.containsKey(bean.getMonitorId())) {
            map.replace(bean.getMonitorId(), bean);
        } else {
            map.put(bean.getMonitorId(), bean);
        }
    }

    public synchronized void delete(String monitorId) {
        if (map.containsKey(monitorId)) {
            map.remove(monitorId, map.get(monitorId));
        }
    }

    public synchronized void delete(List<String> idArray) {
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> map.remove(id, map.get(id)));
    }

    public synchronized List<MonitorItemBean> getAll() {
        ArrayList<MonitorItemBean> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void clean() {
        map.clear();
    }
}
