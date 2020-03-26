package com.copsec.monitor.web.service.serviceImpl;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.commons.CopsecResult;
import com.copsec.monitor.web.config.SystemConfig;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.*;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.FileReaderType;
import com.copsec.monitor.web.pools.*;
import com.copsec.monitor.web.service.MonitorService;
import com.copsec.monitor.web.utils.logUtils.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
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

    @Override
    public CopsecResult getAllMonitorItem() {
        MonitorItemPools.getInstances().clean();
        try {
            monitorItemReader.getData(config.getBasePath() + config.getMonitorItemPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

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
            return CopsecResult.failed("此设备无监控任务");
        }
        MonitorGroupBean monitorGroupBean = MonitorGroupPools.getInstances().get(monitorTaskBean.getGroupId());
        if (ObjectUtils.isEmpty(monitorGroupBean)) {
            return CopsecResult.failed("此设备无监控组");
        }

        return CopsecResult.success(MonitorItemPools.getInstances().get(monitorGroupBean.getMonitorItems()));
    }

    @Override
    public CopsecResult addMonitorItem(UserBean user, String ip, MonitorItemBean bean, String filePath) {
        try {
            bean.setMonitorType(bean.getMonitorItemType().getType());
            MonitorItemPools.getInstances().add(bean);

            MonitorItemReader reader = (MonitorItemReader) FileReaderFactory.getFileReader(FileReaderType.MONITORITEM);

            reader.writeDate(MonitorItemPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "添加监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控项");

            return CopsecResult.success("添加成功", bean);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorItemPools.getInstances().delete(bean.getMonitorId());
            LogUtils.sendFailLog(user.getId(), ip, "添加监控项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控项");
            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult updateMonitorItem(UserBean user, String ip, MonitorItemBean bean, String filePath) {
        MonitorItemBean oldBean = MonitorItemPools.getInstances().get(bean.getMonitorId());
        try {
            bean.setMonitorType(bean.getMonitorItemType().getType());
            MonitorItemPools.getInstances().update(bean);
            MonitorItemReader reader = (MonitorItemReader) FileReaderFactory.getFileReader(FileReaderType.MONITORITEM);
            reader.writeDate(MonitorItemPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "更新监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控项");

            return CopsecResult.success("更新成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorItemPools.getInstances().update(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "更新监控项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控项");
            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteMonitorItem(UserBean user, String ip, String monitorId, String filePath) {
        MonitorItemBean oldBean = MonitorItemPools.getInstances().get(monitorId);
        try {
            StringBuilder groupStr = checkMonitorGroup(monitorId);
            if (!"".equals(groupStr.toString())) {
                return CopsecResult.failed("不可删除", "监控组" + groupStr.toString() + "包含此项");
            }

            StringBuilder warningStr = checkWarningItem(monitorId);
            if (!"".equals(warningStr.toString())) {
                return CopsecResult.failed("不可删除", "告警项" + warningStr.toString() + "包含此项");
            }

            MonitorItemPools.getInstances().delete(monitorId);
            MonitorItemReader reader = (MonitorItemReader) FileReaderFactory.getFileReader(FileReaderType.MONITORITEM);
            reader.writeDate(MonitorItemPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控项");

            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorItemPools.getInstances().add(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "删除监控项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控项");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
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
        List<MonitorItemBean> oldBeanList = MonitorItemPools.getInstances().get(idArray);

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

        try {
            MonitorItemReader reader = (MonitorItemReader) FileReaderFactory.getFileReader(FileReaderType.MONITORITEM);
            reader.writeDate(MonitorItemPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控项");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorItemPools.getInstances().add(oldBeanList);
            LogUtils.sendFailLog(user.getId(), ip, "删除所选监控项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控项");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult getAllMonitorGroup() {
        MonitorGroupPools.getInstances().clean();
        try {
            monitorGroupReader.getData(config.getBasePath() + config.getMonitorGroupPath());
            monitorItemReader.getData(config.getBasePath() + config.getMonitorItemPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

        ArrayList<Object> list = new ArrayList<>();
        list.add(MonitorGroupPools.getInstances().getAll());
        list.add(MonitorItemPools.getInstances().getAll());

        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addMonitorGroup(UserBean user, String ip, MonitorGroupBean bean, String filePath) {
        try {
            MonitorGroupPools.getInstances().add(bean);

            MonitorGroupReader reader = (MonitorGroupReader) FileReaderFactory.getFileReader(FileReaderType.MONITORGROUP);

            reader.writeDate(MonitorGroupPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "添加监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控组");

            return CopsecResult.success("添加成功", bean);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorGroupPools.getInstances().delete(bean.getId());
            LogUtils.sendFailLog(user.getId(), ip, "添加监控组失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控组");
            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult updateMonitorGroup(UserBean user, String ip, MonitorGroupBean bean, String filePath) {
        MonitorGroupBean oldBean = MonitorGroupPools.getInstances().get(bean.getId());
        try {
            MonitorGroupPools.getInstances().update(bean);

            MonitorGroupReader reader = (MonitorGroupReader) FileReaderFactory.getFileReader(FileReaderType.MONITORGROUP);

            reader.writeDate(MonitorGroupPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "更新监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控组");

            return CopsecResult.success("更新成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorGroupPools.getInstances().update(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "更新监控组失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控组");
            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
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

        MonitorGroupBean oldBean = MonitorGroupPools.getInstances().get(id);
        try {
            MonitorGroupPools.getInstances().delete(id);
            MonitorGroupReader reader = (MonitorGroupReader) FileReaderFactory.getFileReader(FileReaderType.MONITORGROUP);
            reader.writeDate(MonitorGroupPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控组");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorGroupPools.getInstances().add(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "删除监控组失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控组");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteMonitorGroupList(UserBean user, String ip, List<String> idArray, String filePath) {
        final StringBuilder taskStr = new StringBuilder();
        idArray.forEach(id -> taskStr.append(checkMonitorTask(id)));
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务包含其中监控组");
        }

        List<MonitorGroupBean> oldBeanList = MonitorGroupPools.getInstances().get(idArray);
        MonitorGroupPools.getInstances().delete(idArray);

        try {
            MonitorGroupReader reader = (MonitorGroupReader) FileReaderFactory.getFileReader(FileReaderType.MONITORGROUP);
            reader.writeDate(MonitorGroupPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控组成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控组");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorGroupPools.getInstances().add(oldBeanList);
            LogUtils.sendFailLog(user.getId(), ip, "删除所选监控组失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控组");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult getAllWarningItem() {
        WarningItemPools.getInstances().clean();
        try {
            warningItemReader.getData(config.getBasePath() + config.getWarningItemPath());
            monitorItemReader.getData(config.getBasePath() + config.getMonitorItemPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

        ArrayList<Object> list = new ArrayList<>();
        list.add(WarningItemPools.getInstances().getAll());
        list.add(MonitorItemEnum.containVal());
        list.add(WarningLevel.containVal());
        list.add(MonitorItemPools.getInstances().getAll());

        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addWarningItem(UserBean user, String ip, WarningItemBean bean, String filePath) {
        try {
            WarningItemPools.getInstances().add(bean);
            WarningItemReader reader = (WarningItemReader) FileReaderFactory.getFileReader(FileReaderType.WARNINGITEM);
            reader.writeDate(WarningItemPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "添加告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加告警项");

            return CopsecResult.success("添加成功", bean);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            WarningItemPools.getInstances().delete(bean.getWarningId());
            LogUtils.sendFailLog(user.getId(), ip, "添加告警项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加告警项");
            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult updateWarningItem(UserBean user, String ip, WarningItemBean bean, String filePath) {
        WarningItemBean oldBean = WarningItemPools.getInstances().get(bean.getWarningId());
        try {
            WarningItemPools.getInstances().update(bean);

            WarningItemReader reader = (WarningItemReader) FileReaderFactory.getFileReader(FileReaderType.WARNINGITEM);

            reader.writeDate(WarningItemPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "更新告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新告警项");

            return CopsecResult.success("更新成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            WarningItemPools.getInstances().update(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "更新告警项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新告警项");
            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
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

        WarningItemBean oldBean = WarningItemPools.getInstances().get(id);
        try {
            WarningItemPools.getInstances().delete(id);

            WarningItemReader reader = (WarningItemReader) FileReaderFactory.getFileReader(FileReaderType.WARNINGITEM);

            reader.writeDate(WarningItemPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "删除告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除告警项");

            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            WarningItemPools.getInstances().add(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "删除告警项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除告警项");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteWarningItemList(UserBean user, String ip, List<String> idArray, String filePath) {
        final StringBuilder taskStr = new StringBuilder();
        idArray.forEach(id -> taskStr.append(checkMonitorTaskByWarning(id)));
        if (!"".equals(taskStr.toString())) {
            return CopsecResult.failed("不可删除", "监控任务包含其中告警项");
        }

        List<WarningItemBean> oldBeanList = WarningItemPools.getInstances().get(idArray);
        WarningItemPools.getInstances().delete(idArray);

        try {
            WarningItemReader reader = (WarningItemReader) FileReaderFactory.getFileReader(FileReaderType.WARNINGITEM);
            reader.writeDate(WarningItemPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除所选告警项成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选告警项");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            WarningItemPools.getInstances().add(oldBeanList);
            LogUtils.sendFailLog(user.getId(), ip, "删除所选告警项失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选告警项");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult getAllMonitorTask() {
        MonitorTaskPools.getInstances().clean();
        try {
            monitorTaskReader.getData(config.getBasePath() + config.getMonitorTaskPath());
            monitorGroupReader.getData(config.getBasePath() + config.getMonitorGroupPath());
            warningItemReader.getData(config.getBasePath() + config.getWarningItemPath());
        } catch (CopsecException e) {
            logger.error(e.getMessage(), e);
            return CopsecResult.failed(e.getMessage());
        }

        ArrayList<Object> list = new ArrayList<>();
        list.add(MonitorTaskPools.getInstances().getAll());
        list.add(DevicePools.getInstance().getAll());
        list.add(MonitorGroupPools.getInstances().getAll());
        list.add(WarningItemPools.getInstances().getAll());

        return CopsecResult.success(list);
    }

    @Override
    public CopsecResult addMonitorTask(UserBean user, String ip, MonitorTaskBean bean, String filePath) {
        try {
            MonitorTaskPools.getInstances().add(bean);

            MonitorTaskReader reader = (MonitorTaskReader) FileReaderFactory.getFileReader(FileReaderType.MONITORTASK);

            reader.writeDate(MonitorTaskPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "添加监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控任务");

            return CopsecResult.success("添加成功", bean);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorTaskPools.getInstances().delete(bean.getTaskId());
            LogUtils.sendFailLog(user.getId(), ip, "添加监控任务失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "添加监控任务");
            return CopsecResult.failed("添加失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult updateMonitorTask(UserBean user, String ip, MonitorTaskBean bean, String filePath) {
        MonitorTaskBean oldBean = MonitorTaskPools.getInstances().get(bean.getTaskId());
        try {
            MonitorTaskPools.getInstances().update(bean);

            MonitorTaskReader reader = (MonitorTaskReader) FileReaderFactory.getFileReader(FileReaderType.MONITORTASK);

            reader.writeDate(MonitorTaskPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "更新监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控任务");

            return CopsecResult.success("更新成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorTaskPools.getInstances().update(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "更新监控任务失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "更新监控任务");
            return CopsecResult.failed("更新失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteMonitorTask(UserBean user, String ip, String monitorId, String filePath) {
        MonitorTaskBean oldBean = MonitorTaskPools.getInstances().get(monitorId);
        try {
            MonitorTaskPools.getInstances().delete(monitorId);

            MonitorTaskReader reader = (MonitorTaskReader) FileReaderFactory.getFileReader(FileReaderType.MONITORTASK);

            reader.writeDate(MonitorTaskPools.getInstances().getAll(), filePath);

            LogUtils.sendSuccessLog(user.getId(), ip, "删除监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控任务");

            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorTaskPools.getInstances().add(oldBean);
            LogUtils.sendFailLog(user.getId(), ip, "删除监控任务失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除监控任务");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }

    @Override
    public CopsecResult deleteMonitorTaskList(UserBean user, String ip, List<String> idArray, String filePath) {
        List<MonitorTaskBean> oldBeanList = MonitorTaskPools.getInstances().get(idArray);
        MonitorTaskPools.getInstances().delete(idArray);

        try {
            MonitorTaskReader reader = (MonitorTaskReader) FileReaderFactory.getFileReader(FileReaderType.MONITORTASK);
            reader.writeDate(MonitorTaskPools.getInstances().getAll(), filePath);
            LogUtils.sendSuccessLog(user.getId(), ip, "删除所选监控任务成功", config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控任务");
            return CopsecResult.success("删除成功");
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
            MonitorTaskPools.getInstances().add(oldBeanList);
            LogUtils.sendFailLog(user.getId(), ip, "删除所选监控任务失败" + e.getMessage(), config.getLogHost(), config.getLogPort(), config.getLogCollection(), "删除所选监控任务");
            return CopsecResult.failed("删除失败", "文件写入异常，请稍后重试");
        }
    }
}
