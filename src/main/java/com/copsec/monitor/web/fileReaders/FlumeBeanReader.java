package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.flume.FlumeBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.flume.pools.FlumeBeanPool;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 读取FlumeBean 对象配置信息
 */
@Component
public class FlumeBeanReader extends BaseFileReader<FlumeBean> {
    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 7)
                    .forEach(item -> {

                        String[] attrs = item.split(Resources.SPLITER);
                        FlumeBean flume = new FlumeBean();
                        flume.setAgentId(attrs[0]);
                        flume.setSource(attrs[1]);
                        flume.setChannel(attrs[2]);
                        flume.setSink(attrs[3]);
                        flume.setFileName(attrs[4]);
                        flume.setRunId(attrs[5]);
                        flume.setStatus(attrs[6]);
                        FlumeBeanPool.getInstances().add(flume);
                    });
        }
    }
}
