package com.copsec.monitor.web.service;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.commons.CopsecResult;

import java.util.List;

public interface MonitorService {

    CopsecResult getAllMonitorItem();

    CopsecResult getMonitorItemByDeviceId(String deviceId);

    CopsecResult addMonitorItem(UserBean userInfo, String ip, MonitorItemBean bean, String filePath);

    CopsecResult updateMonitorItem(UserBean userInfo, String ip, MonitorItemBean bean, String filePath);

    CopsecResult deleteMonitorItem(UserBean userInfo, String ip, String monitorId, String filePath);

    CopsecResult deleteMonitorItemList(UserBean userInfo, String ip, List<String> idArray, String filePath);

    CopsecResult getAllMonitorGroup();

    CopsecResult addMonitorGroup(UserBean userInfo, String ip, MonitorGroupBean bean, String filePath);

    CopsecResult updateMonitorGroup(UserBean userInfo, String ip, MonitorGroupBean bean, String filePath);

    CopsecResult deleteMonitorGroup(UserBean userInfo, String ip, String id, String filePath);

    CopsecResult deleteMonitorGroupList(UserBean userInfo, String ip, List<String> idArray, String filePath);

    CopsecResult getAllWarningItem();

    CopsecResult addWarningItem(UserBean userInfo, String ip, WarningItemBean bean, String filePath);

    CopsecResult updateWarningItem(UserBean userInfo, String ip, WarningItemBean bean, String filePath);

    CopsecResult deleteWarningItem(UserBean userInfo, String ip, String id, String filePath);

    CopsecResult deleteWarningItemList(UserBean userInfo, String ip, List<String> idArray, String filePath);

    CopsecResult getAllMonitorTask();

    CopsecResult addMonitorTask(UserBean userInfo, String ip, MonitorTaskBean bean, String filePath);

    CopsecResult updateMonitorTask(UserBean userInfo, String ip, MonitorTaskBean bean, String filePath);

    CopsecResult deleteMonitorTask(UserBean userInfo, String ip, String id, String filePath);

    CopsecResult deleteMonitorTaskList(UserBean userInfo, String ip, List<String> idArray, String filePath);
}
