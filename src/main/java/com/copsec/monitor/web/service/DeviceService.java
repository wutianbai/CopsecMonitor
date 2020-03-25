package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.LinkBean;
import com.copsec.monitor.web.beans.node.PositionBeans;
import com.copsec.monitor.web.commons.CopsecResult;

import java.util.ArrayList;

public interface DeviceService {

    CopsecResult getData();

    CopsecResult addDevice(UserBean userInfo, String ip, Device device);

    CopsecResult getDevice(UserBean userInfo, String ip, String deviceId);

    CopsecResult updateDevice(UserBean userInfo, String ip, Device device);

    CopsecResult deleteDevice(UserBean userInfo, String ip, String deviceId);

    CopsecResult addLink(UserBean userInfo, String ip, LinkBean link);

    CopsecResult updateLink(UserBean userInfo, String ip, LinkBean link);

    CopsecResult deleteLink(UserBean userInfo, String ip, String linkId);

    CopsecResult getAllZone();

    CopsecResult addZone(UserBean userInfo, String ip, Device zone);

    CopsecResult updateZone(UserBean userInfo, String ip, Device zone);

    CopsecResult deleteZone(UserBean userInfo, String ip, String zoneId);

    CopsecResult updateTopology(ArrayList<PositionBeans> positions);

    /**
     * 获取当前设备状态信息，如果没有对应设备信息，则显示该设备未激活，
     * 计算updatetime，查看设备上报信息状态是否正常
     *
     * @return
     */
    CopsecResult getDeviceStatus();

}
