package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.copsec.monitor.web.beans.network.AllowedIpBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.pools.CommonPools;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class IpConfigFileReader extends BaseFileReader<AllowedIpBean>{

	@Override
	public void getData(String filePath) throws CopsecException {

		List<String> list = super.readContent(filePath);
		if(!ObjectUtils.isEmpty(list)){
			list.stream().
					filter(d -> !ObjectUtils.isEmpty(d)&& d.split(Resources.SPLITER).length == 2)
					.forEach(item -> {

						String[] datas = item.split(Resources.SPLITER);
						AllowedIpBean bean = new AllowedIpBean();
						bean.setId(datas[0]);
						bean.setIp(datas[1]);

						CommonPools.getInstances().updateAllowedIp(bean);
					});
		}
	}
}
