package com.copsec.monitor.web.handler.ReportItemHandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.copsec.monitor.SpringContext;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.CertInfoBean;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.beans.warning.VmInfoBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.WarningItemPools;
import com.copsec.monitor.web.pools.deviceStatus.DeviceStatusPools;
import com.copsec.monitor.web.pools.deviceStatus.ObjectListPools;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.service.WarningService;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ReportHandler {
    private WarningService warningService= SpringContext.getBean(WarningService.class);

    private DeviceService deviceService=SpringContext.getBean(DeviceService.class);

    public void handle(Report report) {
        Device device = DevicePools.getInstance().getDevice(report.getDeviceId());//设备信息
        //更新设备上报时间
        device.getData().setReportTime(report.getReportTime());
        deviceService.updateDevice(new UserBean(), "127.0.0.1", device);
        UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());//运维用户信息

        ConcurrentHashMap<String, Status> map = ObjectListPools.getInstances().getMap();//新建设备状态缓存
        List<ReportItem> reportItems = castList(report.getReportItems(), ReportItem.class);//获取上报项
        if (!ObjectUtils.isEmpty(reportItems)) {
            reportItems.parallelStream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(reportItem -> {

                WarningEvent warningEvent = new WarningEvent();
                warningEvent.setEventSource(reportItem.getMonitorItemType());
                warningEvent.setEventTime(new Date());
                warningEvent.setDeviceId(report.getDeviceId());
                warningEvent.setDeviceName(device.getData().getDeviceHostname());
                warningEvent.setUserId(userInfo.getUserId());
                warningEvent.setUserName(userInfo.getUserName());
                warningEvent.setUserMobile(userInfo.getMobile());

                if (reportItem.getStatus() == 0) {//获取信息失败告警
                    warningEvent.setEventDetail(reportItem.getResult().toString());
                    warningService.insertWarningEvent(warningEvent);
                } else {
                    final Status monitorType = new Status();
                    final StringBuffer str = new StringBuffer();//子项 [CPU] [DISK]...
                    final ConcurrentHashMap<String, Status> monitorTypeMap = new ConcurrentHashMap<>();//每台设备的告警类型缓存

                    List<WarningItemBean> warningItemList = WarningItemPools.getInstances().getAll();//所有告警项
                    warningItemList.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(warningItem -> {
                        warningEvent.setEventType(warningItem.getWarningLevel());//设置告警级别
                        String[] monitorIds = warningItem.getMonitorIds().split(Resources.SPLITE, -1);
                        if (new ArrayList<>(Arrays.asList(monitorIds)).contains(reportItem.getMonitorId())) {
                            str.append("[" + reportItem.getMonitorItemType().name() + "]");
                            creatMonitorItem(monitorTypeMap, warningItem, warningEvent, reportItem, monitorType);
                        }
                    });
                    monitorType.setDeviceId(reportItem.getMonitorType().getName());//类型中文名称
                    monitorType.setWarnMessage(str.toString());
                    monitorType.setMessage(monitorTypeMap);
                    map.put(reportItem.getMonitorType().name(), monitorType);//根据监控类型更新
                }

//                        Status monitorType = map.get(reportItem.getMonitorType().name());
//                        final StringBuffer str = new StringBuffer();//子项 [CPU] [DISK]...
//                        if (!ObjectUtils.isEmpty(monitorType.getWarnMessage())) {
//                            str.append(monitorType.getWarnMessage());
//                        }
//                        final ConcurrentHashMap<String, Status> monitorTypeMap = new ConcurrentHashMap<>();//每台设备的告警类型缓存
//                        final ConcurrentHashMap<String, Status> oldMap = (ConcurrentHashMap<String, Status>) monitorType.getMessage();
//                        if (!ObjectUtils.isEmpty(oldMap)) {
//                            monitorTypeMap.putAll(oldMap);
//                        }
            });
            DeviceStatusPools.getInstances().update(report.getDeviceId(), map);//更新设备状态缓存池
            System.err.println(map);
        }
    }

    private static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    private void creatMonitorItem(ConcurrentHashMap<String, Status> monitorTypeMap, WarningItemBean warningItem, WarningEvent warningEvent, ReportItem reportItem, Status monitorType) {
        //生成告警事件
        System.err.println("report Item is " + reportItem);
        switch (reportItem.getMonitorItemType().name()) {
            case "CPU": {
                Status monitorItemType = new Status();
                if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                    warningEvent.setEventDetail(reportItem.getItem() + "[" + reportItem.getResult() + Resources.PERCENTAGE + "]" + "超出阈值[" + warningItem.getThreadHold() + Resources.PERCENTAGE + "]");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage(reportItem.getResult() + Resources.PERCENTAGE);
                }
                if (logger.isDebugEnabled()) {

                    System.err.println("item is " + Objects.toString(monitorItemType));
                }
                monitorTypeMap.put("CPU", monitorItemType);
                break;
            }
            case "DISK": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> DISKMap = new ConcurrentHashMap<>();

                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (warningItem.getThreadHold() < Integer.parseInt(next.getString("used"))) {
                        warningEvent.setEventDetail("[" + next.getString("total") + "]" + reportItem.getItem() + "[" + next.getString("used") + Resources.PERCENTAGE + "]" + "超出阈值[" + warningItem.getThreadHold() + Resources.PERCENTAGE + "]");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("[" + next.getString("total") + "]" + reportItem.getItem() + "[" + next.getString("used") + Resources.PERCENTAGE + "]");
                    }
                    DISKMap.putIfAbsent(next.getString("total"), statusBean);
                }
                monitorItemType.setMessage(DISKMap);
                if (logger.isDebugEnabled()) {
                    System.err.println("item is " + Objects.toString(monitorItemType));
                }
                monitorTypeMap.put("DISK", monitorItemType);
                break;
            }
            case "MEMORY": {
                Status monitorItemType = new Status();
                if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                    warningEvent.setEventDetail(reportItem.getItem() + "[" + reportItem.getResult() + Resources.PERCENTAGE + "]" + "超出阈值[" + warningItem.getThreadHold() + Resources.PERCENTAGE + "]");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage(reportItem.getItem() + "[" + reportItem.getResult() + Resources.PERCENTAGE + "]");
                }
                monitorTypeMap.put("MEMORY", monitorItemType);
                break;
            }
            case "USER": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> USERMap = new ConcurrentHashMap<>();

                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (Integer.parseInt(next.getString("status")) == 0) {
                        warningEvent.setEventDetail("用户[" + next.getString("userId") + "]过期");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("用户[" + next.getString("userId") + "]正常");
                    }
                    USERMap.putIfAbsent(next.getString("userId"), statusBean);
                }
                monitorItemType.setMessage(USERMap);
                monitorTypeMap.put("USER", monitorItemType);
                break;
            }
            case "RAID": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> RAIDMap = new ConcurrentHashMap<>();
                VmInfoBean vmInfo = (VmInfoBean) reportItem.getResult();

                ConcurrentHashMap<String, Status> diskInfoMap = new ConcurrentHashMap<>();
                Status diskInfoStatusBean = new Status();
                vmInfo.getDiskInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(diskInfo -> {
                    Status diskInfoStatus = new Status();
                    if (Integer.parseInt(diskInfo.getStatus()) == 0) {
                        warningEvent.setEventDetail("虚拟机[" + diskInfo.getId() + "]磁盘异常");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        diskInfoStatusBean.setStatus(0);
                        diskInfoStatus.setStatus(0);
                        diskInfoStatus.setMessage(warningEvent.getEventDetail());
                    } else {
                        diskInfoStatus.setMessage("虚拟机[" + diskInfo.getId() + "]磁盘正常");
                    }
                    diskInfoMap.putIfAbsent(diskInfo.getId(), diskInfoStatus);
                });
                diskInfoStatusBean.setMessage(diskInfoMap);
                RAIDMap.putIfAbsent("diskInfos", diskInfoStatusBean);

                ConcurrentHashMap<String, Status> domainInfoMap = new ConcurrentHashMap<>();
                Status domainInfoStatusBean = new Status();
                vmInfo.getDomainInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(domainInfo -> {
                    Status domainInfoStatus = new Status();
                    if (Integer.parseInt(domainInfo.getState()) == 0) {
                        warningEvent.setEventDetail("虚拟机[" + domainInfo.getName() + "]域异常");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        domainInfoStatusBean.setStatus(0);
                        domainInfoStatus.setStatus(0);
                        domainInfoStatus.setMessage(warningEvent.getEventDetail());
                    } else {
                        domainInfoStatus.setMessage("虚拟机[" + domainInfo.getName() + "]域正常");
                    }
                    domainInfoMap.putIfAbsent(domainInfo.getName(), domainInfoStatus);
                });
                domainInfoStatusBean.setMessage(domainInfoMap);
                RAIDMap.putIfAbsent("domainInfos", domainInfoStatusBean);

                ConcurrentHashMap<String, Status> volumeInfoMap = new ConcurrentHashMap<>();
                Status volumeInfoStatusBean = new Status();
                vmInfo.getVolumeInfos().stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(volumeInfo -> {
                    Status volumeInfoStatus = new Status();
                    if (Integer.parseInt(volumeInfo.getStatus()) == 0) {
                        warningEvent.setEventDetail("虚拟机[" + volumeInfo.getName() + "]卷异常");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        volumeInfoStatusBean.setStatus(0);
                        volumeInfoStatus.setStatus(0);
                        volumeInfoStatus.setMessage(warningEvent.getEventDetail());
                    } else {
                        volumeInfoStatus.setMessage("虚拟机[" + volumeInfo.getName() + "]卷正常");
                    }
                    volumeInfoMap.putIfAbsent(volumeInfo.getId(), volumeInfoStatus);
                });
                volumeInfoStatusBean.setMessage(volumeInfoMap);
                RAIDMap.putIfAbsent("volumeInfos", volumeInfoStatusBean);

                Status controllerInfoStatusBean = new Status();
                controllerInfoStatusBean.setMessage(vmInfo.getControllerInfos());
                RAIDMap.putIfAbsent("controllerInfos", controllerInfoStatusBean);

                monitorItemType.setMessage(RAIDMap);
                monitorTypeMap.put("RAID", monitorItemType);
                break;
            }
            case "SYSTEMTYPE": {
                Status monitorItemType = new Status();
                monitorItemType.setMessage(reportItem.getResult());
                monitorTypeMap.put("SYSTEMTYPE", monitorItemType);
                break;
            }
            case "SYSTEMVERSION": {
                Status monitorItemType = new Status();
                monitorItemType.setMessage(reportItem.getResult());
                monitorTypeMap.put("SYSTEMVERSION", monitorItemType);
                break;
            }
            case "SYSTEMPATCH": {
                Status monitorItemType = new Status();
                monitorItemType.setMessage(reportItem.getResult());
                monitorTypeMap.put("SYSTEMPATCH", monitorItemType);
                break;
            }
            case "APPLICATION": {
                Status monitorItemType = new Status();
                if (reportItem.getResult().toString().equalsIgnoreCase("0")) {
                    warningEvent.setEventDetail("通道[" + reportItem.getItem() + "]异常");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage("通道[" + reportItem.getItem() + "]正常");
                }
                monitorTypeMap.put("APPLICATION", monitorItemType);
                break;
            }
            case "INSTANCES_WEB70": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> WEB70Map = new ConcurrentHashMap<>();
                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
