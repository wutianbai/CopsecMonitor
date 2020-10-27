package com.copsec.monitor.web.fileReaders;

import com.copsec.monitor.web.beans.monitor.MonitorGroupBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.MonitorGroupPools;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class MonitorGroupReader extends BaseFileReader<MonitorGroupBean> {

    @Override
    public void getData(String filePath) throws CopsecException {
        List<String> list = super.readContent(filePath);
        if (!ObjectUtils.isEmpty(list)) {
            list.stream().
                    filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER, -1).length == 3)
                    .forEach(item -> {
                        String[] dataList = item.trim().split(Resources.SPLITER, -1);
                        MonitorGroupBean bean = new MonitorGroupBean();
                        bean.setId(dataList[0]);
                        bean.setName(dataList[1]);
//                        bean.setMonitorItems(JSONArray.parseArray(dataList[2], MonitorItemBean.class));
                        bean.setMonitorItems(dataList[2].trim());
                        MonitorGroupPools.getInstances().update(bean);
                    });
        }
    }

	@Override
	public void getDataByInfos(String info) {
		String[] dataList = info.trim().split(Resources.SPLITER, -1);
		MonitorGroupBean bean = new MonitorGroupBean();
		bean.setId(dataList[0]);
		bean.setName(dataList[1]);
//                        bean.setMonitorItems(JSONArray.parseArray(dataList[2], MonitorItemBean.class));
		bean.setMonitorItems(dataList[2].trim());
		MonitorGroupPools.getInstances().update(bean);
	}
}
