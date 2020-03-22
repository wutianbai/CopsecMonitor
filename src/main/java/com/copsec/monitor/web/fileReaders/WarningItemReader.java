package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.WarningLevel;
import com.copsec.monitor.web.beans.monitor.WarningItemBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.WarningItemPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class WarningItemReader extends BaseFileReader<WarningItemBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER, -1).length == 6)
                    .forEach(item -> {
                        String[] dataList = item.trim().split(Resources.SPLITER, -1);
                        WarningItemBean bean = new WarningItemBean();
                        bean.setWarningId(dataList[0]);
                        bean.setWarningName(dataList[1]);
                        bean.setMonitorItemType(MonitorItemEnum.valueOf(dataList[2]));
                        bean.setWarningLevel(WarningLevel.valueOf(dataList[3]));
                        bean.setThreadHold(Integer.parseInt(dataList[4]));
                        bean.setMonitorIds(dataList[5]);
                        WarningItemPools.getInstances().update(bean);
                    });
        }
    }
}
