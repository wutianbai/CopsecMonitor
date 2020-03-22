package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.UserPools;
import com.copsec.monitor.web.fileReaders.BaseFileReader;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class UserFileReader extends BaseFileReader<UserBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream()
                    .filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 5)
                    .forEach(item -> {

                        String[] datas = item.trim().split(Resources.SPLITER);
                        UserBean bean = new UserBean();
                        bean.setId(datas[0]);
                        bean.setPassword(datas[1]);
                        bean.setName(datas[2]);
                        bean.setRole(datas[3]);
                        bean.setStatus(datas[4]);
                        UserPools.getInstances().update(bean);
                    });
        }
    }
}
