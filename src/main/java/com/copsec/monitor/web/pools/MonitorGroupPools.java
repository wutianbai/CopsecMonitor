package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonitorGroupPools {
    private static MonitorGroupPools pool;
    private static ConcurrentHashMap<String, MonitorGroupBean> map = new ConcurrentHashMap<>();

    private MonitorGroupPools() {
    }

    public static synchronized MonitorGroupPools getInstances() {
        if (pool == null) {
            synchronized (MonitorGroupPools.class) {
                if (pool == null) {
                    pool = new MonitorGroupPools();
                }
            }
        }
        return pool;
    }

    public synchronized void add(MonitorGroupBean bean) {
        map.putIfAbsent(bean.getId(), bean);
    }

    public synchronized void add(List<MonitorGroupBean> beanList) {
        beanList.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && !ObjectUtils.isEmpty(d.getId()))
                .forEach((bean) -> map.putIfAbsent(bean.getId(), bean));
    }

    public synchronized MonitorGroupBean get(String id) {
        if (map.containsKey(id)) {
            return map.get(id);
        }
        return null;
    }

    public synchronized List<MonitorGroupBean> get(List<String> idArray) {
        List<MonitorGroupBean> list = new ArrayList<>();
        idArray.stream().
                filter(d -> !ObjectUtils.isEmpty(d) && map.containsKey(d))
                .forEach((id) -> list.add(map.get(id)));

        return list;
    }

    public synchronized void update(MonitorGroupBean bean) {
        if (map.containsKey(bean.getId())) {
            map.replace(bean.getId(), bean);
        } else {
            map.put(bean.getId(), bean);
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

    public synchronized List<MonitorGroupBean> getAll() {
        ArrayList<MonitorGroupBean> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void clean() {
        map.clear();
    }
}
