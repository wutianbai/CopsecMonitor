package com.copsec.monitor.web.pools;

import com.copsec.monitor.web.beans.node.Device;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class DevicePools {
    private static DevicePools pools;

    private DevicePools() {
    }

    private static Map<String, Device> map = Maps.newConcurrentMap();

    public synchronized static DevicePools getInstance() {
        if (pools == null) {
            synchronized (DevicePools.class) {
                if (pools == null) {
                    pools = new DevicePools();
                }
            }
        }
        return pools;
    }

    public synchronized Device getDevice(String deviceId) {
        if (map.containsKey(deviceId)) {
            return map.get(deviceId);
        }
        return null;
    }

    public synchronized void addDevice(Device device) {
        map.putIfAbsent(device.getData().getDeviceId(), device);
    }

    public synchronized void updateDevice(Device device) {
        if (map.containsKey(device.getData().getDeviceId())) {
            map.replace(device.getData().getDeviceId(), device);
        }
    }

    public synchronized void delete(String deviceId) {
        if (map.containsKey(deviceId)) {
            map.remove(deviceId);
        }
    }

    public synchronized List<Device> getAll() {
        ArrayList<Device> list = new ArrayList<>();
        map.forEach((k, v) -> list.add(v));
        return list;
    }

    public List<Device> getAllDevice(){
        List<Device> list = map.entrySet().stream().map(Map.Entry::getValue).collect(Collectors.toList());
        return list;
    }

    /**
     * 清空
     */
    public synchronized void clean() {
        map.clear();
    }
}

