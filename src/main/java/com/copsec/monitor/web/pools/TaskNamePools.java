package com.copsec.monitor.web.pools;

import java.util.HashMap;
import java.util.List;

import com.copsec.monitor.web.config.Resources;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TaskNamePools {
    private static TaskNamePools pool = null;
    private static HashMap<String, String> map = Maps.newHashMap();

    private TaskNamePools() {
    }

    public synchronized static TaskNamePools getInstances() {
        if (pool == null) {
            synchronized (TaskNamePools.class) {

                if (pool == null) {
                    pool = new TaskNamePools();
                }
            }
        }
        return pool;
    }

    public void update(String key, String value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.putIfAbsent(key, value);
        }
    }

    public String getValues(String key) {
        return map.get(key);
    }

    public List<String> getAll() {
        List<String> list = Lists.newArrayList();
        map.entrySet().stream().forEach(entry -> {
            list.add(entry.getKey() + Resources.SPLITER + entry.getValue());
        });
        return list;
    }

    public void addAll(List<String> list) {
        list.stream().filter(d -> {
            if (d.split(Resources.SPLITER).length == 2) {
                return true;
            }
            return false;
        }).forEach(item -> {
            String[] attrs = item.split(Resources.SPLITER);
            update(attrs[0], attrs[1]);
        });
    }

    public HashMap<String, String> getMap() {
        return map;
    }

    public void deleteTaskName(String key) {
        map.remove(key);
    }
}
