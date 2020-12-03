package com.copsec.monitor.web.service.serviceImpl;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.beans.node.*;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.DeviceEntity;
import com.copsec.monitor.web.entity.LinkEntity;
import com.copsec.monitor.web.entity.MonitorTaskEntity;
import com.copsec.monitor.web.entity.UserInfoEntity;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.ZoneEntity;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.DeviceFileReader;
import com.copsec.monitor.web.fileReaders.LinkFileReader;
import com.copsec.monitor.web.fileReaders.UserInfoReader;
import com.copsec.monitor.web.fileReaders.ZoneFileReader;
import com.copsec.monitor.web.pools.*;
import com.copsec.monitor.web.pools.deviceStatus.DeviceStatusPools;
import com.copsec.monitor.web.repository.DeviceRepository;
import com.copsec.monitor.web.repository.LinkRepository;
import com.copsec.monitor.web.repository.MonitorTaskRepository;
import com.copsec.monitor.web.repository.UserInfoEntityRepository;
import com.copsec.monitor.web.repository.WarningEventRepository;
import com.copsec.monitor.web.repository.ZoneRepository;
import com.copsec.monitor.web.service.DeviceService;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.krb5.internal.KdcErrException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;


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

    @Autowired
	private DeviceRepository deviceRepository;

    @Autowired
	private LinkRepository linkRepository;

    @Autowired
	private UserInfoEntityRepository userInfoRepository;

    @Autowired
	private ZoneRepository zoneRepository;

    @Autowired
	private WarningEventRepository warningEventRepository;

    @Override
    public CopsecResult getData() {
        List list = new ArrayList<>();
		DevicePools.getInstance().clean();
		LinkPools.getInstance().clean();
		ZonePools.getInstance().clean();
		UserInfoPools.getInstances().clean();
		deviceRepository.findAll().stream().forEach(d -> deviceReader.getDataByInfos(d.getDeviceInfo()));
		zoneRepository.findAll().stream().forEach(zone -> zoneReader.getDataByInfos(zone.getZoneInfo()));
      	linkRepository.findAll().stream().forEach(l -> linkReader.getDataByInfos(l.getLinkInfo()));
       	userInfoRepository.findAll().stream().forEach(u -> userInfoReader.getDataByInfos(u.getUserInfo()));
		list.add(DevicePools.getInstance().getAll());
		list.add(LinkPools.getInstance().getAll());
		list.add(ZonePools.getInstance().getAll());
		list.add(UserInfoPools.getInstances().getAll());
        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addDevice(UserBean userInfo, String ip, Device device) {

            if (!ObjectUtils.isEmpty(DevicePools.getInstance().getDevice(device.getData().getDeviceId()))) {

            	LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加设备失败", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDDEVICE);

				return CopsecResult.failed("设备ID已存在");
            }

            DevicePools.getInstance().addDevice(device);
           	DeviceEntity deviceEntity = new DeviceEntity();
           	deviceEntity.setDeviceId(device.getData().getDeviceId());
			deviceEntity.setDeviceInfo(device.toString());
           	deviceRepository.save(deviceEntity);
            LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加设备成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDDEVICE);
            return CopsecResult.success("添加成功", device);
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
		DevicePools.getInstance().updateDevice(device);
		DeviceEntity entity = deviceRepository.findByDeviceId(device.getData().getDeviceId());
		entity.setDeviceInfo(device.toString());
		deviceRepository.save(entity);
		return CopsecResult.success("更新成功", device);
    }

    private StringBuilder checkMonitorTask(String deviceId) {
        //检查是否有对应监控组
		StringBuilder str = new StringBuilder();
		List<MonitorTaskBean> monitorTaskList = MonitorTaskPools.getInstances().getAll();
		monitorTaskList.forEach(monitorTask -> {
			List<String> deviceIdList = Arrays.asList(monitorTask.getDeviceId().split(",", -1));
			if (deviceIdList.contains(deviceId)) {
				str.append("[" + monitorTask.getTaskName() + "]");
			}
		});
		return str;
    }

    @Override
    public CopsecResult deleteDevice(UserBean userInfo, String ip, String deviceId) {
        StringBuilder taskStr = checkMonitorTask(deviceId);
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务" + taskStr.toString() + "包含此设备");
        }

        Device device = DevicePools.getInstance().getDevice(deviceId);
        if (!ObjectUtils.isEmpty(device)) {
            DevicePools.getInstance().delete(deviceId);
            LinkPools.getInstance().deleteLinksByDeviceId(deviceId);
			deviceRepository.deleteByDeviceId(deviceId);
			LinkPools.getInstance().save(linkRepository);
			LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除设备成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEDEVICE);
			return CopsecResult.success("删除成功");
        }
        return CopsecResult.failed();
    }

    @Override
    public CopsecResult addLink(UserBean userInfo, String ip, LinkBean bean) {
        List<Link> list = new ArrayList<>();
        saveLink(list, bean);
		LinkPools.getInstance().save(linkRepository);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDLINK);
		return CopsecResult.success(list);
    }

    @Override
    public CopsecResult updateLink(UserBean userInfo, String ip, LinkBean bean) {
        List<Link> list = new ArrayList<>();
        LinkPools.getInstance().delete(bean.getId());
        saveLink(list, bean);
		LinkPools.getInstance().save(linkRepository);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATELINK);
		return CopsecResult.success(list);
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
		LinkPools.getInstance().save(linkRepository);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新连接成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETELINK);
		return CopsecResult.success();
    }

    @Override
    public CopsecResult getAllZone() {
        return CopsecResult.success(ZonePools.getInstance().getAll());
    }

    @Override
    public CopsecResult addZone(UserBean userInfo, String ip, Device zone) {
        ZonePools.getInstance().add(zone);
		ZoneEntity zoneEntity = new ZoneEntity();
		zoneEntity.setZoneId(zone.getData().getId());
		zoneEntity.setZoneInfo(zone.toString());
		zoneRepository.save(zoneEntity);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "添加网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_ADDZONE);
		return CopsecResult.success("添加成功", zone);
    }

    @Override
    public CopsecResult updateZone(UserBean userInfo, String ip, Device zone) {
		ZonePools.getInstance().update(zone);
		ZoneEntity zoneEntity = zoneRepository.findByZoneId(zone.getData().getId());
		zoneEntity.setZoneInfo(zone.toString());
		zoneRepository.save(zoneEntity);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "更新网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_UPDATEZONE);
		return CopsecResult.success("更新成功", zone);
    }

    @Override
    public CopsecResult deleteZone(UserBean userInfo, String ip, String zoneId) {
		ZonePools.getInstance().delete(zoneId);
		zoneRepository.deleteByZoneId(zoneId);
		LogUtils.sendSuccessLog(userInfo.getId(), ip, "删除网络区域成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), Resources.OPERATETYPE_DELETEZONE);
		return CopsecResult.success("删除成功");
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

        DevicePools.getInstance().save(deviceRepository);
        ZonePools.getInstance().save(zoneRepository);
        return CopsecResult.success("更新成功");
    }

	/**
	 *  循环设备状态缓存，如果reportTime超时，则从数据库读取数据上报时间进行判断。
	 * @return
	 */
	@Override
    public CopsecResult getDeviceStatus() {

		Map<String,Status> statusMap = DeviceStatusPools.getInstances().getMap();
		statusMap.entrySet().stream().forEach(entry -> {

			Status status = entry.getValue();
			DeviceEntity deviceEntity = deviceRepository.findByDeviceId(entry.getKey());
			if(!ObjectUtils.isEmpty(deviceEntity)){

				Device device = getDeviceInfo(deviceEntity.getDeviceInfo());
				if(!ObjectUtils.isEmpty(device)){

					if(System.currentTimeMillis() - device.getData().getReportTime().getTime() > config.getDeviceUpdateTime()){

						status.setStatus(2);
						WarningEvent warningEvent = new WarningEvent();
						warningEvent.setMonitorId(entry.getKey());//设置监控ID为设备ID
						warningEvent.setEventType(WarningLevel.ERROR);//初始告警级别
						warningEvent.setEventDetail("上报超时 设备失去连接");
						warningEvent.setEventTime(new Date());
						warningEvent.setDeviceId(entry.getKey());
						warningEvent.setDeviceName(device.getData().getDeviceHostname());
						UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());//运维用户信息
						if (!ObjectUtils.isEmpty(userInfo)) {
							warningEvent.setUserId(userInfo.getUserId());
							warningEvent.setUserName(userInfo.getUserName());
							warningEvent.setUserMobile(userInfo.getMobile());
						}
						if(!WarningEventPools.getInstances().exists(warningEvent)){

							WarningEventPools.getInstances().add(warningEvent);
							warningEventRepository.save(warningEvent);
						}
					}
					DeviceStatusPools.getInstances().update(entry.getKey(),status);
				}
			}
		});
        return CopsecResult.success(DeviceStatusPools.getInstances().getMap());
    }

    private Device getDeviceInfo(String info){

		String[] datalist = info.trim().split(Resources.SPLITER, -1);
		if (datalist.length == 2) {
			Device device = new Device();
			device.setData(JSON.parseObject(datalist[0], Data.class));
			device.setPosition(JSON.parseObject(datalist[1],Position.class));
			return device;
		}
		return null;
	}
}

