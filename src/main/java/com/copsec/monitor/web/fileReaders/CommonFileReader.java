package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.CommonPools;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * 读取单值属性值
 */
@Component
public class CommonFileReader extends BaseFileReader<String> {
    @Override
    public void getData(String filePath, final NetworkType type) throws CopsecException {

        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            String str = "";
            if (list.size() == 1) {
                CommonPools.getInstances().update(type, list.get(0));
            } else {
                for (String item : list) {
                    str += item + Resources.SPLITER;
                }
                CommonPools.getInstances().update(type, str);
            }
        }
    }
}
