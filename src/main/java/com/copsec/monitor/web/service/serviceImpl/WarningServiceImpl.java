package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.beans.node.Status;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.ReportItem;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.WarningHistory;
import com.copsec.monitor.web.handler.ReportHandlerPools;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportBaseHandler;
import com.copsec.monitor.web.handler.ReportItemHandler.ReportHandler;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.deviceStatus.DeviceStatusPools;
import com.copsec.monitor.web.pools.deviceStatus.MonitorItemListPools;
import com.copsec.monitor.web.pools.deviceStatus.MonitorTypePools;
import com.copsec.monitor.web.repository.WarningEventRepository;
import com.copsec.monitor.web.repository.WarningHistoryRepository;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.FormatUtils;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import com.google.common.collect.Lists;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WarningServiceImpl extends ReportBaseHandler implements WarningService {

    private static final Logger logger = LoggerFactory.getLogger(WarningServiceImpl.class);

    @Autowired
    private SystemConfig config;

    @Autowired
    private WarningEventRepository warningEventRepository;

    @Autowired
    private WarningHistoryRepository warningHistoryRepository;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private WarningService warningService;

    @Override
    public Page<WarningEventBean> searchWarningEvent(UserBean userInfo, String ip, Pageable pageable, WarningEventBean condition) {

        Page<WarningEvent> pages = warningEventRepository.findWarningEventByCondition(pageable, condition);

        List<WarningEvent> content = pages.getContent();
        List<WarningEventBean> lists = Lists.newArrayList();
        content.forEach(item -> {
            WarningEventBean bean = new WarningEventBean();
            bean.setEventId(item.getId().toHexString());
//            bean.setEventSource(MonitorItemEnum.valueOf(item.getEventSource()));
            if (ObjectUtils.isEmpty(item.getEventSource())) {
                bean.setEventSource("");
            } else {
                bean.setEventSource(item.getEventSource().getName());
            }

            bean.setEventTime(FormatUtils.getFormatDate(item.getEventTime()));
            bean.setEventDetail(item.getEventDetail());
//            bean.setEventType(WarningLevel.valueOf(item.getEventType()));
            bean.setEventType(item.getEventType().getName());
            bean.setDeviceId(item.getDeviceId());
            bean.setDeviceName(item.getDeviceName());
            bean.setUserId(item.getUserId());
            bean.setUserName(item.getUserName());
            bean.setUserMobile(item.getUserMobile());
            lists.add(bean);
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "查询告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return new PageImpl<>(lists, pages.getPageable(), pages.getTotalElements());
    }

    @Override
    public synchronized List<WarningEvent> findWarningEventByCondition(WarningEventBean condition) {
        return warningEventRepository.findWarningEventByCondition(condition);
    }

    protected void handle(UserBean userInfo, String id) {
        Optional<WarningEvent> optional = warningEventRepository.findById(new ObjectId(id));
        if (optional.isPresent()) {
            WarningEvent status = optional.get();
            WarningHistory warningHistory = new WarningHistory();
            warningHistory.setId(status.getId());
            warningHistory.setEventSource(status.getEventSource());
            warningHistory.setEventTime(status.getEventTime());
            warningHistory.setEventDetail(status.getEventDetail());
            warningHistory.setEventType(status.getEventType());
            warningHistory.setDeviceId(status.getDeviceId());
            warningHistory.setDeviceName(status.getDeviceName());
            warningHistory.setUserId(userInfo.getId());
            warningHistory.setUserName(userInfo.getName());
            warningHistory.setDealTime(new Date());
            warningHistory.setStatus(1);
            warningHistoryRepository.insert(warningHistory);
            warningEventRepository.deleteById(status.getId());
        }
    }

    @Override
    public CopsecResult handleWarningEvent(UserBean userInfo, String ip, WarningEventBean bean) {
        handle(userInfo, bean.getEventId());
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "处理告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("处理告警事件成功");
    }

    @Override
    public CopsecResult deleteWarningEvent(UserBean userInfo, String ip, WarningEventBean bean) {
        Optional<WarningEvent> status = warningEventRepository.findById(new ObjectId((bean.getEventId())));
        if (status.isPresent()) {
            warningEventRepository.deleteById(new ObjectId(bean.getEventId()));
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("删除告警事件成功");
    }

    @Override
    public CopsecResult handleCheckWarningEvent(UserBean userInfo, String ip, List<String> ids) {
        ids.forEach(id -> handle(userInfo, id));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "处理所选告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("处理所选告警事件成功");
    }

    @Override
    public CopsecResult deleteCheckWarningEvent(UserBean userInfo, String ip, List<String> ids) {
        ids.forEach(id -> {
            Optional<WarningEvent> status = warningEventRepository.findById(new ObjectId((id)));
            if (status.isPresent()) {
                warningEventRepository.deleteById(new ObjectId(id));
            }
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除所选告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("删除所选告警事件成功");
    }

    @Override
    public CopsecResult handleAllWarningEvent(UserBean userInfo, String ip) {
        List<WarningEvent> list = warningEventRepository.findAll();
        list.forEach(bean -> handle(userInfo, bean.getId().toString()));
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "处理所有告警事件", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("处理所有告警事件成功");
    }

    @Override
    public void insertWarningEvent(WarningEvent bean) {
        warningEventRepository.insertWarningEvent(bean);
    }

    @Override
    public boolean checkIsWarningByTime(String id) {
        return warningEventRepository.checkIsWarningByTime(id);
    }

    @Override
    public boolean deleteWarningEvent(WarningEvent bean) {
        return warningEventRepository.deleteWarningEvent(bean);
    }

    @Override
    public boolean handleDeviceOutTimeWarning(String deviceId) {
        return warningEventRepository.handleDeviceOutTimeWarning(deviceId);
    }

    @Override
    public Page<WarningHistoryBean> searchWarningHistory(UserBean userInfo, String ip, Pageable pageable, WarningHistoryBean condition) {

        Page<WarningHistory> pages = warningHistoryRepository.findWarningHistoryByCondition(pageable, condition);

        List<WarningHistory> content = pages.getContent();
        List<WarningHistoryBean> lists = Lists.newArrayList();
        content.forEach(item -> {
            WarningHistoryBean bean = new WarningHistoryBean();
            bean.setEventId(item.getId().toHexString());

            if (ObjectUtils.isEmpty(item.getEventSource())) {
                bean.setEventSource("");
            } else {
                bean.setEventSource(item.getEventSource().getName());
            }

            bean.setEventTime(FormatUtils.getFormatDate(item.getEventTime()));
            bean.setEventDetail(item.getEventDetail());
            bean.setEventType(item.getEventType().getName());
            bean.setDeviceId(item.getDeviceId());
            bean.setDeviceName(item.getDeviceName());
            bean.setUserId(item.getUserId());
            bean.setUserName(item.getUserName());
            bean.setDealTime(FormatUtils.getFormatDate(item.getDealTime()));
            bean.setStatus(String.valueOf(item.getStatus()));
            lists.add(bean);
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "查询告警历史", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return new PageImpl<>(lists, pages.getPageable(), pages.getTotalElements());
    }

    @Override
    public CopsecResult deleteWarningHistory(UserBean userInfo, String ip, WarningHistoryBean bean) {
        Optional<WarningHistory> status = warningHistoryRepository.findById(new ObjectId((bean.getEventId())));
        if (status.isPresent()) {
            warningHistoryRepository.deleteById(new ObjectId(bean.getEventId()));
        }
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除告警历史", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("删除告警历史成功");
    }

    @Override
    public CopsecResult deleteCheckWarningHistory(UserBean userInfo, String ip, List<String> ids) {
        ids.forEach(id -> {
            Optional<WarningHistory> status = warningHistoryRepository.findById(new ObjectId((id)));
            if (status.isPresent()) {
                warningHistoryRepository.deleteById(new ObjectId(id));
            }
        });
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除所选告警历史", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("删除所选告警历史成功");
    }

    @Override
    public CopsecResult deleteAllWarningHistory(UserBean userInfo, String ip) {
        warningHistoryRepository.deleteAll();
        LogUtils.sendSuccessLog(userInfo.getId(), ip, "处理所有告警历史", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_WARNING);
        return CopsecResult.success("处理所有告警历史成功");
    }

    @Override
    public void insertWarningHistory(WarningHistory bean) {
        warningHistoryRepository.insertWarningHistory(bean);
    }

    @Override
    public synchronized void receiveWarningEvent(Report report) {
//        LocalCache.putValue(report.getDeviceId(), report, -1);
//        ConcurrentMap<String, CacheEntity> cache = LocalCache.getCache();//获取本地缓存
//        for (Iterator it = cache.entrySet().iterator(); it.hasNext(); ) {
//            Map.Entry entry = (Map.Entry) it.next();
//            CacheEntity cacheEntity = (CacheEntity) entry.getValue();
//            Report report = (Report) cacheEntity.getValue();//取出上报数据
//            LocalCache.remove(report.getDeviceId());//清除缓存

        Device device = DevicePools.getInstance().getDevice(report.getDeviceId());//设备信息
        //更新设备上报时间
        device.getData().setReportTime(report.getReportTime());
        deviceService.updateDevice(new UserBean(), "127.0.0.1", device);
        UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());//运维用户信息

        Status deviceStatus = new Status();
        ConcurrentHashMap<String, Status> monitorTypeMap = MonitorTypePools.getInstances().getMap();

        List<ReportItem> reportItems = report.getReportItems();//获取上报项
        if (!ObjectUtils.isEmpty(reportItems)) {
            reportItems.parallelStream().filter(d -> !ObjectUtils.isEmpty(d)).forEach(reportItem -> {
                Status monitorType = MonitorTypePools.getInstances().get(reportItem.getMonitorType().name());//主类型
                if (ObjectUtils.isEmpty(monitorType)) {
                    monitorType = new Status();
                    MonitorTypePools.getInstances().add(reportItem.getMonitorType().name(), monitorType);
                }

//                        String title = monitorType.getWarnMessage();//标题
//                        if (ObjectUtils.isEmpty(title)) {
//                            title = "";
//                        }
//                        title += "[" + reportItem.getMonitorItemType().name() + "]";

                ConcurrentHashMap<String, List<Status>> monitorItemListMap = (ConcurrentHashMap<String, List<Status>>) monitorType.getMessage();
                if (ObjectUtils.isEmpty(monitorItemListMap)) {
                    monitorItemListMap = MonitorItemListPools.getInstances().getMap();
                    monitorType.setMessage(monitorItemListMap);
                }

                List<Status> monitorItemList = monitorItemListMap.get(reportItem.getMonitorItemType().name());//子状态类型
                if (ObjectUtils.isEmpty(monitorItemList)) {
                    monitorItemList = new ArrayList<>();
                    monitorItemListMap.putIfAbsent(reportItem.getMonitorItemType().name(), monitorItemList);
                }

                Optional<ReportHandler> optional = ReportHandlerPools.getInstance().getHandler(reportItem.getMonitorItemType());
                if (optional.isPresent()) {
                    Status status = optional.get().handle(deviceStatus, device, userInfo, warningService, reportItem, monitorType);
                    monitorItemList.add(status);
                }

                //根据监控类型更新
                monitorType.setDeviceId(reportItem.getMonitorType().getName() + "状态");
//                        monitorType.setWarnMessage(title);
                monitorItemListMap.replace(reportItem.getMonitorItemType().name(), monitorItemList);
                monitorType.setMessage(monitorItemListMap);

                MonitorTypePools.getInstances().update(reportItem.getMonitorType().name(), monitorType);
            });
//            }
        }
        deviceStatus.setDeviceId(report.getDeviceId());
        deviceStatus.setMessage(monitorTypeMap);

        long errorStatus;
        if (!ObjectUtils.isEmpty(report.getReportTime())) {
            deviceStatus.setUpdateTime(FormatUtils.getFormatDate(report.getReportTime()));
            errorStatus = System.currentTimeMillis() - report.getReportTime().getTime();
        } else {
            errorStatus = config.getDeviceUpdateTime() * 30000 + 1;
        }

        if (errorStatus > (config.getDeviceUpdateTime() * 30000)) {//上报超时 并产生告警
            deviceStatus.setWarnMessage(Resources.ERROR_MESSAGE);
            deviceStatus.setStatus(2);

            WarningEvent warningEvent = new WarningEvent();
            warningEvent.setMonitorId(report.getDeviceId());//设置监控ID为设备ID
            warningEvent.setEventType(WarningLevel.ERROR);//初始告警级别
            warningEvent.setEventDetail("上报超时 设备失去连接");
            warningEvent.setEventTime(new Date());
            warningEvent.setDeviceId(report.getDeviceId());
            warningEvent.setDeviceName(device.getData().getDeviceHostname());
            if (!ObjectUtils.isEmpty(device.getData().getMonitorUserId())) {
                warningEvent.setUserId(userInfo.getUserId());
                warningEvent.setUserName(userInfo.getUserName());
                warningEvent.setUserMobile(userInfo.getMobile());
            }

            if (!warningService.checkIsWarningByTime(warningEvent.getMonitorId())) {
                warningService.insertWarningEvent(warningEvent);
            }
        } else if (deviceStatus.getStatus() == 0) {
            deviceStatus.setWarnMessage(Resources.WARNING_MESSAGE);
            warningService.handleDeviceOutTimeWarning(report.getDeviceId());
        } else {
            deviceStatus.setWarnMessage(Resources.NORMAL_MESSAGE);
            warningService.handleDeviceOutTimeWarning(report.getDeviceId());
        }

        DeviceStatusPools.getInstances().update(report.getDeviceId(), deviceStatus);//更新设备状态缓存池
    }
}
