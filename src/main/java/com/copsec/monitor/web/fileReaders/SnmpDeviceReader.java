package com.copsec.monitor.web.fileReaders;

import com.alibaba.fastjson.JSON;
import com.copsec.monitor.web.beans.snmp.SnmpConfigBean;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.snmp.pools.SnmpDevicePool;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Component
public class SnmpDeviceReader extends BaseFileReader<SnmpConfigBean> {

	@Override
	public void getData(String filePath) throws CopsecException {

		List<String> list = super.readContent(filePath);
		if(!ObjectUtils.isEmpty(list)){

			list.stream().forEach(item -> {

				SnmpConfigBean bean = JSON.parseObject(item,SnmpConfigBean.class);
				if(!ObjectUtils.isEmpty(bean)){

					SnmpDevicePool.getInstance().addSnmpConfig(bean);
				}
			});
		}
	}
}
