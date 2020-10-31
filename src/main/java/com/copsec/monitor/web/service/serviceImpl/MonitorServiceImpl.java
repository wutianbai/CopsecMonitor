package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.beans.node.Device;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.entity.MonitorGroupEntity;
import com.copsec.monitor.web.entity.MonitorItemEntity;
import com.copsec.monitor.web.entity.MonitorTaskEntity;
import com.copsec.monitor.web.entity.WarningEvent;
import com.copsec.monitor.web.entity.WarningItemEntity;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import com.copsec.monitor.web.pools.*;
import com.copsec.monitor.web.repository.MonitorGroupRepository;
import com.copsec.monitor.web.repository.MonitorItemRepository;
import com.copsec.monitor.web.repository.MonitorTaskRepository;
import com.copsec.monitor.web.repository.WarningItemRepository;
import com.copsec.monitor.web.service.MonitorService;
import com.copsec.monitor.web.service.WarningService;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class MonitorServiceImpl implements MonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

    @Autowired
    private SystemConfig config;

    @Autowired
    private MonitorItemReader monitorItemReader;
    @Autowired
    private MonitorGroupReader monitorGroupReader;
    @Autowired
    private MonitorTaskReader monitorTaskReader;
    @Autowired
    private WarningItemReader warningItemReader;
    @Autowired
    private WarningService warningService;

    @Autowired
	private MonitorItemRepository monitorItemRepository;
    @Autowired
	private MonitorGroupRepository monitorGroupRepository;
    @Autowired
	private MonitorTaskRepository monitorTaskRepository;
    @Autowired
	private WarningItemRepository warningItemRepository;

    @Override
    public CopsecResult getAllMonitorItem() {
		MonitorItemPools.getInstances().clean();
		monitorItemRepository.findAll().stream().forEach(m -> monitorItemReader.getDataByInfos(m.getMonitorItemInfo()));
		ArrayList<Object> list = new ArrayList<>();
        list.add(MonitorItemPools.getInstances().getAll());
        list.add(MonitorItemEnum.containVal());
        list.add(MonitorItemEnum.containMessage());
        list.add(MonitorTypeEnum.containVal());

        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult getMonitorItemByDeviceId(String deviceId) {
        MonitorTaskBean monitorTaskBean = MonitorTaskPools.getInstances().getByDeviceId(deviceId);
        if (ObjectUtils.isEmpty(monitorTaskBean)) {
            Device device = DevicePools.getInstance().getDevice(deviceId);

            WarningEvent warningEvent = new WarningEvent();
            warningEvent.setEventType(WarningLevel.ERROR);//初始告警级别
            warningEvent.setEventDetail("监控任务不存在");
            warningEvent.setEventTime(new Date());
            warningEvent.setDeviceId(deviceId);
            warningEvent.setDeviceName(device.getData().getDeviceHostname());

            UserInfoBean userInfo = UserInfoPools.getInstances().get(device.getData().getMonitorUserId());
            if (!ObjectUtils.isEmpty(userInfo)) {
                warningEvent.setUserId(userInfo.getUserId());
                warningEvent.setUserName(userInfo.getUserName());
                warningEvent.setUserMobile(userInfo.getMobile());
            }
            warningService.insertWarningEvent(warningEvent);
            return CopsecResult.failed("此设备无监控任务");
        }

        MonitorGroupBean monitorGroupBean = MonitorGroupPools.getInstances().get(monitorTaskBean.getGroupId());
        if (ObjectUtils.isEmpty(monitorGroupBean)) {
            return CopsecResult.failed("此设备无监控组");
        }

        return CopsecResult.success(MonitorItemPools.getInstances().get(Arrays.asList(monitorGroupBean.getMonitorItems().split(Resources.SPLITE, -1))));
    }

    @Override
    public CopsecResult addMonitorItem(UserBean user, String ip, MonitorItemBean bean, String filePath) {
		bean.setMonitorType(bean.getMonitorItemType().getType());
		MonitorItemPools.getInstances().add(bean);
		MonitorItemEntity monitorItemEntity=new MonitorItemEntity();
		monitorItemEntity.setMonitorId(bean.getMonitorId());
		monitorItemEntity.setMonitorItemInfo(bean.toString());
		monitorItemRepository.save(monitorItemEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "添加监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控项");
		return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateMonitorItem(UserBean user, String ip, MonitorItemBean bean, String filePath) {
		bean.setMonitorType(bean.getMonitorItemType().getType());
		MonitorItemPools.getInstances().update(bean);
		MonitorItemEntity monitorItemEntity=monitorItemRepository.findByMonitorId(bean.getMonitorId());
		monitorItemEntity.setMonitorItemInfo(bean.toString());
		monitorItemRepository.save(monitorItemEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "更新监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控项");
		return CopsecResult.success("更新成功");
    }

    @Override
    public CopsecResult deleteMonitorItem(UserBean user, String ip, String monitorId, String filePath) {
		StringBuilder groupStr = checkMonitorGroup(monitorId);
		if (!"".equals(groupStr.toString())) {
			return CopsecResult.failed("不可删除", "监控组" + groupStr.toString() + "包含此项");
		}

		StringBuilder warningStr = checkWarningItem(monitorId);
		if (!"".equals(warningStr.toString())) {
			return CopsecResult.failed("不可删除", "告警项" + warningStr.toString() + "包含此项");
		}

		MonitorItemPools.getInstances().delete(monitorId);
		monitorItemRepository.deleteByMonitorId(monitorId);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控项");
		return CopsecResult.success("删除成功");
    }

    private StringBuilder checkMonitorGroup(String monitorId) {
        //检查是否有对应监控组
        StringBuilder str = new StringBuilder();
        List<MonitorGroupBean> monitorGroupList = MonitorGroupPools.getInstances().getAll();
        monitorGroupList.forEach(monitorGroup -> {
            List<String> monitorIdList = Arrays.asList(monitorGroup.getMonitorItems().split(",", -1));
            if (monitorIdList.contains(monitorId)) {
                str.append("[" + monitorGroup.getName() + "]");
            }
        });
        return str;
    }

    private StringBuilder checkWarningItem(String monitorId) {
        //检查是否有对应告警项
        StringBuilder str = new StringBuilder();
        List<WarningItemBean> warningItemList = WarningItemPools.getInstances().getAll();
        warningItemList.forEach(warningItem -> {
            List<String> monitorIds = Arrays.asList(warningItem.getMonitorIds().split(",", -1));
            if (monitorIds.contains(monitorId)) {
                str.append("[" + warningItem.getWarningName() + "]");
            }
        });
        return str;
    }

    @Override
    public CopsecResult deleteMonitorItemList(UserBean user, String ip, List<String> idArray, String filePath) {

        final StringBuilder groupStr = new StringBuilder();
        final StringBuilder warningStr = new StringBuilder();
        idArray.forEach(id -> {
            StringBuilder str = checkMonitorGroup(id);
            StringBuilder s = checkWarningItem(id);

            groupStr.append(str);
            warningStr.append(s);
        });
        if (!"".equals(groupStr.toString())) {
            return CopsecResult.failed("不可删除", "监控组包含其中监控项");
        }

        if (!"".equals(warningStr.toString())) {
            return CopsecResult.failed("不可删除", "监控组包含其中监控项");
        }

        MonitorItemPools.getInstances().delete(idArray);
		MonitorItemPools.getInstances().save(monitorItemRepository);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控项");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult getAllMonitorGroup() {

		MonitorGroupPools.getInstances().clean();
		MonitorItemPools.getInstances().clean();
		monitorGroupRepository.findAll().stream().forEach(monitorGroupEntity -> monitorGroupReader.getDataByInfos(monitorGroupEntity.getMonitorGroupInfo()));
		monitorItemRepository.findAll().stream().forEach(monitorItemEntity -> monitorItemReader.getDataByInfos(monitorItemEntity.getMonitorItemInfo()));
		ArrayList<Object> list = new ArrayList<>();
        list.add(MonitorGroupPools.getInstances().getAll());
        list.add(MonitorItemPools.getInstances().getAll());
        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addMonitorGroup(UserBean user, String ip, MonitorGroupBean bean, String filePath) {
		MonitorGroupPools.getInstances().add(bean);
		MonitorGroupEntity monitorGroupEntity=new MonitorGroupEntity();
		monitorGroupEntity.setMonitorGroupId(bean.getId());
		monitorGroupEntity.setMonitorGroupInfo(bean.toString());
		monitorGroupRepository.save(monitorGroupEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "添加监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控组");
		return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateMonitorGroup(UserBean user, String ip, MonitorGroupBean bean, String filePath) {
		MonitorGroupPools.getInstances().update(bean);
		MonitorGroupEntity monitorGroupEntity=monitorGroupRepository.findByMonitorGroupId(bean.getId());
		monitorGroupEntity.setMonitorGroupInfo(bean.toString());
		monitorGroupRepository.save(monitorGroupEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "更新监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控组");
		return CopsecResult.success("更新成功");
    }

    private StringBuilder checkMonitorTask(String monitorGroupId) {
        //检查是否有对应监控任务
        StringBuilder str = new StringBuilder();
        List<MonitorTaskBean> monitorTaskList = MonitorTaskPools.getInstances().getAll();
        monitorTaskList.forEach(monitorTask -> {
            if (monitorGroupId.equalsIgnoreCase(monitorTask.getGroupId())) {
                str.append("[" + monitorTask.getTaskName() + "]");
            }
        });
        return str;
    }

    @Override
    public CopsecResult deleteMonitorGroup(UserBean user, String ip, String id, String filePath) {
        StringBuilder taskStr = checkMonitorTask(id);
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务" + taskStr.toString() + "包含此项");
        }
		MonitorGroupPools.getInstances().delete(id);
		monitorGroupRepository.deleteByMonitorGroupId(id);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控组");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult deleteMonitorGroupList(UserBean user, String ip, List<String> idArray, String filePath) {
        final StringBuilder taskStr = new StringBuilder();
        idArray.forEach(id -> taskStr.append(checkMonitorTask(id)));
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务包含其中监控组");
        }

        MonitorGroupPools.getInstances().delete(idArray);
		MonitorGroupPools.getInstances().save(monitorGroupRepository);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控组");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult getAllWarningItem() {

		WarningItemPools.getInstances().clean();
		MonitorItemPools.getInstances().clean();
		warningItemRepository.findAll().stream().forEach(warningItemEntity -> warningItemReader.getDataByInfos(warningItemEntity.getWarningItemInfo()));
		monitorItemRepository.findAll().stream().forEach(monitorItemEntity -> monitorItemReader.getDataByInfos(monitorItemEntity.getMonitorItemInfo()));
        ArrayList<Object> list = new ArrayList<>();
        list.add(WarningItemPools.getInstances().getAll());
        list.add(MonitorItemEnum.containVal());
        list.add(WarningLevel.containVal());
        list.add(MonitorItemPools.getInstances().getAll());

        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addWarningItem(UserBean user, String ip, WarningItemBean bean, String filePath) {

		WarningItemPools.getInstances().add(bean);
		WarningItemEntity warningItemEntity = new WarningItemEntity();
		warningItemEntity.setWarningItemId(bean.getWarningId());
		warningItemEntity.setWarningItemInfo(bean.toString());
		warningItemRepository.save(warningItemEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "添加告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加告警项");
		return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateWarningItem(UserBean user, String ip, WarningItemBean bean, String filePath) {
		WarningItemPools.getInstances().update(bean);
		WarningItemEntity warningItemEntity=new WarningItemEntity();
		warningItemEntity.setWarningItemInfo(bean.toString());
		warningItemRepository.save(warningItemEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "更新告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新告警项");
		return CopsecResult.success("更新成功");
    }

    private StringBuilder checkMonitorTaskByWarning(String warningId) {
        //检查是否有对应监控组
        StringBuilder str = new StringBuilder();
        List<MonitorTaskBean> monitorTaskList = MonitorTaskPools.getInstances().getAll();
        monitorTaskList.forEach(monitorTask -> {
            List<String> warningIdList = Arrays.asList(monitorTask.getWarningItems().split(",", -1));
            if (warningIdList.contains(warningId)) {
                str.append("[" + monitorTask.getTaskName() + "]");
            }
        });
        return str;
    }

    @Override
    public CopsecResult deleteWarningItem(UserBean user, String ip, String id, String filePath) {
        StringBuilder taskStr = checkMonitorTaskByWarning(id);
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务" + taskStr.toString() + "包含此项");
        }

		WarningItemPools.getInstances().delete(id);
		warningItemRepository.deleteByWarningItemId(id);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除告警项");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult deleteWarningItemList(UserBean user, String ip, List<String> idArray, String filePath) {
        final StringBuilder taskStr = new StringBuilder();
        idArray.forEach(id -> taskStr.append(checkMonitorTaskByWarning(id)));
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务包含其中告警项");
        }

        WarningItemPools.getInstances().delete(idArray);
        WarningItemPools.getInstances().save(warningItemRepository);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除所选告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选告警项");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult getAllMonitorTask() {

		MonitorTaskPools.getInstances().clean();
		MonitorGroupPools.getInstances().clean();
		WarningItemPools.getInstances().clean();
		monitorTaskRepository.findAll().stream().forEach(m -> monitorTaskReader.getDataByInfos(m.getMonitorTaskInfo()));
		monitorGroupRepository.findAll().stream().forEach(m -> monitorGroupReader.getDataByInfos(m.getMonitorGroupInfo()));
		warningItemRepository.findAll().stream().forEach(m -> warningItemReader.getDataByInfos(m.getWarningItemInfo()));
        ArrayList<Object> list = new ArrayList<>();
        list.add(MonitorTaskPools.getInstances().getAll());
        list.add(DevicePools.getInstance().getAll());
        list.add(MonitorGroupPools.getInstances().getAll());
        list.add(WarningItemPools.getInstances().getAll());
        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addMonitorTask(UserBean user, String ip, MonitorTaskBean bean, String filePath) {

		MonitorTaskPools.getInstances().add(bean);
		MonitorTaskEntity monitorTaskEntity = new MonitorTaskEntity();
		monitorTaskEntity.setMonitorTaskId(bean.getTaskId());
		monitorTaskEntity.setMonitorTaskInfo(bean.toString());
		monitorTaskRepository.save(monitorTaskEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "添加监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控任务");
		return CopsecResult.success("添加成功", bean);
    }

    @Override
    public CopsecResult updateMonitorTask(UserBean user, String ip, MonitorTaskBean bean, String filePath) {
		MonitorTaskPools.getInstances().update(bean);
		MonitorTaskEntity monitorTaskEntity = monitorTaskRepository.findByMonitorTaskId(bean.getTaskId());
		monitorTaskEntity.setMonitorTaskInfo(bean.toString());
		monitorTaskRepository.save(monitorTaskEntity);
		LogUtils.sendSuccessLog(user.getId(), ip, "更新监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控任务");
		return CopsecResult.success("更新成功");
    }

    @Override
    public CopsecResult deleteMonitorTask(UserBean user, String ip, String monitorId, String filePath) {
		MonitorTaskPools.getInstances().delete(monitorId);
		monitorTaskRepository.deleteByMonitorTaskId(monitorId);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控任务");
		return CopsecResult.success("删除成功");
    }

    @Override
    public CopsecResult deleteMonitorTaskList(UserBean user, String ip, List<String> idArray, String filePath) {
        MonitorTaskPools.getInstances().delete(idArray);
		MonitorTaskPools.getInstances().save(monitorTaskRepository);
		LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控任务");
		return CopsecResult.success("删除成功");
    }
}
