package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.beans.UserBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.UserPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class UserFileReader extends BaseFileReader<UserBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream()
                    .filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 5)
                    .forEach(item -> {

                        String[] dataArray = item.trim().split(Resources.SPLITER);
                        UserBean bean = new UserBean();
                        bean.setId(dataArray[0]);
                        bean.setPassword(dataArray[1]);
                        bean.setName(dataArray[2]);
                        bean.setRole(dataArray[3]);
                        bean.setStatus(dataArray[4]);
                        UserPools.getInstances().update(bean);
                    });
        }
    }
}
