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
	public void getDataByInfos(String info) {
		String[] dataList = info.trim().split(Resources.SPLITER, -1);
		WarningItemBean bean = new WarningItemBean();
		bean.setWarningId(dataList[0]);
		bean.setWarningName(dataList[1]);
		bean.setMonitorItemType(MonitorItemEnum.valueOf(dataList[2]));
		bean.setWarningLevel(WarningLevel.valueOf(dataList[3]));
		bean.setThreadHold(Integer.parseInt(dataList[4]));
		bean.setMonitorIds(dataList[5].trim());
		WarningItemPools.getInstances().update(bean);
	}
}
