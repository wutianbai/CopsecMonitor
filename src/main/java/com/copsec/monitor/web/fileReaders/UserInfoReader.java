package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.beans.UserInfoBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.UserInfoPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class UserInfoReader extends BaseFileReader<UserInfoBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER, -1).length == 6)
                    .forEach(item -> {
                        String[] dataList = item.trim().split(Resources.SPLITER, -1);
                        UserInfoBean bean = new UserInfoBean();
                        bean.setUserId(dataList[0]);
                        bean.setUserName(dataList[1]);
                        bean.setPassword(dataList[2]);
                        bean.setManufacturerInfo(dataList[3]);
                        bean.setMobile(dataList[4]);
                        bean.setProductionName(dataList[5]);
                        UserInfoPools.getInstances().update(bean);
                    });
        }
    }

	@Override
	public void getDataByInfos(String info) {
		String[] dataList = info.trim().split(Resources.SPLITER, -1);
		UserInfoBean bean = new UserInfoBean();
		bean.setUserId(dataList[0]);
		bean.setUserName(dataList[1]);
		bean.setPassword(dataList[2]);
		bean.setManufacturerInfo(dataList[3]);
		bean.setMobile(dataList[4]);
		bean.setProductionName(dataList[5]);
		UserInfoPools.getInstances().update(bean);
	}
}
