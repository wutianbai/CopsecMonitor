package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.snmp.OidMapBean;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.google.common.collect.Maps;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SnmpMibPools {

    private static SnmpMibPools pool;

    private ConcurrentHashMap<NetworkType, Map<String, OidMapBean>> map = new ConcurrentHashMap<>();

    private HashMap<String, String> typeMaps = Maps.newHashMap();

    private SnmpMibPools() {
    }

    public static synchronized SnmpMibPools getInstances() {
        if (pool == null) {
            synchronized (SnmpMibPools.class) {
                if (pool == null) {
                    pool = new SnmpMibPools();
                }
            }
        }
        return pool;
    }

    public void add(NetworkType type, Map<String, OidMapBean> list) {
        if (!map.containsKey(type)) {
            map.putIfAbsent(type, list);
        }
    }

    public Map<String, OidMapBean> getMibsByType(NetworkType type) {
        return map.get(type);
    }

    public Map<NetworkType, Map<String, OidMapBean>> getAll() {
        return map;
    }

    public void addTypeNames(String type, String name) {
        if (!typeMaps.containsKey(type)) {
            typeMaps.put(type, name);
        }
    }

    public Map<String, String> getTypeNames() {
        return typeMaps;
    }
}
