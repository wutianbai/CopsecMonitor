package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.syslogConf.LogSettingBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.LogSettingPools;

import org.springframework.stereotype.Component;

@Component
public class LogSettingBeanReader extends BaseFileReader<LogSettingBean>{

	@Override
	public void getData(String filePath) throws CopsecException {

		List<String> lines = readContent(filePath);
		lines.stream().filter(d -> {

			if(d.split(Resources.SPLITER).length == 3){

				return true;
			}
			return false;
		}).forEach(line -> {

			String[] attrs = line.split(Resources.SPLITER);
			LogSettingBean bean = new LogSettingBean();
			bean.setLogType(attrs[0]);
			bean.setLogName(attrs[1]);
			bean.setEnable(attrs[2].equals("yes"));
			LogSettingPools.getInstances().update(bean);
		});
	}
}
