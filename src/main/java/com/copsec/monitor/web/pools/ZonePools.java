package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.node.Device;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ZonePools {
    private static ZonePools pools;

    private ZonePools() {
    }

    private static Map<String, Device> map = Maps.newConcurrentMap();

    public synchronized static ZonePools getInstance() {
        if (pools == null) {
            synchronized (ZonePools.class) {
                if (pools == null) {
                    pools = new ZonePools();
                }
            }
        }
        return pools;
    }

    public synchronized List<Device> getAll() {
        ArrayList<Device> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public synchronized void add(Device zone) {
        map.putIfAbsent(zone.getData().getId(), zone);
    }

    public synchronized Device get(String zoneId) {
        if (map.containsKey(zoneId)) {
            return map.get(zoneId);
        }
        return null;
    }

    public synchronized void update(Device zone) {
        if (map.containsKey(zone.getData().getId())) {
            map.replace(zone.getData().getId(), zone);
        }
    }

    public synchronized void delete(String zoneId) {
        if (map.containsKey(zoneId)) {
            map.remove(zoneId);
        }
    }

    /**
     * 清空
     */
    public synchronized void clean() {
        map.clear();
    }
}

