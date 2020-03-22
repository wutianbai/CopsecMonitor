package com.copsec.monitor.web.fileReaders;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.copsec.monitor.web.beans.network.NetPortBondBean;
import com.copsec.monitor.web.config.Resources;
import com.copsec.monitor.web.exception.CopsecException;
import com.copsec.monitor.web.fileReaders.fileReaderEnum.NetworkType;
import com.copsec.monitor.web.pools.CommonPools;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class BondModeReader extends BaseFileReader<NetPortBondBean> {

	@Override
	public void getData(String filePath, NetworkType type) throws CopsecException {

		List<String> list = super.readContent(filePath);
		if(!ObjectUtils.isEmpty(list)){

			JSONArray array = new JSONArray();
			list.stream().filter(d -> !ObjectUtils.isEmpty(d) && d.split(Resources.SPLITER).length == 3)
					.forEach(i -> {

						String[] datas = i.split(Resources.SPLITER);
						NetPortBondBean bean =new NetPortBondBean();
						bean.setBoundName(datas[0]);
						bean.setPort(datas[1]);
						bean.setMode(datas[2]);
						array.add(bean);
					});
			CommonPools.getInstances().update(type,array.toJSONString());
		}
	}
}