//                                            JSONObject json = (JSONObject) JSON.toJSON(reportItem.getResult());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("message").equalsIgnoreCase("实例已停止")) {
                        warningEvent.setEventDetail("实例_WEB70[" + next.getString("ports") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("实例_WEB70[" + next.getString("ports") + "]正常");
                    }
                    WEB70Map.putIfAbsent(next.getString("ports"), statusBean);
                }
                monitorItemType.setMessage(WEB70Map);
                monitorTypeMap.put("INSTANCES_WEB70", monitorItemType);
                break;
            }
            case "INSTANCES_WEBPROXY40": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> WEB70Map = new ConcurrentHashMap<>();
                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
//                                            JSONObject json = (JSONObject) JSON.toJSON(reportItem.getResult());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("message").equalsIgnoreCase("实例已停止")) {
                        warningEvent.setEventDetail("实例_WEBPROXY40[" + next.getString("ports") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("实例_WEBPROXY40[" + next.getString("ports") + "]正常");
                    }
                    WEB70Map.putIfAbsent(next.getString("ports"), statusBean);
                }
                monitorItemType.setMessage(WEB70Map);
                monitorTypeMap.put("INSTANCES_WEBPROXY40", monitorItemType);
                break;
            }
            case "INSTANCES_CONFIG": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> WEB70Map = new ConcurrentHashMap<>();
                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("message").equalsIgnoreCase("实例已停止")) {
                        warningEvent.setEventDetail("实例_配置[" + next.getString("ports") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("实例_配置[" + next.getString("ports") + "]正常");
                    }
                    WEB70Map.putIfAbsent(next.getString("ports"), statusBean);
                }
                monitorItemType.setMessage(WEB70Map);
                monitorTypeMap.put("INSTANCES_CONFIG", monitorItemType);
                break;
            }
            case "INSTANCES_USER": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> WEB70Map = new ConcurrentHashMap<>();
                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("message").equalsIgnoreCase("实例已停止")) {
                        warningEvent.setEventDetail("实例_用户[" + next.getString("ports") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("实例_用户[" + next.getString("ports") + "]正常");
                    }
                    WEB70Map.putIfAbsent(next.getString("ports"), statusBean);
                }
                monitorItemType.setMessage(WEB70Map);
                monitorTypeMap.put("INSTANCES_USER", monitorItemType);
                break;
            }
            case "NETWORK": {
                Status monitorItemType = new Status();
                if (!reportItem.getResult().toString().equalsIgnoreCase("网络正常")) {
                    warningEvent.setEventDetail("网络[" + reportItem.getItem() + "]异常");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage("网络[" + reportItem.getItem() + "]正常");
                }
                monitorTypeMap.put("NETWORK", monitorItemType);
                break;
            }
            case "ACCESSLOG": {
                Status monitorItemType = new Status();
                if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                    warningEvent.setEventDetail(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]" + "超出阈值[" + warningItem.getThreadHold() + "]");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]");
                }
                monitorTypeMap.put("ACCESSLOG", monitorItemType);
                break;
            }
            case "PROXYLOG": {
                Status monitorItemType = new Status();
                if (warningItem.getThreadHold() < Integer.parseInt(reportItem.getResult().toString())) {
                    warningEvent.setEventDetail(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]" + "超出阈值[" + warningItem.getThreadHold() + "]");
                    warningService.insertWarningEvent(warningEvent);

                    monitorType.setStatus(0);
                    monitorItemType.setStatus(0);
                    monitorItemType.setMessage(warningEvent.getEventDetail());
                } else {
                    monitorItemType.setMessage(reportItem.getItem() + "异常数[" + reportItem.getResult() + "]");
                }
                monitorTypeMap.put("PROXYLOG", monitorItemType);
                break;
            }
            case "CERT70": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> CERT70Map = new ConcurrentHashMap<>();
                List<CertInfoBean> list = (List<CertInfoBean>) reportItem.getResult();
                list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(certInfo -> {
                    Status statusBean = new Status();
                    if (certInfo.getStatus() == 0) {
                        warningEvent.setEventDetail("证书70[" + certInfo.getNickname() + "]异常");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("证书70[" + certInfo.getNickname() + "]正常");
                    }
                    CERT70Map.putIfAbsent(certInfo.getNickname(), statusBean);
                });

                monitorItemType.setMessage(CERT70Map);
                monitorTypeMap.put("CERT70", monitorItemType);
                break;
            }
            case "CERT40": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> CERT40Map = new ConcurrentHashMap<>();
                List<CertInfoBean> list = (List<CertInfoBean>) reportItem.getResult();
                list.stream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(certInfo -> {
                    Status statusBean = new Status();
                    if (certInfo.getStatus() == 0) {
                        warningEvent.setEventDetail("证书40[" + certInfo.getNickname() + "]异常");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("证书40[" + certInfo.getNickname() + "]正常");
                    }
                    CERT40Map.putIfAbsent(certInfo.getNickname(), statusBean);
                });

                monitorItemType.setMessage(CERT40Map);
                monitorTypeMap.put("CERT40", monitorItemType);
                break;
            }
            case "IMSERVICE": {
                Status monitorItemType = new Status();
                ConcurrentHashMap<String, Status> IMMap = new ConcurrentHashMap<>();
                JSONArray jSONArray = JSON.parseArray(reportItem.getResult().toString());
                for (Iterator<Object> iterator = jSONArray.iterator(); iterator.hasNext(); ) {
                    Status statusBean = new Status();
                    JSONObject next = (JSONObject) iterator.next();
                    if (next.getString("message").equalsIgnoreCase("已停止")) {
                        warningEvent.setEventDetail("IM进程[" + next.getString("processorName") + "]已停止");
                        warningEvent.setId(null);
                        warningService.insertWarningEvent(warningEvent);

                        monitorType.setStatus(0);
                        monitorItemType.setStatus(0);
                        statusBean.setStatus(0);
                        statusBean.setMessage(warningEvent.getEventDetail());
                    } else {
                        statusBean.setMessage("IM进程[" + next.getString("processorName") + "]正在运行");
                    }
                    IMMap.putIfAbsent(next.getString("processorName"), statusBean);
                }

                monitorItemType.setMessage(IMMap);
                monitorTypeMap.put("IMSERVICE", monitorItemType);
                break;
            }
        }
    }
}
