package com.copsec.monitor.web.fileReaders;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.network.NetworkTimingBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class NetworkTimeReader extends BaseFileReader<NetworkTimingBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 4)
                    .forEach(item -> {
                        String[] datas = item.split(Resources.SPLITER);
                        NetworkTimingBean bean = new NetworkTimingBean();
                        bean.setIp(datas[0]);
                        bean.setProtocol(datas[1]);
                        bean.setFrequency(datas[2]);
                        bean.setStatus(datas[3]);
                        CommonPools.getInstances().update(NetworkType.NETWORKTIMESERVICE, JSON.toJSONString(bean));
                    });
        }
    }
}
