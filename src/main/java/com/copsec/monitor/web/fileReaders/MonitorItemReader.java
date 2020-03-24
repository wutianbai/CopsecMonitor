package com.copsec.monitor.web.fileReaders;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorItemEnum;
import com.copsec.monitor.web.beans.monitor.MonitorEnum.MonitorTypeEnum;
import com.copsec.monitor.web.beans.monitor.MonitorItemBean;
import com.copsec.monitor.web.beans.warning.CertConfig;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.MonitorItemPools;
import com.copsec.monitor.web.utils.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class MonitorItemReader extends BaseFileReader<MonitorItemBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER, -1).length == 5)
                    .forEach(item -> {
                        String[] dataList = item.trim().split(Resources.SPLITER, -1);
                        MonitorItemBean bean = new MonitorItemBean();
                        bean.setMonitorId(dataList[0]);
                        bean.setMonitorName(dataList[1]);
                        bean.setMonitorItemType(MonitorItemEnum.valueOf(dataList[2]));
                        bean.setMonitorType(MonitorTypeEnum.valueOf(dataList[3]));
                        if (CommonUtils.isJSONValid(dataList[4]) && !"".equals(dataList[4])) {
                            bean.setItem(JSON.parseObject(dataList[4], CertConfig.class));
                        } else {
                            bean.setItem(dataList[4]);
                        }
                        MonitorItemPools.getInstances().update(bean);
                    });
        }
    }
}
