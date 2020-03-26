package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.beans.monitor.MonitorTaskBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.MonitorTaskPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class MonitorTaskReader extends BaseFileReader<MonitorTaskBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            for (String item : list) {
                if (!ObjectUtils.isEmpty(item)) {

                }
            }

            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER, -1).length == 5)
                    .forEach(item -> {
                        String[] dataList = item.trim().split(Resources.SPLITER, -1);
                        MonitorTaskBean bean = new MonitorTaskBean();
                        bean.setTaskId(dataList[0]);
                        bean.setTaskName(dataList[1]);
                        bean.setDeviceId(dataList[2]);
                        bean.setGroupId(dataList[3]);
                        bean.setWarningItems(dataList[4]);
                        MonitorTaskPools.getInstances().update(bean);
                    });
        }
    }
}
