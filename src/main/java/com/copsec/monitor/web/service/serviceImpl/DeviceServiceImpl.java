package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.node.*;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.pools.DevicePools;
import com.copsec.monitor.web.pools.LinkPools;
import com.copsec.monitor.web.pools.UserInfoPools;
import com.copsec.monitor.web.pools.ZonePools;
import com.copsec.monitor.web.pools.deviceStatus.DeviceStatusPools;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.utils.FormatUtils;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Service
public class DeviceServiceImpl implements DeviceService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceServiceImpl.class);

    @Autowired
    private SystemConfig config;
    @Autowired
    private DeviceFileReader deviceReader;
    @Autowired
    private LinkFileReader linkReader;
    @Autowired
    private ZoneFileReader zoneReader;
    @Autowired
    private UserInfoReader userInfoReader;

    @Override
    public CopsecResult getData() {
        DevicePools.getInstance().clean();
        LinkPools.getInstance().clean();
        ZonePools.getInstance().clean();
        UserInfoPools.getInstances().clean();
        try {
            deviceReader.getData(config.getBasePath() + config.getDevicePath());
            linkReader.getData(config.getBasePath() + config.getLinkPath());
            zoneReader.getData(config.getBasePath() + config.getZonePath());
            userInfoReader.getData(config.getBasePath() + config.getUserInfoPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

        List list = new ArrayList<>();
        list.add(DevicePools.getInstance().getAll());
        list.add(LinkPools.getInstance().getAll());
        list.add(ZonePools.getInstance().getAll());
        list.add(UserInfoPools.getInstances().getAll());
        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addDevice(UserBean userInfo, String ip, Device device) {
        try {
            if (!ObjectUtils.isEmpty(DevicePools.getInstance().getDevice(device.getData().getId()))) {
                return CopsecResult.failed("设备ID已存在");
            }

            DevicePools.getInstance().addDevice(device);
            deviceReader.writeDate(DevicePools.getInstance().getAll(), config.getBasePath() + config.getDevicePath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加设备成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDDEVICE);
            return CopsecResult.success("添加成功", device);
        } catch (CopsecException e) {
            DevicePools.getInstance().delete(device.getData().getDeviceId());
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加设备失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDDEVICE);
            logger.error(e.getMessage(), e);
            return CopsecResult.failed("添加失败", "系统异常");
        }
    }

    @Override
    public CopsecResult getDevice(UserBean userInfo, String ip, String deviceId) {
        if (logger.isDebugEnabled()) {
            logger.debug("get device by id {}", deviceId);
        }
        Device device = DevicePools.getInstance().getDevice(deviceId);
        if (!ObjectUtils.isEmpty(device)) {
            return CopsecResult.success(device);
        }
        return CopsecResult.failed("获取设备信息出错，请稍后重试");
    }

    @Override
    public synchronized CopsecResult updateDevice(UserBean userInfo, String ip, Device device) {
        Device oldBean = DevicePools.getInstance().getDevice(device.getData().getDeviceId());
        try {
            DevicePools.getInstance().updateDevice(device);
            deviceReader.writeDate(DevicePools.getInstance().getAll(), config.getBasePath() + config.getDevicePath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新设备成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATEDEVICE);
            return CopsecResult.success("更新成功", device);
        } catch (CopsecException e) {
            DevicePools.getInstance().updateDevice(oldBean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新设备失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATEDEVICE);
            logger.error(e.getMessage(), e);
            return CopsecResult.failed("更新失败", "系统异常");
        }
    }

    @Override
    public CopsecResult deleteDevice(UserBean userInfo, String ip, String deviceId) {
        Device device = DevicePools.getInstance().getDevice(deviceId);
        if (!ObjectUtils.isEmpty(device)) {
            DevicePools.getInstance().delete(deviceId);
            LinkPools.getInstance().deleteLinksByDeviceId(deviceId);
            try {
                deviceReader.writeDate(DevicePools.getInstance().getAll(), config.getBasePath() + config.getDevicePath());
                linkReader.writeDate(LinkPools.getInstance().getAll(), config.getBasePath() + config.getLinkPath());
                LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除设备成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEDEVICE);
                return CopsecResult.success("删除成功");
            } catch (CopsecException e) {
                DevicePools.getInstance().addDevice(device);
                LogUtils.sendFailLog(userInfo.getId(), ip, "删除设备失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEDEVICE);
                logger.error(e.getMessage(), e);
                return CopsecResult.failed("删除失败", "系统异常");
            }
        }
        return CopsecResult.failed();
    }

    @Override
    public CopsecResult addLink(UserBean userInfo, String ip, LinkBean bean) {
        List<Link> list = new ArrayList<>();
        saveLink(list, bean);
        try {
            linkReader.writeDate(LinkPools.getInstance().getAll(), config.getBasePath() + config.getLinkPath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDLINK);
            return CopsecResult.success(list);
        } catch (CopsecException e) {
            LinkPools.getInstance().delete(list);
            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加连接失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDLINK);
            return CopsecResult.failed();
        }
    }

    @Override
    public CopsecResult updateLink(UserBean userInfo, String ip, LinkBean bean) {
        List<Link> list = new ArrayList<>();
        Link oldBean = LinkPools.getInstance().get(bean.getId());
        LinkPools.getInstance().delete(bean.getId());
        saveLink(list, bean);
        try {
            linkReader.writeDate(LinkPools.getInstance().getAll(), config.getBasePath() + config.getLinkPath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATELINK);
            return CopsecResult.success(list);
        } catch (CopsecException e) {
            LinkPools.getInstance().delete(list);
            LinkPools.getInstance().add(oldBean);
            logger.error(e.getMessage(), e);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新连接失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATELINK);
            return CopsecResult.failed();
        }
    }

    private void saveLink(List<Link> list, LinkBean bean) {
        if (!ObjectUtils.isEmpty(bean)) {
            List<String> targetList = Arrays.asList(bean.getTargets());
            targetList.forEach(target -> {
                Link link = new Link();
                Data data = new Data();
                data.setId(UUID.randomUUID().toString());
                data.setSource(bean.getSource());
                data.setTarget(target);
                link.setData(data);
                link.setClasses(bean.getClasses());

                link = LinkPools.getInstance().add(link);
                if (!ObjectUtils.isEmpty(link)) {
                    list.add(link);
                }
            });
        }
    }

    @Override
    public CopsecResult deleteLink(UserBean userInfo, String ip, String linkId) {
        LinkPools.getInstance().delete(linkId);
        try {
            linkReader.writeDate(LinkPools.getInstance().getAll(), config.getBasePath() + config.getLinkPath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETELINK);
            return CopsecResult.success();
        } catch (CopsecException e) {
            LogUtils.sendFailLog(userInfo.getId(), ip, "删除连接失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETELINK);
            logger.error(e.getMessage(), e);
            return CopsecResult.failed("删除连接失败，系统异常");
        }
    }

    @Override
    public CopsecResult getAllZone() {
        return CopsecResult.success(ZonePools.getInstance().getAll());
    }

    @Override
    public CopsecResult addZone(UserBean userInfo, String ip, Device zone) {
        ZonePools.getInstance().add(zone);
        try {
            zoneReader.writeDate(ZonePools.getInstance().getAll(), config.getBasePath() + config.getZonePath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDZONE);
            return CopsecResult.success("添加成功", zone);
        } catch (CopsecException e) {
            ZonePools.getInstance().delete(zone.getData().getId());
            LogUtils.sendFailLog(userInfo.getId(), ip, "添加网络区域失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDZONE);
            logger.error(e.getMessage(), e);
            return CopsecResult.failed("添加失败", "系统异常");
        }
    }

    @Override
    public CopsecResult updateZone(UserBean userInfo, String ip, Device zone) {
        Device oldBean = ZonePools.getInstance().get(zone.getData().getId());
        try {
            ZonePools.getInstance().update(zone);
            zoneReader.writeDate(ZonePools.getInstance().getAll(), config.getBasePath() + config.getZonePath());
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATEZONE);
            return CopsecResult.success("更新成功", zone);
        } catch (CopsecException e) {
            ZonePools.getInstance().update(oldBean);
            LogUtils.sendFailLog(userInfo.getId(), ip, "更新网络区域失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATEZONE);
            logger.error(e.getMessage(), e);
            return CopsecResult.failed("更新失败", "系统异常");
        }
    }

    @Override
    public CopsecResult deleteZone(UserBean userInfo, String ip, String zoneId) {
        Device zone = ZonePools.getInstance().get(zoneId);
        if (!ObjectUtils.isEmpty(zone)) {
            ZonePools.getInstance().delete(zoneId);
            try {
                zoneReader.writeDate(ZonePools.getInstance().getAll(), config.getBasePath() + config.getZonePath());
                LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEZONE);
                return CopsecResult.success("删除成功");
            } catch (CopsecException e) {
                ZonePools.getInstance().add(zone);
                LogUtils.sendFailLog(userInfo.getId(), ip, "删除网络区域失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEZONE);
                logger.error(e.getMessage(), e);
                return CopsecResult.failed("删除失败", "系统异常");
            }
        }
        return CopsecResult.failed();
    }

    @Override
    public CopsecResult updateTopology(ArrayList<PositionBeans> positions) {
        positions.stream().forEach(item -> {
            Device device = DevicePools.getInstance().getDevice(item.getId());
            Device zone = ZonePools.getInstance().get(item.getId());

            Position p = new Position();
            p.setX(item.getX());
            p.setY(item.getY());

            if (!ObjectUtils.isEmpty(device)) {
                device.setPosition(p);
                DevicePools.getInstance().updateDevice(device);
            }
            if (!ObjectUtils.isEmpty(zone)) {
                zone.setPosition(p);
                ZonePools.getInstance().update(zone);
            }
        });

        try {
            deviceReader.writeDate(DevicePools.getInstance().getAll(), config.getBasePath() + config.getDevicePath());
            zoneReader.writeDate(ZonePools.getInstance().getAll(), config.getBasePath() + config.getZonePath());
        } catch (CopsecException e) {
            logger.error("error happened {}", e.getMessage());
            return CopsecResult.failed("系统异常，请稍后重试");
        }

        return CopsecResult.success("更新成功");
    }

    private final ConcurrentHashMap<String, Status> statusMap = new ConcurrentHashMap<>();

    private Status getStatusBean(Object key) {
        Status status = statusMap.get(key.toString());
        if (ObjectUtils.isEmpty(status)) {
            status = new Status();
            statusMap.putIfAbsent((String) key, status);
        }
        return status;
    }

    @Override
    public CopsecResult getDeviceStatus() {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Status>> deviceStatusMap = DeviceStatusPools.getInstances().getMap();
        for (Iterator it = deviceStatusMap.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            ConcurrentHashMap<String, Status> deviceStatus = (ConcurrentHashMap<String, Status>) entry.getValue();

            Status statusBean = getStatusBean(entry.getKey());

            long errorStatus;
//            Device device =  DevicePools.getInstance().getDevice(entry.getKey().toString());
//            updateDevice(new UserBean(), "127.0.0.1", device);//更新后台设备上报时间
            Date updateTime = DevicePools.getInstance().getDevice(entry.getKey().toString()).getData().getReportTime();
            if (ObjectUtils.isEmpty(updateTime)) {
                errorStatus = config.getDeviceUpdateTime() * 30000 + 1;
            } else {
                errorStatus = System.currentTimeMillis() - updateTime.getTime();
            }
            statusBean.setUpdateTime(FormatUtils.getFormatDate(updateTime));

            if (errorStatus > (config.getDeviceUpdateTime() * 30000)) {
                statusBean.setWarnMessage(Resources.ERROR_MESSAGE);
                statusBean.setStatus(2);
            } else if (statusBean.getStatus() == 0) {
                statusBean.setWarnMessage("故障");
                statusBean.setStatus(0);
            } else {
                statusBean.setWarnMessage("健康");
                statusBean.setStatus(1);
            }

            statusBean.setDeviceId(entry.getKey().toString());
            statusBean.setMessage(deviceStatus);

            statusMap.replace(entry.getKey().toString(), statusBean);
        }
        return CopsecResult.success(statusMap);
    }
}

