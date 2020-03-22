package com.copsec.monitor.web.handler.impl;

import com.copsec.monitor.web.beans.syslogParseBeans.DBSynLogBean;
import com.copsec.monitor.web.beans.syslogParseBeans.SyslogMessageBean;
import com.copsec.monitor.web.entity.DBSyncHistoryStatus;
import com.copsec.monitor.web.handler.TaskHistoryStatusHandler;
import com.copsec.monitor.web.pools.TaskStatusHandlerPools;
import com.copsec.monitor.web.repository.DbStatusRepository;
import com.copsec.monitor.web.utils.FormatUtils;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.Calendar;
import java.util.List;

@Component
public class DatabaseTaskStatusHandler implements TaskHistoryStatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseTaskStatusHandler.class);

    private List<String> instancesNames = Lists.newArrayList("数据库数据采集实例"
            , "数据库数据入库实例", "数据库同步集群模式");

    private static final String log_type_for_storage = "解析同步数据";

    private static final String log_type_for_gather = "采集数据";

    private static final String desc_type_for_gather = "生成文件";

    private static final String desc_type_for_storage = "解析完成";

    private static final String storage_suffix = "_back_storage_1";

    private static final String gather_suffix = "_front_collect_1";

    @Autowired
    private DbStatusRepository dbStatusRepository;

    @PostConstruct
    public void init() {

        TaskStatusHandlerPools.getInstance().registerHandler(this);
    }

    @Override
    public void handler(SyslogMessageBean syslogMessageBean) {

        if (canSave(syslogMessageBean.getProperties(), instancesNames)) {

            DBSynLogBean bean = createDBLogBean(syslogMessageBean);

            updateDBSyncHistoryStatus(bean);
        }
    }

    private DBSynLogBean createDBLogBean(SyslogMessageBean syslogMessageBean) {

        DBSynLogBean bean = new DBSynLogBean();
        bean.setDeviceId(syslogMessageBean.getDeviceId());
        bean.setUpdateTime(syslogMessageBean.getUpdateTime());
        syslogMessageBean.getProperties().stream().filter(d -> {

            if (d.split("=").length == 2) {
                return true;
            }
            return false;
        }).forEach(str -> {

            String[] attrs = str.split("=");
            switch (attrs[0]) {

                case "taskName": {
                    String name = attrs[1].replace(gather_suffix, "").replace(storage_suffix, "");
                    bean.setTaskName(name);
                    break;
                }
                case "logType": {

                    bean.setGather(attrs[1].equalsIgnoreCase(log_type_for_gather));
                    break;
                }
                case "desc": {

                    if (attrs[1].startsWith(desc_type_for_storage)) {

                        bean.setInsertCount(FormatUtils.getValue(attrs[1], 1, true));
                        bean.setUpdateCount(FormatUtils.getValue(attrs[1], 2, true));
                        bean.setDeleteCount(FormatUtils.getValue(attrs[1], 3, true));
                    } else if (attrs[1].startsWith(desc_type_for_gather)) {

                        bean.setSum(FormatUtils.getValue(attrs[1], 1, true));
                    }
                    break;
                }
            }
        });
        return bean;
    }

    private void updateDBSyncHistoryStatus(DBSynLogBean bean) {

        if (ObjectUtils.isEmpty(bean) || bean.getTaskName().equals("N/A")) {

            return;
        }
        DBSyncHistoryStatus status = new DBSyncHistoryStatus();
        status.setDeviceId(bean.getDeviceId());
        status.setTaskName(bean.getTaskName());
        status.setUpdateTime(bean.getUpdateTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(bean.getUpdateTime());
        status.setYear(calendar.get(Calendar.YEAR));
        status.setMonth((calendar.get(Calendar.MONTH) + 1));
        status.setDay(calendar.get(Calendar.DAY_OF_MONTH));
        status.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        if (bean.isGather()) {

            status.setGatherInsertCount(bean.getInsertCount());
            status.setGatherUpdateCount(bean.getUpdateCount());
            status.setGatherDeleteCount(bean.getDeleteCount());
            status.setGatherCount(bean.getInsertCount() + bean.getUpdateCount() + bean.getDeleteCount());
        } else {

            status.setStorageCount(bean.getSum());
            status.setStorageInsertCount(bean.getSum());
        }
        dbStatusRepository.updateDBSyncHistoryStatus(status);
    }
}
