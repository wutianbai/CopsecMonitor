package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.warning.Report;
import com.copsec.monitor.web.beans.warning.WarningEventBean;
import com.copsec.monitor.web.beans.warning.WarningHistoryBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.WarningHistory;
import com.copsec.monitor.web.repository.WarningEventRepository;
import com.copsec.monitor.web.repository.WarningHistoryRepository;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.FormatUtils;
import com.copsec.monitor.web.utils.LocalCache;
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

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class WarningServiceImpl implements WarningService {

    private static final Logger logger = LoggerFactory.getLogger(WarningServiceImpl.class);

    @Autowired
    private SystemConfig config;

    @Autowired
    private WarningEventRepository warningEventRepository;

    @Autowired
    private WarningHistoryRepository warningHistoryRepository;

    @Override
    public Page<WarningEventBean> searchWarningEvent(UserBean userInfo, String ip, Pageable pageable, WarningEventBean condition) {

        Page<WarningEvent> pages = warningEventRepository.findWarningEventByCondition(pageable, condition);

        List<WarningEvent> content = pages.getContent();
        List<WarningEventBean> lists = Lists.newArrayList();
        content.forEach(item -> {
            WarningEventBean bean = new WarningEventBean();
            bean.setEventId(item.getId().toHexString());
//            bean.setEventSource(MonitorItemEnum.valueOf(item.getEventSource()));
            bean.setEventSource(item.getEventSource().getName());
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
    public List<WarningEvent> findWarningEventByCondition(WarningEventBean condition) {
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
    public boolean deleteWarningEvent(WarningEvent bean) {
        return warningEventRepository.deleteWarningEvent(bean);
    }

    @Override
    public Page<WarningHistoryBean> searchWarningHistory(UserBean userInfo, String ip, Pageable pageable, WarningHistoryBean condition) {

        Page<WarningHistory> pages = warningHistoryRepository.findWarningHistoryByCondition(pageable, condition);

        List<WarningHistory> content = pages.getContent();
        List<WarningHistoryBean> lists = Lists.newArrayList();
        content.forEach(item -> {
            WarningHistoryBean bean = new WarningHistoryBean();
            bean.setEventId(item.getId().toHexString());
            bean.setEventSource(item.getEventSource().getName());
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
    public void insertWarningHistory(WarningHistory bean) {
        warningHistoryRepository.insertWarningHistory(bean);
    }

    @Override
    public synchronized void receiveWarningEvent(Report report) {
//        Report r = new Report();
//        r.setDeviceId("ztxax86ea");
//        r.setReportTime(new Date());
//
//        List<ReportItem> list = new ArrayList<>();
//
//        ReportItem reportItem = new ReportItem();
//        reportItem.setItem("CPU使用率");
//        reportItem.setMonitorId("55B9E274-A25D-4F49-9C74-D81718AA3309");
//        reportItem.setMonitorItemType(MonitorItemEnum.valueOf("CPU"));
//        reportItem.setMonitorType(MonitorTypeEnum.valueOf("SYSTEM"));
//        reportItem.setStatus(1);
//        reportItem.setResult("20");
//
//        ReportItem reportItem1 = new ReportItem();
//        reportItem1.setItem("磁盘使用率");
//        reportItem1.setMonitorId("3B9C9A3E-BBE3-4DAD-8BA8-C17FEB15D90A");
//        reportItem1.setMonitorItemType(MonitorItemEnum.valueOf("DISK"));
//        reportItem1.setMonitorType(MonitorTypeEnum.valueOf("SYSTEM"));
//        reportItem1.setStatus(1);
//        reportItem1.setResult(JSONArray.parse("[{'total':'/usr','used':'50'},{'total':'/var','used':'40'}]"));
//
//        list.add(reportItem);
//        list.add(reportItem1);
//        r.setReportItems(list);

        LocalCache.putValue(report.getDeviceId(), report, -1);
    }
}
